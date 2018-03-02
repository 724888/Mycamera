package com.example.jiexingxing.mycamera.camera.base;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;


import com.example.jiexingxing.mycamera.camera.bean.PhotoItem;

import java.io.IOException;
import java.util.Stack;

/**
 * 相机管理类
 * Created by sky on 15/7/6.
 * Weibo: http://weibo.com/2030683111
 * Email: 1132234509@qq.com
 */
public class CameraManager {

    private static CameraManager mInstance;
    private Stack<Activity> cameras = new Stack<Activity>();

    public static CameraManager getInst() {
        if (mInstance == null) {
            synchronized (CameraManager.class) {
                if (mInstance == null)
                    mInstance = new CameraManager();
            }
        }
        return mInstance;
    }

//

    //判断图片是否需要裁剪
    public Bitmap processPhotoItem(Activity activity, PhotoItem photo) {
        Toast.makeText(activity, "保存好了" + photo.getImageUri(), Toast.LENGTH_SHORT).show();
        Uri uri = photo.getImageUri().startsWith("file:") ? Uri.parse(photo
                .getImageUri()) : Uri.parse("file://" + photo.getImageUri());
        Bitmap bitmap = null;
        Bitmap newbitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
            Matrix matrix = new Matrix();
            matrix.postScale(1, 1);// 缩放比例
            newbitmap = Bitmap.createBitmap(bitmap, 200, 200, bitmap.getWidth() / 2, bitmap.getHeight() / 2, matrix, true);
            return newbitmap;

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (null == bitmap) {
            bitmap.recycle();
        }
        if (null == newbitmap) {
            newbitmap.recycle();
        }
        return null;
    }

    public void close() {
        for (Activity act : cameras) {
            try {
                act.finish();
            } catch (Exception e) {

            }
        }
        cameras.clear();
    }

    public void addActivity(Activity act) {
        cameras.add(act);
    }

    public void removeActivity(Activity act) {
        cameras.remove(act);
    }


}
