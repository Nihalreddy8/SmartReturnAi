# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /path/to/android-sdk/tools/proguard/proguard-android.txt

# Keep Room entities
-keep class com.smartreturn.ai.data.entity.** { *; }

# Keep data classes used in the UI layer
-keep class com.smartreturn.ai.** { *; }
