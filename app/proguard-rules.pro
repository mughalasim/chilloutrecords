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
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# MAPPING
-printmapping mapping.txt
-printusage build/usage.txt
-printseeds build/seeds.txt

# GLIDE CLASSES
-dontwarn javax.annotation.**
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder

# FIREBASE NEEDS THE MODELS
-keepattributes Signature
-keepattributes *Annotation*
-keepclassmembers class com.chilloutrecords.models.** {*;}

# YOYO ANIMATION CLASSES
-keep class com.daimajia.* { *; }
-keep interface com.daimajia.* { *; }
-keep public class com.daimajia.* { *; }
-keep class com.daimajia.easing.** { *; }
-keep interface com.daimajia.easing.** { *; }
-keep class com.nineoldandroids.* { *; }
-keep interface com.nineoldandroids.* { *; }
-keep public class com.nineoldandroids.* { *; }

# Mobfox
-keepclassmembers class * {
   @android.webkit.JavascriptInterface <methods>;
}
# MobFox SDK
-keep class com.mobfox.** { *; }
-keepclassmembers class com.mobfox.** { *; }