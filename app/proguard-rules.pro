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

# 高德地图SDK混淆规则
-keep class com.amap.api.**{*;}
-keep class com.loc.**{*;}
-keep class com.autonavi.**{*;}
-keep class com.locationtech.**{*;}

# 3D地图
-keep class com.amap.api.maps.**{*;}
-keep class com.amap.api.mapcore.**{*;}

# 导航
-keep class com.amap.api.navi.**{*;}
-keep class com.autonavi.**{*;}

# 搜索
-keep class com.amap.api.services.**{*;}

# 定位
-keep class com.amap.api.location.**{*;}

# 2D地图
-keep class com.amap.api.maps2d.**{*;}

# 基础功能
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}