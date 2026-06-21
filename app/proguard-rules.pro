# Add project specific ProGuard rules here.
# https://developer.android.com/build/shrink-code

# ============================================================================
# DATA MODEL (Room entities + enums)
# ============================================================================
# Room generates code against these at compile time; keep them (and enum
# values, which Room reads via valueOf) intact so R8 can't rename/strip them.
-keep class com.noty.app.data.** { *; }
-keepclassmembers enum com.noty.app.data.** { *; }

# ============================================================================
# KOTLIN
# ============================================================================
-keep class kotlin.Metadata { *; }

# ============================================================================
# ROOM
# ============================================================================
# Room ships its own consumer rules in the AAR; these are belt-and-suspenders.
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-dontwarn androidx.room.paging.**
