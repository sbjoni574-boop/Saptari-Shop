# सप्तरी शप (Saptari Shop) — Android App

सप्तरी जिल्लाका ग्राहकहरूका लागि बनाइएको अनलाइन किनमेल (shopping) Android एप। हिन्दी/नेपाली
(देवनागरी) भाषामा UI, Cash on Delivery (COD), सप्तरीका १८ स्थानीय तह + वडा + गाउँ/टोल
सहितको डेलिभरी ठेगाना फारम, र Firebase (Auth + Firestore + Storage) मा आधारित सुरक्षित
एडमिन प्यानल समावेश गर्ने लक्ष्य राखिएको छ।

> ⚠️ **हालको अवस्था / Project Status**: यो प्रोजेक्ट एउटा राम्रोसँग डिजाइन गरिएको
> **आर्किटेक्चर र फाउन्डेसन (scaffold)** हो, तर अझै **पूर्ण रूपमा build-ready APK होइन**।
> तल "के पूरा भयो" र "के बाँकी छ" स्पष्ट रूपमा उल्लेख गरिएको छ। सिमित सन्दर्भ (सन्दर्भ
> विन्डो/समय) भित्र पूरा एन्ड-टु-एन्ड working एप (५०+ स्क्रिन, एडमिन CRUD UI, Glide/RecyclerView
> adapters, navigation wiring, इत्यादि) बनाउन धेरै ठूलो मात्रामा कोड चाहिन्छ। यो काम
> अर्को सत्रमा निरन्तरता दिन सकिन्छ।

---

## १. के पूरा भइसकेको छ (Completed)

### १.१ प्रोजेक्ट संरचना र बिल्ड सेटअप
- Gradle Kotlin DSL (`build.gradle.kts`, `settings.gradle.kts`, `app/build.gradle.kts`)
- Gradle Wrapper कन्फिगरेसन (Gradle 8.9, AGP 8.5.2, Kotlin 1.9.24)
- `minSdk 23`, `targetSdk 34`, `compileSdk 34` — धेरै पुराना भन्दा आधुनिक स्मार्टफोनसम्म काम गर्ने दायरा
- Firebase BoM (33.4.0) सहित Auth / Firestore / Storage / Analytics dependencies
- Glide (image loading), Coroutines, Navigation, Lifecycle, Material Components dependencies
- ProGuard rules (release build को लागि)
- Network security config (cleartext traffic बन्द, HTTPS मात्र)

### १.२ डेटा मोडेल (Kotlin data classes)
- `Product.kt` — नाम, मूल्य, पुरानो मूल्य (छुट), फोटो URL, स्टक, श्रेणी, active/featured flags
- `Category.kt` — उत्पादन श्रेणी
- `CartItem.kt` — स्थानीय कार्ट आइटम
- `OrderItem.kt` — अर्डर भित्रको उत्पादन स्न्यापसट
- `Order.kt` — पूर्ण अर्डर (ग्राहक जानकारी + ठेगाना + items + status + COD)
- `DeliveryAddress.kt` — स्थानीय तह/वडा/टोल भ्यालिडेसन सहित

### १.३ सप्तरीका १८ स्थानीय तह डेटा (`SaptariLocalLevels.kt`)
सरकारी तथ्याङ्क (DCC सप्तरी / MoFAGA) अनुसार९ नगरपालिका + ९ गाउँपालिका, प्रत्येकको
वडा संख्या सहित (जम्मा १६४ वडा):

