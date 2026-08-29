# Equinox Proguard / R8 Optimization Rules

# Preserve Room Annotations and Entities
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
-keep class androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Preserve Data Models and Entities for Room & Gson Serialization
-keep class com.toukir.equinox.data.local.entity.** { *; }
-keep class com.toukir.equinox.data.local.model.** { *; }
-keep class com.toukir.equinox.data.backup.** { *; }

# Gson Proguard Rules
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Kotlin Coroutines & Reflection
-dontwarn kotlinx.coroutines.**
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# AndroidX Compose & Datastore
-dontwarn androidx.datastore.**
-dontwarn androidx.compose.**
