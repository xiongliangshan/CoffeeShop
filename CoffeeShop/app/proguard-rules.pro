# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Program Files\Android\sdk/tools/proguard/proguard-android.txt
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

-dontwarn android.support.**
-dontwarn org.apache.**
-dontwarn com.igexin.**
-dontwarn com.alibaba.fastjson.**

-keep class com.igexin.**{*;}
-keep class com.alibaba.fastjson.**{*;}
-keep class org.apache.**{*;}
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }

#baidu SDK
-dontwarn com.baidu.**
-dontwarn vi.com.gdi.bgl.android.**
-dontwarn com.sinovoice.**
-keep class com.sinovoice.**{*;}
-keep class com.baidu.** { *; }
-keep class vi.com.gdi.bgl.android.**{*;}
-keep class com.jsytwy.**{*;}
-keep interface com.baidu.**{*;}
-keep interface com.sinovoice.**{*;}

#bugly
-keep public class com.tencent.bugly.**{*;}

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}

#butter knife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

#jiguang
-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }