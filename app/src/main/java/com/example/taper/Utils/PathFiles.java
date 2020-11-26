package com.example.taper.Utils;

import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.StringReader;

@RequiresApi(api = Build.VERSION_CODES.R)
public class PathFiles {
    public String ROOT_DIR= Environment.getExternalStorageDirectory().getPath();
    public String PICTURES=ROOT_DIR+"/Pictures";
    public String CAMERA=ROOT_DIR+"/DCIM/camera";
//    public String ROOT=Environment.getRootDirectory().getAbsolutePath();
//    public String TTT=Environment.getStorageDirectory().getAbsolutePath();
//    public String pl=TTT+"/DCIM/camera";
    public File ir=Environment.getExternalStorageDirectory();
    public String h=ir.getAbsolutePath();
    public String pic=h+"/DCIM/Camera";

    public String FIREBASE_IMAGE_STORAGE="photos/users/";
}