# Consumer R8 rules for :core:database — merged into the app's release shrinker.
# Room accesses entities, DAOs and type converters reflectively; keep them.

-keepattributes *Annotation*, Signature

# Entities (@Entity), DAOs (@Dao) and type converters (@TypeConverter).
-keep class com.privin.database.model.** { *; }
-keep class com.privin.database.dao.** { *; }
-keep class com.privin.database.util.** { *; }

# Room-generated implementations.
-keep class * extends androidx.room.RoomDatabase { <init>(); }
-keep @androidx.room.Entity class * { *; }
-dontwarn androidx.room.paging.**
