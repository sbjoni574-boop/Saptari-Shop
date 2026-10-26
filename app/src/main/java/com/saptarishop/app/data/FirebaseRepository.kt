package com.saptarishop.app.data

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.saptarishop.app.model.Category
import com.saptarishop.app.model.Order
import com.saptarishop.app.model.OrderStatus
import com.saptarishop.app.model.Product
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * सबै Firebase (Auth / Firestore / Storage) कार्यहरूको लागि एकल प्रवेश बिन्दु।
 * UI तहले सीधै Firebase SDK कल नगरी यो Repository प्रयोग गर्छ — यसले कोड सफा र टेस्ट गर्न सजिलो बनाउँछ।
 */
object FirebaseRepository {

    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    private const val COLLECTION_PRODUCTS = "products"
    private const val COLLECTION_CATEGORIES = "categories"
    private const val COLLECTION_ORDERS = "orders"
    private const val COLLECTION_ADMINS = "admins"

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    // ---------------- AUTH ----------------

    suspend fun signInAdmin(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Result.failure(Exception("लगइन असफल भयो"))
            // Verify this uid is a registered admin in Firestore (admins/{uid})
            val adminDoc = firestore.collection(COLLECTION_ADMINS).document(user.uid).get().await()
            if (!adminDoc.exists()) {
                auth.signOut()
                Result.failure(Exception("NOT_ADMIN"))
            } else {
                Result.success(user)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun ensureAnonymousAuth(): String? {
        // ग्राहकका लागि अर्डर ट्र्याकिङ्गको लागि anonymous UID प्रयोग गरिन्छ (कुनै फोन नम्बर/OTP आवश्यक पर्दैन)
        val user = auth.currentUser
        if (user != null) return user.uid
        return try {
            val result = auth.signInAnonymously().await()
            result.user?.uid
        } catch (e: Exception) {
            null
        }
    }

    fun signOutAdmin() {
        auth.signOut()
    }

    fun isAdminLoggedIn(): Boolean = auth.currentUser != null && !auth.currentUser!!.isAnonymous

    // ---------------- CATEGORIES ----------------

    fun listenCategories(onUpdate: (List<Category>) -> Unit): ListenerRegistration {
        return firestore.collection(COLLECTION_CATEGORIES)
            .orderBy("sortOrder", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Category::class.java)?.apply { id = doc.id }
                } ?: emptyList()
                onUpdate(list)
            }
    }

    suspend fun addCategory(category: Category): Result<String> {
        return try {
            val ref = firestore.collection(COLLECTION_CATEGORIES).document()
            category.id = ref.id
            ref.set(category).await()
            Result.success(ref.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCategory(categoryId: String): Result<Unit> {
        return try {
            firestore.collection(COLLECTION_CATEGORIES).document(categoryId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------------- PRODUCTS ----------------

    fun listenActiveProducts(onUpdate: (List<Product>) -> Unit): ListenerRegistration {
        return firestore.collection(COLLECTION_PRODUCTS)
            .whereEqualTo("isActive", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.apply { id = doc.id }
                } ?: emptyList()
                onUpdate(list)
            }
    }

    fun listenAllProductsForAdmin(onUpdate: (List<Product>) -> Unit): ListenerRegistration {
        return firestore.collection(COLLECTION_PRODUCTS)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.apply { id = doc.id }
                } ?: emptyList()
                onUpdate(list)
            }
    }

    suspend fun getProduct(productId: String): Product? {
        return try {
            val doc = firestore.collection(COLLECTION_PRODUCTS).document(productId).get().await()
            doc.toObject(Product::class.java)?.apply { id = doc.id }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveProduct(product: Product, newImageUri: Uri?): Result<String> {
        return try {
            val ref = if (product.id.isBlank()) {
                firestore.collection(COLLECTION_PRODUCTS).document()
            } else {
                firestore.collection(COLLECTION_PRODUCTS).document(product.id)
            }
            product.id = ref.id

            if (newImageUri != null) {
                val uploadResult = uploadProductImage(ref.id, newImageUri)
                uploadResult.onSuccess { url -> product.imageUrl = url }
                uploadResult.onFailure { return Result.failure(it) }
            }

            ref.set(product).await()
            Result.success(ref.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            firestore.collection(COLLECTION_PRODUCTS).document(productId).delete().await()
            // Storage image cleanup (best-effort)
            try {
                storage.reference.child("products/$productId").listAll().await().items.forEach {
                    it.delete()
                }
            } catch (_: Exception) { /* ignore cleanup errors */ }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun uploadProductImage(productId: String, uri: Uri): Result<String> {
        return try {
            val fileName = "${UUID.randomUUID()}.jpg"
            val ref = storage.reference.child("products/$productId/$fileName")
            ref.putFile(uri).await()
            val url = ref.downloadUrl.await()
            Result.success(url.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun searchProductsLocally(products: List<Product>, query: String): List<Product> {
        if (query.isBlank()) return products
        val q = query.trim().lowercase()
        return products.filter {
            it.name.lowercase().contains(q) || it.categoryName.lowercase().contains(q)
        }
    }

    // ---------------- ORDERS ----------------

    suspend fun placeOrder(order: Order): Result<String> {
        return try {
            val ref = firestore.collection(COLLECTION_ORDERS).document()
            order.id = ref.id
            order.orderNumber = generateOrderNumber()
            ref.set(order).await()
            Result.success(order.orderNumber)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun listenMyOrders(uid: String, onUpdate: (List<Order>) -> Unit): ListenerRegistration {
        return firestore.collection(COLLECTION_ORDERS)
            .whereEqualTo("customerUid", uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Order::class.java)?.apply { id = doc.id }
                } ?: emptyList()
                onUpdate(list)
            }
    }

    fun listenAllOrdersForAdmin(onUpdate: (List<Order>) -> Unit): ListenerRegistration {
        return firestore.collection(COLLECTION_ORDERS)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Order::class.java)?.apply { id = doc.id }
                } ?: emptyList()
                onUpdate(list)
            }
    }

    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit> {
        return try {
            firestore.collection(COLLECTION_ORDERS).document(orderId)
                .update("status", status.key).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generateOrderNumber(): String {
        val timestamp = System.currentTimeMillis().toString().takeLast(8)
        return "SS$timestamp"
    }
}
