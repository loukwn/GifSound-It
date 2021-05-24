# PROGUARD RULES FOR THE RELEASE VERSION

# kotlin flow proguard bug: https://github.com/Kotlin/kotlinx.coroutines/issues/1270
-dontwarn kotlinx.coroutines.flow.**inlined**

# Firebase
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
-keep class com.google.android.gms.** { *; }
-keep class com.google.firebase.** { *; }
