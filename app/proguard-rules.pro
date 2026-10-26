# Add project specific ProGuard rules here.
# Firebase / Firestore model classes must keep fields for serialization.
-keepattributes Signature
-keepattributes *Annotation*

-keep class com.saptarishop.app.model.** { *; }

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Firebase Firestore
-keepclassmembers class * {
  @com.google.firebase.firestore.PropertyName <fields>;
}
