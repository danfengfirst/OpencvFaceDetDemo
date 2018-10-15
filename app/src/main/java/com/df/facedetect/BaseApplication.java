package com.df.facedetect;

import android.app.Application;
import android.util.Log;

import org.opencv.android.OpenCVLoader;

/**
 * Created by Danfeng on 2018/10/8.
 */

public class BaseApplication extends Application {
    static {
        boolean load = OpenCVLoader.initDebug();
        if (load) {
            Log.e("CV", "Open CV Libraries loaded...");
        }
    }

}
