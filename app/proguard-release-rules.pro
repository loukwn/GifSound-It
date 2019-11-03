# PROGUARD RULES FOR THE RELEASE VERSION

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# Fabric needs this to work
-keepattributes *Annotation*

# keep the model classes for the retrofit results
-keep class com.kostaslou.gifsoundit.data.api.model.** { *; }

# kotlin flow proguard bug: https://github.com/Kotlin/kotlinx.coroutines/issues/1270
-dontwarn kotlinx.coroutines.flow.**inlined**