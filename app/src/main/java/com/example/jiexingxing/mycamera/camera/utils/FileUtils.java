package com.example.jiexingxing.mycamera.camera.utils;

import android.os.Environment;

import com.example.jiexingxing.mycamera.Myapplication;


import java.io.File;

public class FileUtils {

    private static String BASE_PATH;
    private static String STICKER_BASE_PATH;

    private static FileUtils mInstance;

    public static FileUtils getInst() {
        if (mInstance == null) {
            synchronized (FileUtils.class) {
                if (mInstance == null) {
                    mInstance = new FileUtils();
                }
            }
        }
        return mInstance;
    }

    public String getSystemPhotoPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera";
    }

    private FileUtils() {
        String sdcardState = Environment.getExternalStorageState();
        //如果没SD卡则放缓存
        if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
            BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/stickercamera/";
        } else {
            BASE_PATH = getCacheDirPath();
        }

        STICKER_BASE_PATH = BASE_PATH + "/stickers/";
    }

    public boolean mkdir(File file) {
        while (!file.getParentFile().exists()) {
            mkdir(file.getParentFile());
        }
        return file.mkdir();
    }

    //获取应用的data/data/....File目录
    public String getFilesDirPath() {
        return Myapplication.getContext().getFilesDir().getAbsolutePath();
    }

    //获取应用的data/data/....Cache目录
    public String getCacheDirPath() {
        return Myapplication.getContext().getCacheDir().getAbsolutePath();
    }
}
