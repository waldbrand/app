# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/z/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontobfuscate

-assumenosideeffects class * implements org.slf4j.Logger {
     public void debug(...);
     public void info(...);
}

-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
    public static int wtf(...);
}

-assumenosideeffects class de.topobyte.android.misc.utils.AndroidTimeUtil {
    public static void time(...);
}

-assumenosideeffects class de.topobyte.misc.util.TimeCounter {
    public static void start(...);
    public static void stop(...);
    public static long getTotal(...);
}

-dontwarn net.jpountz.util.UnsafeUtils
-dontwarn de.topobyte.misc.util.SwingHelper
-dontwarn de.topobyte.misc.util.ImageUtil
-dontwarn com.vividsolutions.jts.awt.**
-dontwarn com.google.common.**
-dontwarn de.topobyte.jeography.viewer.core.**
