# Preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

# audioTagger
-dontwarn java.awt.**
-dontwarn javax.imageio.**
-dontwarn javax.swing.filechooser.**
-dontwarn sun.security.action.**
-keep class org.jaudiotagger.** { *; }