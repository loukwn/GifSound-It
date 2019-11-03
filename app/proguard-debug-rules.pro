# PROGUARD RULES FOR THE DEBUG VERSION

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

# these are needed for the android instrumentation tests
-dontobfuscate
-keep class androidx.recyclerview.widget.RecyclerView  {
    public androidx.recyclerview.widget.RecyclerView$ViewHolder findViewHolderForPosition(int);
}

-dontwarn kotlinx.coroutines.flow.**inlined**
