# Consumer R8 rules for :core:network — merged into the app's release shrinker
# (the app is the only module with isMinifyEnabled = true).

-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations, AnnotationDefault

# --- Moshi (reflection via KotlinJsonAdapterFactory + codegen adapters) ---
# Network JSON models are accessed reflectively; keep them and their members.
-keep class com.privin.network.model.** { *; }
-keepclassmembers class com.privin.network.model.** { *; }
# Keep generated Moshi adapters (looked up by "<Class>JsonAdapter" name).
-keep class **JsonAdapter { <init>(...); *; }
-keepnames @com.squareup.moshi.JsonClass class *
-keepclassmembers @com.squareup.moshi.JsonClass class * { <init>(...); }
-keepclasseswithmembers class * {
    @com.squareup.moshi.Json <fields>;
}
-keep class com.squareup.moshi.** { *; }
-dontwarn com.squareup.moshi.**

# --- Retrofit (modern Retrofit ships its own rules; these are defensive) ---
-keep,allowobfuscation,allowshrinking interface com.privin.network.ApiService
-keepclasseswithmembers,allowobfuscation,allowshrinking interface * {
    @retrofit2.http.* <methods>;
}
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-dontwarn retrofit2.**
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**

# --- OkHttp / Okio ---
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# --- kotlinx.serialization (serializer lookup is generated/reflective) ---
-keepclassmembers class com.privin.network.** {
    *** Companion;
}
-keepclasseswithmembers class com.privin.network.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.privin.network.**$$serializer { *; }
