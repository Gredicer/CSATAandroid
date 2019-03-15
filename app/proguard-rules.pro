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
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontwarn com.alibaba.fastjson.**
-dontwarn com.aliyun.alink.linksdk.**
-dontwarn com.facebook.**
-dontwarn okhttp3.internal.**
-keep class com.alibaba.** {*;}
-keep class com.aliyun.alink.linkkit.api.**{*;}
-keep class com.aliyun.alink.dm.api.**{*;}
-keep class com.aliyun.alink.dm.model.**{*;}
-keep class com.aliyun.alink.dm.shadow.ShadowResponse{*;}
## 设备sdk keep
-keep class com.aliyun.alink.linksdk.channel.**{*;}
-keep class com.aliyun.alink.linksdk.tmp.**{*;}
-keep class com.aliyun.alink.linksdk.cmp.**{*;}
-keep class com.aliyun.alink.linksdk.alcs.**{*;}
-keep public class com.aliyun.alink.linksdk.alcs.coap.**{*;}
-keep class com.http.helper.**{*;}
-keep class com.aliyun.alink.apiclient.**{*;}