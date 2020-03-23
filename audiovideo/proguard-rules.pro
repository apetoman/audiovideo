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


-keep public class com.ihsanbal.logging.**{*;}
-keep public class com.eju.cy.audiovideo.audio.**{*;}
-keep public class com.eju.cy.audiovideo.dto.**{*;}
-keep public class com.eju.cy.audiovideo.im.**{*;}

-keep public class com.eju.cy.audiovideo.net.**{*;}
-keep public class com.eju.cy.audiovideo.trtc.**{*;}
-keep public class com.eju.cy.audiovideo.utils.**{*;}
-keep public class com.eju.cy.audiovideo.video.**{*;}


 -keep class com.eju.cy.audiovideo.** { *; }
-keep class com.tencent.** { *; }


################gson###############
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.sunloto.shandong.bean.** { *; }



-keep class com.just.agentweb.** {
    *;
}
-dontwarn com.just.agentweb.**

-keepclassmembers class com.eju.cy.drawlibrary.web.AndroidInterface{ *; }
-keep class com.eju.cy.drawlibrary.JDHomeSdk{ *; }
####################腾讯webx5#################
-dontwarn com.tencent.smtt.**
-keep public class com.tencent.smtt.**{*;}
-dontwarn com.tencent.tbs.**
-keep public class com.tencent.tbs.**{*;}
####################腾讯webx5#################

################retrofit###############
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

################RxJava and RxAndroid###############
-dontwarn org.mockito.**
-dontwarn org.junit.**
-dontwarn org.robolectric.**

-keep class io.reactivex.** { *; }
-keep interface io.reactivex.** { *; }

-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-dontwarn okio.**
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

-dontwarn io.reactivex.**
-dontwarn retrofit.**
-keep class retrofit.** { *; }

-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

-keep class sun.misc.Unsafe { *; }

-dontwarn java.lang.invoke.*

-keep class io.reactivex.schedulers.Schedulers {
    public static <methods>;
}
-keep class io.reactivex.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class io.reactivex.schedulers.TestScheduler {
    public <methods>;
}
-keep class io.reactivex.schedulers.Schedulers {
    public static ** test();
}
-keepclassmembers class io.reactivex.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class io.reactivex.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}

-keepclassmembers class io.reactivex.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    io.reactivex.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class io.reactivex.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    io.reactivex.internal.util.atomic.LinkedQueueNode consumerNode;
}

-dontwarn io.reactivex.internal.util.unsafe.**

-keepclasseswithmembernames class * { # 保持native方法不被混淆
    native <methods>;
}

