package com.example.jiexingxing.mycamera.camera.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


/**
 * Created by shiyawei on 17/7/3.
 */

public class BitmapUtil {
    /**
     * @desc 图片转化成base64字符串
     * @author syw
     * created by 17/7/3 下午3:29
     */
//    public static String getImageStr(int id, Context context) {
//        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);
//        byte[] bytes = Bitmap2Bytes(bitmap);
//        //对字节数组Base64编码
//        BASE64Encoder encoder = new BASE64Encoder();
//        //返回Base64编码过的字节数组字符串
//        return encoder.encode(bytes);
//    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 图片质量压缩
     *
     * @param src
     * @param maxByteSize
     * @return
     */
    public static Bitmap compress2BitmapByQuality(Bitmap src, long maxByteSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        src.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        while (baos.toByteArray().length > maxByteSize && quality > 0) {
            baos.reset();
            src.compress(Bitmap.CompressFormat.JPEG, quality -= 5, baos);
        }
        if (quality < 0) return null;
        byte[] bytes = baos.toByteArray();
        Bitmap bit = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bit;
    }

    /**
     * 图片质量压缩
     *
     * @param src
     * @param maxByteSize
     * @return
     */
    public static byte[] compress2ByteArrayByQuality(Bitmap src, long maxByteSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        src.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        while (baos.toByteArray().length > maxByteSize && quality > 0) {
            baos.reset();
            src.compress(Bitmap.CompressFormat.WEBP, quality -= 5, baos);
        }
        if (quality < 0) return null;
        return baos.toByteArray();
    }




    // 将图片转换成base64编码
    public static String getBase64(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //压缩的质量为60%
        bitmap.compress(Bitmap.CompressFormat.PNG, 60, out);
        //生成base64字符
        String base = Base64.encodeToString(out.toByteArray(), Base64.DEFAULT);
        return base;
    }



    /**
     * 得到bitmap的大小
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        // 在低版本中用一行的字节x高度
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }



    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 50;

        while (baos.toByteArray().length / 1024 > 100&&options>=5) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 5;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }








}
