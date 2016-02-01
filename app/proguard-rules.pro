# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\ADT\sdk/tools/proguard/proguard-android.txt
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


-keep class com.uni.unidsher.data.database.DatabaseHelper
-keepclassmembers class com.uni.unidsher.data.database.DatabaseHelper { *; }

-keep class com.uni.unidasher.data.datamodel.**
-keepclassmembers class com.uni.unidasher.data.datamodel.** { *; }
-keep enum com.uni.unidsher.data.datamodel.**
-keepclassmembers enum com.uni.unidasher.data.datamodel.** { *; }
-keep interface com.uni.unidasher.data.datamodel.**
-keepclassmembers interface com.uni.unidasher.data.datamodel.** { *; }

-keep class com.uni.unidasher.data.entity.**
-keepclassmembers class com.uni.unidasher.data.entity.** { *; }
-keep enum com.uni.unidasher.data.entity.**
-keepclassmembers enum com.uni.unidasher.data.entity.** { *; }
-keep interface com.uni.unidasher.data.entity.**
-keepclassmembers interface com.uni.unidasher.data.entity.** { *; }

#retrofit & okhttp
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keep interface com.uni.unidasher.data.rest.RESTService
-keepclassmembers interface com.uni.unidasher.data.rest.RESTService { *; }

-keep class com.google.gson.** { *; }
-keep class com.google.inject.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.** { *; }
-keep class retrofit.** { *; }
-dontwarn rx.**
-dontwarn com.squareup.okhttp.**
-dontwarn retrofit.appengine.**
-dontwarn okio.**
-keep class com.example.testobfuscation.** { *; }
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }

-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}
-keepclassmembers,allowoptimization class com.google.common.* {
    void finalizeReferent();
    void startFinalizer(java.lang.Class,java.lang.Object);
}
-keepclassmembers class * {
       @com.google.common.eventbus.Subscribe *;
}

-keepclassmembers class * implements java.io.Serializable {
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable,*Annotation*,Signature

#guava
-keep class com.google.common.**
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe
-dontwarn com.google.common.collect.MinMaxPriorityQueue
-keep,allowoptimization class com.google.inject.** { *; }
-keep,allowoptimization class javax.inject.** { *; }
-keep,allowoptimization class javax.annotation.** { *; }
-keep,allowoptimization class com.google.inject.Binder

# OrmLite
-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }

#Bugly
-keep public class com.tencent.bugly.crashreport.crash.jni.NativeCrashHandler{public *; native <methods>;}
-keep public interface com.tencent.bugly.crashreport.crash.jni.NativeExceptionHandler{*;}

#百度地图
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.**

#环信
-keep class com.easemob.** {*;}
-keep class org.jivesoftware.** {*;}
-keep class org.apache.** {*;}
-dontwarn  com.easemob.**
#2.0.9后的不需要加下面这个keep
#-keep class org.xbill.DNS.** {*;}
#另外，demo中发送表情的时候使用到反射，需要keep SmileUtils,注意前面的包名，
#不要SmileUtils复制到自己的项目下keep的时候还是写的demo里的包名
-keep class com.uni.unidasher.chat.ui.utils.SmileUtils {*;}

#2.0.9后加入语音通话功能，如需使用此功能的api，加入以下keep
-dontwarn ch.imvs.**
-dontwarn org.slf4j.**
-keep class org.ice4j.** {*;}
-keep class net.java.sip.** {*;}
-keep class org.webrtc.voiceengine.** {*;}
-keep class org.bitlet.** {*;}
-keep class org.slf4j.** {*;}
-keep class ch.imvs.** {*;}

#个推
-keep class com.igexin.** {*;}
-dontwarn com.igexin.**


-dontwarn java.lang.invoke.**


