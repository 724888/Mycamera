package com.example.jiexingxing.mycamera.camera.utils;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 图片工具类
 */
public class ImageUtils {
    //保存图片文件
    public static String saveToFile(Context ctx, String fileFolderStr, boolean isDir, Bitmap croppedImage) throws  IOException {
        File jpgFile;
        if (isDir) {

            File fileFolder = new File(fileFolderStr);

            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间
            String filename = format.format(date) + ".png";
            if (!fileFolder.exists()) { // 如果目录不存在，则创建一个名为"finger"的目录
                FileUtils.getInst().mkdir(fileFolder);
            }
            jpgFile = new File(fileFolder, filename);
        } else {
            jpgFile = new File(fileFolderStr);
            if (!jpgFile.getParentFile().exists()) { // 如果目录不存在，则创建一个名为"finger"的目录
                FileUtils.getInst().mkdir(jpgFile.getParentFile());
            }
        }
        FileOutputStream outputStream = new FileOutputStream(jpgFile); // 文件输出流

        croppedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        IOUtil.closeStream(outputStream);
        return jpgFile.getPath();
    }

}
