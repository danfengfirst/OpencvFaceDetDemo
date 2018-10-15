# OpencvFaceDetDemo
## 正脸检测
![这里随便写文字](https://github.com/danfengfirst/OpencvFaceDetDemo/blob/master/demo.jpg)

## 侧脸检测

![这里随便写文字](https://github.com/danfengfirst/OpencvFaceDetDemo/blob/master/demo2.jpg)

## 导入opencv依赖

https://blog.csdn.net/danfengw/article/details/78754233

## 自己编译so文件
如果你需要自己编译libdetection_based_tracker.so文件可以参考一下操作

1.拷贝opencv-3.3.0-android-sdk\OpenCV-android-sdk\samples\face-detection\jni目录到工程app module的main目录下
2.修改jni目录下的Android.mk
(1)第一处修改  去掉#， 对应的off改为on 
```
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
```
（2）第二处修改 仅将OpenCV.mk的路径改为绝对路径，其他不变
```
ifdef OPENCV_ANDROID_SDK
  ifneq ("","$(wildcard $(OPENCV_ANDROID_SDK)/OpenCV.mk)")
    include ${OPENCV_ANDROID_SDK}/OpenCV.mk
  else
    include ${OPENCV_ANDROID_SDK}/sdk/native/jni/OpenCV.mk
  endif
else
  include D:/download/opencv-3.4.3-android-sdk/OpenCV-android-sdk/sdk/native/jni/OpenCV.mk
endif
```
3.修改jni目录下Application.mk。由于在导入OpenCV libs时只拷贝了armeabi 、armeabi-v7a、arm64-v8a
，因此这里指定编译平台也为上述三个；修改APP_PLaTFORM版本为android-16(可根据自身情况而定)，具体如下：
```
 # 指定编译平台
       APP_ABI := armeabi armeabi-v7a arm64-v8a
```

4.修改DetectionBasedTracker_jni.h和DetectionBasedTracker_jni.cpp文件，
将源文件中所有包含前缀“Java_org_opencv_samples_facedetect_”替换为“Java_com_XXX(包名+_)_”，
比如我的项目中替换为Java_com_df_facedetect_DetectionBasedTracker_
，DetectionBasedTracker类包含了人脸检测相关的native方法，否则，在调用自己编译生成的so库时会提示找不到该本地函数错误。

5.在build.gradle中添加
```
  externalNativeBuild{
        ndkBuild{
            path "src/main/jni/Android.mk"
        }
    }
```
6.打开Android Studio中的Terminal窗口，使用cd命令切换到工程jni目录所在位置，并执行ndk-build命令，然后会自动在工程的app/src/main目录下生成libs和obj目录，其中libs目录存放的是目标动态库libdetection_based_tracker.so。
```
cd app
cd src
cd main
cd jni

D:\software\android-ndk-r14b\ndk-build
```


## 优化人脸检测

1、 如果想要优化人脸检测可以考虑从从选择的xml文件是haar的还是lbp的入手

2、如果想优化检测速度可以考虑对mat进行resize

