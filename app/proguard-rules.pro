# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Preserve line number information for readable release crash stack traces.
-keepattributes SourceFile,LineNumberTable
# Hide the original source file name in the (kept) line number info.
-renamesourcefileattribute SourceFile

# Hilt/Dagger generate code that R8 handles via bundled rules; no manual keeps
# are required for a standard setup. Add targeted keeps here only if a release
# build surfaces a missing-binding or reflection error.

-keep class androidx.compose.** { *; }
-keep interface androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Moshi (used for JSON serialization/deserialization)
-keep class com.squareup.moshi.** { *; }
-dontwarn com.squareup.moshi.**

-keepclasseswithmembers class * {
    @com.squareup.moshi.Json <fields>;
    @com.squareup.moshi.JsonClass <fields>;
}

# Keep annotations for Moshi
-keepattributes *Annotation*

# Keep all class members for Continuation and coroutines
-keepclassmembers,allowobfuscation class ** {
    @kotlin.coroutines.Continuation public <methods>;
}

# Keep metadata to support reflection for coroutines and suspend functions
-keepclassmembers class kotlin.Metadata {
    *;
}

# Keep methods generated for coroutine suspend functions
-keepclassmembers class ** {
    public <methods>;
}

# Prevent stripping of Coroutine classes used during runtime
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# Prevent stripping of Kotlin internal coroutine classes
-keep class kotlin.coroutines.jvm.internal.** { *; }
-dontwarn kotlin.coroutines.jvm.internal.**

# To ensure proper support for Coroutine Intrinsics and Continuation classes
-keep class kotlin.coroutines.intrinsics.** { *; }

# Keep coroutines debug agent classes
-keep class kotlinx.coroutines.debug.** { *; }