| स्थानीय तह | प्रकार | वडा संख्या |
|---|---|---|
| राजविराज | नगरपालिका | १६ |
| कञ्चनरुप | नगरपालिका | १२ |
| डाकनेश्वरी | नगरपालिका | १० |
| बोदेबरसाइन | नगरपालिका | १० |
| खडक | नगरपालिका | ११ |
| शम्भुनाथ | नगरपालिका | १२ |
| सुरुङ्गा | नगरपालिका | ११ |
| हनुमाननगर कंकालिनी | नगरपालिका | १४ |
| सप्तकोशी | नगरपालिका | ११ |
| अग्निसाइर कृष्णासवरन | गाउँपालिका | ६ |
| छिन्नमस्ता | गाउँपालिका | ७ |
| महादेवा | गाउँपालिका | ६ |
| तिरहुत | गाउँपालिका | ५ |
| तिलाठी कोइलाडी | गाउँपालिका | ८ |
| रुपनी | गाउँपालिका | ६ |
| राजगढ | गाउँपालिका | ६ |
| बिष्णुपुर | गाउँपालिका | ७ |
| बलान-बिहुल | गाउँपालिका | ६ |

यो डेटाको आधारमा checkout फारममा "स्थानीय तह" dropdown चयन गरेपछि "वडा नं." dropdown
स्वतः सम्बन्धित वडा संख्यासम्म देखिने गरी (`SaptariLocalLevels.wardNumbers(id)`) लजिक
तयार छ। त्यसपछि ग्राहकले "गाउँ/टोल" स्वतन्त्र टेक्स्ट लेख्छन्।

### १.४ Firebase Repository तह (`FirebaseRepository.kt`)
- Admin email/password login + Firestore `admins/{uid}` collection मार्फत प्रमाणीकरण जाँच
- ग्राहकका लागि anonymous auth (OTP/फोन प्रमाणीकरण बिना अर्डर ट्र्याक गर्न)
- Products/Categories/Orders को लागि real-time listener (`addSnapshotListener`)
- Product image Storage अपलोड (`products/{productId}/{uuid}.jpg`)
- Order placement, order status update (एडमिनद्वारा)
- Coroutines + `kotlinx-coroutines-play-services` प्रयोग गरी `await()` आधारित clean async code

### १.५ CartManager (`CartManager.kt`)
डिभाइसमा नै (SharedPreferences + हल्का JSON, कुनै बाह्य लाइब्रेरी बिना) कार्ट डेटा
राख्ने — add/remove/update quantity/clear/subtotal/itemCount।

### १.६ Firebase Security Rules — **production-ready**
- **`firestore.rules`**:
  - जोकोहीले categories/products पढ्न सक्छ (ग्राहक एपका लागि आवश्यक)
  - केवल `admins/{uid}` collection मा सूचीकृत UID भएकोले मात्र लेख्न/मेटाउन सक्छ
  - ग्राहकले आफ्नो अर्डर सिर्जना गर्न सक्छ (validation सहित: नाम, फोन लम्बाइ १०,
    स्थानीय तह, वडा नं., गाउँ/टोल अनिवार्य; status सधैं "pending" बाट सुरु)
  - ग्राहकले आफ्नै अर्डर मात्र पढ्न सक्छ (`customerUid == request.auth.uid`)
  - अर्डर स्थिति (pending → confirmed → shipped → delivered) परिवर्तन **केवल एडमिनले** गर्न सक्छ
  - `admins` collection क्लाइन्टबाट लेख्न सकिंदैन (Console/Admin SDK बाट मात्र)
