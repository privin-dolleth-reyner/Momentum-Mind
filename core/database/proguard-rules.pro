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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Room persistence library
-keep class androidx.room.RoomDatabase { *; }
-keep class androidx.room.RoomDatabase_Impl { *; }
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Keep annotations used by Room (for @Entity, @Dao, @Database, etc.)
-keepattributes Signature
-keepattributes *Annotation*

# Keep entity (model) classes. Room uses reflection to access your entities.
# If your entity classes are obfuscated, Room might not be able to access them.
-keep class com.your.package.name.room.entities.** { *; }
# Replace with the actual package where your entities are located

# Keep DAO classes. These classes are used to generate the code for accessing the database.
-keep class com.your.package.name.room.daos.** { *; }
# Replace with the actual package where your DAOs are located

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
