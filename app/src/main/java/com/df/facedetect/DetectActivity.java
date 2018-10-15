package com.df.facedetect;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/*
 *没有适配6.0以上的权限请求，需要适配的话可以添加Rxpermission或者其他自己封装的
 */
public class DetectActivity extends AppCompatActivity implements
        CameraBridgeViewBase.CvCameraViewListener2 {

    private CameraBridgeViewBase cameraView;
    private CascadeClassifier classifier;
    private CascadeClassifier leftclassifier;
    private Mat mGray;
    private Mat mRgba;
    private int mAbsoluteFaceSize = 0;

    // 手动装载openCV库文件，以保证手机无需安装OpenCV Manager
    static {
        System.loadLibrary("opencv_java3");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_detect);
        cameraView = (CameraBridgeViewBase) findViewById(R.id.camera_view);
        cameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        cameraView.setCvCameraViewListener(this); // 设置相机监听
        initClassifier();
        cameraView.enableView();
    }


    // 初始化窗口设置, 包括全屏、横屏、常亮
    private void initWindowSettings() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void initClassifier() {
        classifier = new CascadeClassifier(getClassifierFile(R.raw.lbpcascade_frontalface, "lbpcascade_frontalface.xml"));
        leftclassifier = new CascadeClassifier(getClassifierFile(R.raw.haarcascade_profileface, "haarcascade_profileface.xml"));
    }

    // 初始化人脸级联分类器，必须先初始化
    private String getClassifierFile(int resId, String fileName) {
        try {
            InputStream is = getResources()
                    .openRawResource(resId);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File cascadeFile = new File(cascadeDir, fileName);
            FileOutputStream os = new FileOutputStream(cascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            return cascadeFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    @Override
    // 这里执行人脸检测的逻辑, 根据OpenCV提供的例子实现(face-detection)
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();
        float mRelativeFaceSize = 0.2f;
        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
        }
        MatOfRect faces = new MatOfRect();
        MatOfRect leftfaces = new MatOfRect();
        MatOfRect rightfaces = new MatOfRect();
        classifier.detectMultiScale(mGray, faces, 1.1, 2, 2,
                new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        Rect[] facesArray = faces.toArray();
        Scalar faceRectColor = new Scalar(0, 255, 0, 255);
        for (Rect faceRect : facesArray)
            Imgproc.rectangle(mRgba, faceRect.tl(), faceRect.br(), faceRectColor, 3);
        //检测左脸
        leftclassifier.detectMultiScale(mGray, leftfaces, 1.1, 2, 2,
                new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        Rect[] leftfacesArray = leftfaces.toArray();
        for (Rect faceRect : leftfacesArray)
            Imgproc.rectangle(mRgba, faceRect.tl(), faceRect.br(), faceRectColor, 3);
        //右脸检测
        Mat matFlip = new Mat();
        Core.flip(mGray, matFlip, 1);
        leftclassifier.detectMultiScale(matFlip, rightfaces, 1.1, 2, 2,
                new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        Rect[] rightfacesArray = rightfaces.toArray();
        for (Rect faceRect : rightfacesArray) {
            Rect realRect = new Rect(mRgba.cols() - (faceRect.x + faceRect.width), faceRect.y, faceRect.width, faceRect.height);
            Imgproc.rectangle(mRgba, realRect.tl(), realRect.br(), faceRectColor, 3);
        }
        return mRgba;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraView != null) {
            cameraView.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.disableView();
    }
}