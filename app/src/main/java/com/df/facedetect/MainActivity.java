package com.df.facedetect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {
    //OpenCV库静态加载并初始化
    static {
        boolean load = OpenCVLoader.initDebug();
        if (load) {
            Log.e("CV", "Open CV Libraries loaded...");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