- **`storage.rules`**:
  - उत्पादन फोटो सबैले हेर्न सक्छन्
  - अपलोड/मेटाउने अधिकार केवल एडमिन (Firestore cross-service `firestore.exists()` जाँच)
  - फाइल साइज अधिकतम ५MB, image/* content-type मात्र स्वीकृत

### १.७ Hindi भाषा संसाधन
`strings.xml` मा १५०+ स्ट्रिङ पूर्ण रूपमा हिन्दी/नेपाली (देवनागरी) मा — होम, सर्च,
कार्ट, चेकआउट, अर्डर, एडमिन लगइन/ड्यासबोर्ड/उत्पादन/अर्डर व्यवस्थापन सबैको लागि।

### १.८ डिजाइन प्रणाली
- `colors.xml` — भगुवा/सुन्तला (brand_primary) + हरियो (brand_secondary) आधारित आधुनिक
  Nepali/Madhesh-प्रेरित रङ प्यालेट, मूल्य/स्थिति/छुट रङहरू सहित
- `themes.xml` — Material Components आधारित थिम, राउन्ड बटन/इनपुट बक्स स्टाइल
- `dimens.xml` — स्पेसिङ/टेक्स्ट साइज standard scale
- Vector launcher icon (adaptive icon, shopping-bag थिम) + splash screen background

---

## २. के बाँकी छ (Remaining Work)

एउटा वास्तविक रूपमा install/चलाउन मिल्ने APK बनाउनका लागि अझै तलका काम चाहिन्छन् —
यी सबै "ठूला तर स्पष्ट" काम हुन् र माथिको foundation माथि बनाउन सकिन्छ:

1. **Layout XML फाइलहरू** (`activity_main.xml`, `item_product.xml`,
   `activity_product_detail.xml`, `activity_cart.xml`, `item_cart.xml`,
   `activity_checkout.xml`, `activity_order_success.xml`, `activity_my_orders.xml`,
   `item_order.xml`, `activity_order_detail.xml`, `activity_admin_login.xml`,
   `activity_admin_dashboard.xml`, `activity_admin_product_list.xml`,
   `item_admin_product.xml`, `activity_admin_product_edit.xml`,
   `activity_admin_order_list.xml`, `activity_admin_order_detail.xml`,
   `activity_admin_category_list.xml`, bottom navigation menu, fragments)
2. **Kotlin Activities/Fragments/ViewModels** जुन `AndroidManifest.xml` मा घोषणा
   गरिएका छन् (हाल ती फाइलहरू अझै लेखिएका छैनन्):
   - `ui/common/SplashActivity`
   - `ui/customer/MainActivity` (BottomNavigationView + Home/Category/Cart/Orders/Account fragments)
   - `ui/customer/ProductDetailActivity`, `CartActivity`, `CheckoutActivity`,
     `OrderSuccessActivity`, `MyOrdersActivity`, `OrderDetailActivity`
   - `ui/admin/AdminLoginActivity`, `AdminDashboardActivity`,
     `AdminProductListActivity`, `AdminProductEditActivity` (फोटो अपलोड UI सहित),
     `AdminOrderListActivity`, `AdminOrderDetailActivity`, `AdminCategoryListActivity`
3. **RecyclerView Adapters** (Product grid, Cart list, Order list, Admin product list,
   Ward number spinner adapter, Local level spinner adapter)
4. **Image picker integration** (`ActivityResultContracts.GetContent` / camera) +
   Glide लोडिङ + अपलोड प्रगति UI
5. **Checkout फारम लजिक**: स्थानीय तह spinner → वडा spinner cascading, भ्यालिडेसन,
   Firebase anonymous auth सुनिश्चित गरेर अर्डर पेश गर्ने flow
6. **`google-services.json` प्लेसहोल्डर/टेम्प्लेट** थप्ने (वास्तविक फाइल प्रयोगकर्ताले
   आफ्नै Firebase प्रोजेक्टबाट डाउनलोड गरेर `app/` फोल्डरमा राख्नुपर्छ)
7. **Firebase Console सेटअप कदमहरू अनुसार वास्तविक build-and-run** (Gradle sync +
   assembleDebug/assembleRelease) गरेर compile त्रुटिहरू हटाउने — हालको सन्दर्भमा
   Android SDK पूर्ण रूपमा सेटअप र समय दुवैको सीमाका कारण यो चरण पूरा हुन सकेन।
8. लोगो/ब्यानर/श्रेणी आइकन जस्ता थप raster/vector assets (हाल आधारभूत vector launcher
   icon मात्र तयार छ)।

---

## ३. Firebase सेटअप निर्देशिका (हिन्दी/नेपालीमा) — अर्को चरणका लागि तयार

जब माथिका बाँकी UI फाइलहरू थपिन्छन्, तलका कदमहरूले एपलाई Firebase सँग जोड्न र
APK बनाउन मद्दत गर्नेछ:

### ३.१ Firebase प्रोजेक्ट बनाउने
1. https://console.firebase.google.com मा जानुहोस् र "Add project" थिच्नुहोस्।
2. प्रोजेक्टको नाम `Saptari Shop` राख्नुहोस्।
3. Google Analytics ऐच्छिक छ (चाहेमा सक्षम गर्नुहोस्)।

### ३.२ Android एप थप्ने
1. Firebase Console मा "Add app" → Android छान्नुहोस्।
2. Package name मा ठ्याक्कै यही लेख्नुहोस्: `com.saptarishop.app`
3. `google-services.json` डाउनलोड गर्नुहोस् र यसलाई प्रोजेक्टको `app/` फोल्डर भित्र
   राख्नुहोस् (जस्तै: `app/google-services.json`)।

### ३.३ Authentication सक्षम गर्ने
1. Firebase Console → Build → Authentication → "Get started"
2. Sign-in method मा **Email/Password** सक्षम गर्नुहोस् (एडमिनका लागि)
3. **Anonymous** पनि सक्षम गर्नुहोस् (ग्राहकका लागि, ताकि उनीहरूले लगइन नगरी नै
   अर्डर गर्न र आफ्नो अर्डर ट्र्याक गर्न सकून्)

### ३.४ पहिलो एडमिन खाता बनाउने
1. Authentication → Users ट्याबमा गएर "Add user" थिच्नुहोस्।
2. इमेल र पासवर्ड राख्नुहोस् (जस्तै: `admin@saptarishop.com`)।
3. त्यो user को **UID** कपी गर्नुहोस् (Users लिस्टमा देखिन्छ)।
4. Firestore Database → Data ट्याबमा गएर `admins` नामको collection बनाउनुहोस्।
5. त्यस collection भित्र, डकुमेन्ट ID ठ्याक्कै माथिको **UID** राखेर एउटा डकुमेन्ट
   बनाउनुहोस् (भित्र जे फिल्ड नराखे पनि हुन्छ, डकुमेन्ट अस्तित्वले नै प्रमाणित गर्छ)।

> यसरी मात्र त्यो प्रयोगकर्ताले एडमिन प्यानलमा लगइन गर्न पाउनेछ — किनभने
> `firestore.rules` ले `admins/{uid}` डकुमेन्टको अस्तित्व जाँच गर्छ।

### ३.५ Firestore Database सिर्जना गर्ने
1. Build → Firestore Database → "Create database"
2. "Start in production mode" छान्नुहोस् (हामीले यो repo मा custom rules दिएका छौं)
3. Location: `asia-south1` (Mumbai, भारत — नेपालसँग नजिकैको) छान्नुहोस्
4. डेटाबेस बनिसकेपछि Rules ट्याबमा गएर यो repo को `firestore.rules` फाइलको सामग्री
   कपी-पेस्ट गरेर "Publish" थिच्नुहोस्।

### ३.६ Storage सक्षम गर्ने
1. Build → Storage → "Get started"
2. Production mode मा राख्नुहोस्, same location (`asia-south1`) छान्नुहोस्
3. Rules ट्याबमा गएर यो repo को `storage.rules` फाइलको सामग्री कपी-पेस्ट गरी Publish गर्नुहोस्।

### ३.७ Firebase CLI बाट rules डिप्लोय गर्ने (वैकल्पिक, छिटो तरिका)
```bash
npm install -g firebase-tools
firebase login
firebase init firestore storage   # प्रोजेक्ट छान्नुहोस्, फाइल overwrite नगर्नुहोस्
firebase deploy --only firestore:rules,storage:rules
```

### ३.८ Android Studio मा खोल्ने र चलाउने
1. Android Studio (Giraffe/Koala वा पछिल्लो) स्थापना गर्नुहोस्।
2. यो प्रोजेक्ट फोल्डर खोल्नुहोस् (`File → Open`)।
3. `app/google-services.json` राखेको सुनिश्चित गर्नुहोस्।
4. "Gradle Sync" पूरा हुन दिनुहोस् (इन्टरनेट चाहिन्छ)।
5. बाँकी रहेका Activities/Layouts (माथि खण्ड २ मा उल्लेखित) थप्नुहोस्।
6. `Run ▶` थिचेर USB/emulator मा चलाउनुहोस्।

### ३.९ APK/AAB बनाउने
```bash
# Debug APK (testing का लागि)
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk

# Release APK (Play Store/वितरणका लागि — पहिले signing key बनाउनुपर्छ)
keytool -genkey -v -keystore saptari-release.keystore -alias saptarishop \
  -keyalg RSA -keysize 2048 -validity 10000
./gradlew assembleRelease
```
Release build को लागि `app/build.gradle.kts` मा `signingConfigs` थप्नुपर्छ —
keystore path, password, र alias सहित।

---

## ४. प्रोजेक्ट संरचना

```
webapp/
├── build.gradle.kts, settings.gradle.kts, gradle.properties
├── gradle/wrapper/gradle-wrapper.properties
├── firestore.rules          # Firestore सुरक्षा नियम (production-ready)
├── storage.rules            # Storage सुरक्षा नियम (production-ready)
├── README.md                # यो फाइल
└── app/
    ├── build.gradle.kts
    ├── proguard-rules.pro
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/saptarishop/app/
        │   ├── SaptariShopApp.kt
        │   ├── data/
        │   │   ├── FirebaseRepository.kt
        │   │   ├── CartManager.kt
        │   │   └── SaptariLocalLevels.kt   # सप्तरीका १८ स्थानीय तह + वडा डेटा
        │   ├── model/
        │   │   ├── Product.kt, Category.kt, CartItem.kt
        │   │   ├── OrderItem.kt, Order.kt, DeliveryAddress.kt
        │   ├── utils/
        │   │   ├── Formatters.kt
        │   │   └── ValidationUtils.kt
        │   ├── ui/customer/     # (Activities/Fragments थप्न बाँकी)
        │   ├── ui/admin/        # (Activities थप्न बाँकी)
        │   └── adapter/         # (RecyclerView adapters थप्न बाँकी)
        └── res/
            ├── values/ (strings.xml - हिन्दीमा, colors.xml, themes.xml, dimens.xml)
            ├── drawable/ (icons, backgrounds)
            ├── mipmap*/ (launcher icon)
            ├── layout/ (activity_splash.xml मात्र हाल तयार)
            └── xml/ (network_security_config.xml, backup_rules.xml)
```

---

## ५. सुरक्षा विशेषताहरू सारांश

- ✅ Firebase Authentication (Email/Password एडमिनका लागि, Anonymous ग्राहकका लागि)
- ✅ Firestore Rules: role-based access (`admins` collection आधारित), फिल्ड-स्तर validation
- ✅ Storage Rules: cross-service Firestore जाँच गरेर मात्र एडमिनलाई अपलोड अनुमति
- ✅ Network security config: HTTPS/cleartext traffic बन्द
- ✅ ProGuard/R8 enabled release build मा (कोड obfuscation + shrinking)
- ✅ ग्राहकले अर्डर status/मूल्य आफैं बदल्न नसक्ने (केवल एडमिन अधिकार)

---

## ६. लाइसेन्स र प्रयोग

यो कोड सप्तरी शप प्रोजेक्टका लागि तयार गरिएको हो। प्रयोगकर्ताले आफ्नै Firebase
प्रोजेक्ट, पासवर्ड, र branding सँग अनुकूल बनाएर प्रयोग गर्न सक्नुहुन्छ।
