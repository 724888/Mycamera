package com.example.jiexingxing.mycamera.camera.scan;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.jiexingxing.mycamera.R;
import com.example.jiexingxing.mycamera.camera.base.CameraHelper;
import com.example.jiexingxing.mycamera.camera.bean.PhotoItem;
import com.example.jiexingxing.mycamera.camera.utils.BitmapUtil;
import com.example.jiexingxing.mycamera.camera.utils.DisplayUtil;
import com.example.jiexingxing.mycamera.camera.utils.FileUtils;
import com.example.jiexingxing.mycamera.camera.utils.IOUtil;
import com.example.jiexingxing.mycamera.camera.utils.ImageUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;




/**
 * 相机界面
 * Created by sky on 15/7/6.
 */
public class CameraActivity extends CameraBaseActivity  {

    private CameraHelper mCameraHelper;
    private Camera.Parameters parameters = null;
    private Camera cameraInst = null;
    private Bundle bundle = null;
    private float pointX, pointY;
    static final int FOCUS = 1;            // 聚焦
    static final int ZOOM = 1;            // 缩放
    private int mode;                      //0是聚焦 1是放大
    private float dist;
    private int PHOTO_SIZE = 2000;
    private int mCurrentCameraId = 0;  //1是前置 0是后置
    private Handler handler = new Handler();
    private LinearLayout photoArea;
    private View takePhotoPanel;
    private Button takePicture;
    private ImageView flashBtn;
    //    private ImageView changeBtn;
    private View focusIndex;
    private SurfaceView surfaceView;
    private int height;
    private TextView stamp_tv;
    private TextView mMakesure;
    private TextView mReplay;
    //    private TextView yes;
    private ImageView mCperture;
    private ImageView mCperture1;
    boolean isfirst = true;
    int useHeight = 0;
    int usewidth = 0;
    Bitmap newbitmap = null;
    private String imagePath = "";//整张图片存储路径
    //    private String bitmapfile;
    private boolean success;

    private int surfaceViewwidth;
    private int surfaceViewheight;
    private Bitmap mBitmap;

    private ImageView ivTitleLeft;
    private TextView tvTitleCenter;
    private TextView tvTitleRight;
    private ImageView ivTitleRight;
    private int leftwidth;
    private Bitmap smallbitmap;
    private TextView tagtextview;
    private TextView desc_tv;

    public int getDpi(Activity activity) {//获取屏幕参数
        int dpi = 0;
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            dpi = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;
    }

    int mCperturewidth;
    int mCpertureheight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStatusBarColor(R.color.gray_ABABAB);
        setContentView(R.layout.activity_camera);

        mCameraHelper = new CameraHelper(this);//初始化相机

        ivTitleLeft = (ImageView) findViewById(R.id.ivTitleLeft);
        tvTitleCenter = (TextView) findViewById(R.id.tvTitleCenter);
        tvTitleRight = (TextView) findViewById(R.id.tvTitleRight);
        ivTitleRight = (ImageView) findViewById(R.id.ivTitleRight);
        tagtextview = (TextView) findViewById(R.id.tag);
        desc_tv = (TextView) findViewById(R.id.desc_tv);

        ivTitleLeft.setVisibility(View.VISIBLE);
        ivTitleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvTitleCenter.setText(R.string.cameratitle);
        tvTitleCenter.setTextColor(ContextCompat.getColor(this, R.color.black_000000));


        tvTitleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        usewidth = getWindowManager().getDefaultDisplay().getWidth();
//        获取屏幕的高
        useHeight = getWindowManager().getDefaultDisplay().getHeight() + DisplayUtil.getStatusHeight(CameraActivity.this) + DisplayUtil.getBottomStatusHeight(this);
        int allHeight = getDpi(this);
        photoArea = (LinearLayout) findViewById(R.id.photo_area);
        takePhotoPanel = findViewById(R.id.panel_take_photo);
        takePicture = (Button) findViewById(R.id.takepicture);
        flashBtn = (ImageView) findViewById(R.id.flashBtn);
        mCperture = (ImageView) findViewById(R.id.activity_camera_aperture);
        mCperture1 = (ImageView) findViewById(R.id.activity_camera_aperture1);
        mReplay = (TextView) findViewById(R.id.activity_camera_replay);
        mMakesure = (TextView) findViewById(R.id.activity_camera_makesure);
        stamp_tv = (TextView) findViewById(R.id.stamp_tv);

       mCperturewidth = usewidth - DisplayUtil.dip2px(CameraActivity.this, 30.0f);// 项目要求  左边和右边各有15dip的距离（就是驾驶证的宽）
        mCpertureheight = (int) ((float) mCperturewidth * 60 / 88);// 获取的是驾驶证的高（驾驶证的宽高比例）


        ImageView top_image = (ImageView) findViewById(R.id.top_image);
        ImageView bottom_image = (ImageView) findViewById(R.id.bottom_image);

        leftwidth = DisplayUtil.dip2px(CameraActivity.this, 15.0f);
        int topHeight = DisplayUtil.dip2px(CameraActivity.this, 100.0f);





        int endheight = useHeight - topHeight - mCpertureheight;
        ImageView left_image = (ImageView) findViewById(R.id.left_image);
        ImageView right_image = (ImageView) findViewById(R.id.right_image);

//动态计算  左侧 高度  宽度
        ViewGroup.LayoutParams leftparam = left_image.getLayoutParams();
        leftparam.width = leftwidth;
        leftparam.height = mCpertureheight;
        left_image.setLayoutParams(leftparam);
//动态计算  右侧 高度   宽度
        ViewGroup.LayoutParams rightimageparam = right_image.getLayoutParams();
        rightimageparam.width = leftwidth;
        rightimageparam.height = mCpertureheight;
        right_image.setLayoutParams(rightimageparam);


        ViewGroup.LayoutParams param = mCperture.getLayoutParams();
        param.width = mCperturewidth;
        param.height = mCpertureheight;
        mCperture.setLayoutParams(param);

//动态计算  底部 高度   宽度
        ViewGroup.LayoutParams bottom_imageparam = bottom_image.getLayoutParams();
        bottom_imageparam.width = usewidth;
        bottom_imageparam.height = endheight;
        bottom_image.setLayoutParams(bottom_imageparam);







//动态计算  左侧印章（红框部分）  底部 高度   宽度
        ViewGroup.LayoutParams param1 = mCperture1.getLayoutParams();
        param1.width = mCperturewidth;
        param1.height = mCpertureheight;
        mCperture1.setLayoutParams(param);
        int stampwidth = mCpertureheight / 3;//印章的宽高

        ViewGroup.LayoutParams para1 = stamp_tv.getLayoutParams();
        para1.width = stampwidth;
        para1.height = stampwidth;
        ViewGroup.MarginLayoutParams marginParams = null;
        //获取view的margin设置参数
        if (para1 instanceof ViewGroup.MarginLayoutParams) {
            marginParams = (ViewGroup.MarginLayoutParams) para1;
        } else {
            //不存在时创建一个新的参数
            //基于View本身原有的布局参数对象
            marginParams = new ViewGroup.MarginLayoutParams(para1);
        }
        marginParams.setMargins(stampwidth * 9 / 40, 0, 0, stampwidth / 10);
        stamp_tv.setLayoutParams(para1);






        focusIndex = findViewById(R.id.focus_index);
        View mCameraView = findViewById(R.id.activity_camera_view);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        initView();
        initEvent();




        ///适配华为等具有虚拟按键的手机
        if (useHeight < allHeight) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = allHeight - useHeight;
            params1.height = allHeight - useHeight;
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            takePhotoPanel.setLayoutParams(params);
            mCameraView.setLayoutParams(params1);
        }


    }

    private void initView() {


        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.setKeepScreenOn(true);
        surfaceView.setFocusable(true);
        surfaceView.setBackgroundColor(TRIM_MEMORY_COMPLETE);

        surfaceView.getHolder().addCallback(new SurfaceCallback());//为SurfaceView的句柄添加一个回调函数

    }


    private void initEvent() {

        mReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cameraInst == null)
                    return;
                cameraInst.startPreview();
                mReplay.setVisibility(View.INVISIBLE);
                mMakesure.setVisibility(View.INVISIBLE);
                takePicture.setVisibility(View.VISIBLE);
                mCperture.setVisibility(View.INVISIBLE);
                mCperture1.setVisibility(View.VISIBLE);
                stamp_tv.setVisibility(View.VISIBLE);
                tagtextview.setVisibility(View.VISIBLE);
                desc_tv.setVisibility(View.VISIBLE);


                int mCperturewidth = usewidth - DisplayUtil.dip2px(CameraActivity.this, 30.0f);
                int mCpertureheight = (int) ((float) mCperturewidth * 60 / 88);


                ViewGroup.LayoutParams param = mCperture.getLayoutParams();
                param.width = mCperturewidth;
                param.height = mCpertureheight;
                mCperture.setLayoutParams(param);

                ViewGroup.LayoutParams param1 = mCperture1.getLayoutParams();
                param1.width = mCperturewidth;
                param1.height = mCpertureheight;
                mCperture1.setLayoutParams(param);
                int stampwidth = mCpertureheight / 3;//印章的宽高

                ViewGroup.LayoutParams para1 = stamp_tv.getLayoutParams();
                para1.width = stampwidth;
                para1.height = stampwidth;
                ViewGroup.MarginLayoutParams marginParams = null;
                //获取view的margin设置参数
                if (para1 instanceof ViewGroup.MarginLayoutParams) {
                    marginParams = (ViewGroup.MarginLayoutParams) para1;
                } else {
                    //不存在时创建一个新的参数
                    //基于View本身原有的布局参数对象
                    marginParams = new ViewGroup.MarginLayoutParams(para1);
                }
                marginParams.setMargins(stampwidth * 9 / 40, 0, 0, stampwidth / 10);
                stamp_tv.setLayoutParams(para1);


            }
        });


        mMakesure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                imagePath


                Toast.makeText(CameraActivity.this,"截图的文件路径   ："+imagePath,Toast.LENGTH_SHORT).show();

            }
        });


//       拍照
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    stamp_tv.setVisibility(View.GONE);
                    cameraInst.takePicture(null, null, new MyPictureCallback());
                } catch (Throwable t) {
                    t.printStackTrace();
                    try {
                        cameraInst.startPreview();
                    } catch (Throwable e) {

                    }
                }
            }
        });
        //闪光灯
        flashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnLight(cameraInst);
            }
        });


        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    // 主点按下
                    case MotionEvent.ACTION_DOWN:
                        pointX = event.getX();
                        pointY = event.getY();
                        mode = FOCUS;
                        break;
                    // 副点按下
                    case MotionEvent.ACTION_POINTER_DOWN:
                        dist = spacing(event);
                        // 如果连续两点距离大于10，则判定为多点模式
                        if (spacing(event) > 10f) {
                            mode = ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = FOCUS;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == FOCUS) {
                            //pointFocus((int) event.getRawX(), (int) event.getRawY());
                        } else if (mode == ZOOM) {
                            float newDist = spacing(event);
                            if (newDist > 10f) {
                                float tScale = (newDist - dist) / dist;
                                if (tScale < 0) {
                                    tScale = tScale * 10;
                                }
                                addZoomIn((int) tScale);
                            }
                        }
                        break;
                }
                return false;
            }
        });

        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    pointFocus((int) pointX, (int) pointY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(focusIndex.getLayoutParams());
                layout.setMargins((int) pointX - 60, (int) pointY - 60, 0, 0);
                focusIndex.setLayoutParams(layout);
                focusIndex.setVisibility(View.VISIBLE);
                ScaleAnimation sa = new ScaleAnimation(3f, 1f, 3f, 1f,
                        ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
                sa.setDuration(800);
                focusIndex.startAnimation(sa);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        focusIndex.setVisibility(View.INVISIBLE);
                    }
                }, 800);

            }
        });
        //防止聚焦框出现在白色的位置
        takePhotoPanel.setOnClickListener(null);
    }


    /**
     * 两点的距离
     */
    private float spacing(MotionEvent event) {
        if (event == null) {
            return 0;
        }
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    //放大缩小
    int curZoomValue = 0;

    private void addZoomIn(int delta) {

        try {
            Camera.Parameters params = cameraInst.getParameters();
            Log.d("Camera", "Is support Zoom " + params.isZoomSupported());
            if (!params.isZoomSupported()) {
                return;
            }
            curZoomValue += delta;
            if (curZoomValue < 0) {
                curZoomValue = 0;
            } else if (curZoomValue > params.getMaxZoom()) {
                curZoomValue = params.getMaxZoom();
            }

            if (!params.isSmoothZoomSupported()) {
                params.setZoom(curZoomValue);
                cameraInst.setParameters(params);
                return;
            } else {
                cameraInst.startSmoothZoom(curZoomValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //定点对焦的代码
    private void pointFocus(int x, int y) {
        cameraInst.cancelAutoFocus();
        parameters = cameraInst.getParameters();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            showPoint(x, y);
        }
        cameraInst.setParameters(parameters);
        autoFocus();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void showPoint(int x, int y) {
        if (parameters.getMaxNumMeteringAreas() > 0) {
            List<Camera.Area> areas = new ArrayList<Camera.Area>();
            //xy变换了
            int rectY = -x * 2000 / DisplayUtil.getScreenWidth(this) + 1000;
            int rectX = y * 2000 / DisplayUtil.getScreenHeight(this) - 1000;

            int left = rectX < -900 ? -1000 : rectX - 100;
            int top = rectY < -900 ? -1000 : rectY - 100;
            int right = rectX > 900 ? 1000 : rectX + 100;
            int bottom = rectY > 900 ? 1000 : rectY + 100;
            Rect area1 = new Rect(left, top, right, bottom);
            areas.add(new Camera.Area(area1, 800));
            parameters.setMeteringAreas(areas);
        }

        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
    }



    private final class MyPictureCallback implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {


            bundle = new Bundle();
            bundle.putByteArray("bytes", data); //将图片字节数据保存在bundle当中，实现数据交换


            new SavePicTask(data).execute();
            camera.startPreview(); // 拍完照后，重新开始预览
        }
    }


    public boolean isNotEmpty(String str) {
        return ((str != null) && (str.trim().length() > 0));
    }


    /*SurfaceCallback*/
    private final class SurfaceCallback implements SurfaceHolder.Callback {

        public void surfaceDestroyed(SurfaceHolder holder) {
            try {
                if (cameraInst != null) {
                    cameraInst.stopPreview();
                    cameraInst.release();
                    cameraInst = null;
                }
            } catch (Exception e) {
                //相机已经关了
            }

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (null == cameraInst) {
                try {
                    cameraInst = Camera.open();
                    cameraInst.setPreviewDisplay(holder);

                    initCamera();
                    cameraInst.startPreview();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            autoFocus();
        }
    }

    //实现自动对焦
    private void autoFocus() {
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (cameraInst == null) {
                    return;
                }
                cameraInst.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success) {
                            initCamera();//实现相机的参数初始化
                        }
                    }
                });
            }
        };
    }

    private Camera.Size adapterSize = null;
    private Camera.Size previewSize = null;

    private void initCamera() {
        parameters = cameraInst.getParameters();
        parameters.setPictureFormat(PixelFormat.JPEG);
        if (adapterSize == null) {
            setUpPreviewSize(parameters);
            setUpPicSize(parameters);
        }
//        if (adapterSize != null) {
//            parameters.setPictureSize(previewSize.width, previewSize.height);
//        }
//        if (previewSize != null) {
//            parameters.setPreviewSize(previewSize.width, previewSize.height);
//        }
        // TODO: 17/8/26


        parameters.setPictureSize(previewSize.width, previewSize.height);
        parameters.setPreviewSize(previewSize.width, previewSize.height);


        // TODO: 17/8/26

        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
        } else {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        setDispaly(parameters, cameraInst);


        try {
            cameraInst.setParameters(parameters);

        } catch (Exception e) {
            e.printStackTrace();

        }

        cameraInst.startPreview();
        cameraInst.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
    }

    private void setUpPicSize(Camera.Parameters parameters) {

        if (adapterSize != null) {
            return;
        } else {
            adapterSize = findBestPreviewResolution();
            return;
        }
    }

    private void setUpPreviewSize(Camera.Parameters parameters) {

        if (previewSize != null) {
            return;
        } else {
            previewSize = findBestPreviewResolution();
        }
    }

    /**
     * 最小预览界面的分辨率
     */
    private static final int MIN_PREVIEW_PIXELS = 640 * 480;
    /**
     * 最大宽高比差
     */
    private static final double MAX_ASPECT_DISTORTION = 0.15;
    private static final String TAG = "Camera";

    /**
     * 找出最适合的预览界面分辨率
     *
     * @return
     */
    private Camera.Size findBestPreviewResolution() {
        Camera.Parameters cameraParameters = cameraInst.getParameters();
        Camera.Size defaultPreviewResolution = cameraParameters.getPreviewSize();

        List<Camera.Size> rawSupportedSizes = cameraParameters.getSupportedPreviewSizes();

        Camera.Size optimalSize = getOptimalPreviewSize(rawSupportedSizes, 640, 480);

        if (rawSupportedSizes == null) {
            return defaultPreviewResolution;
        }

        // 按照分辨率从大到小排序
        List<Camera.Size> supportedPreviewResolutions = new ArrayList<Camera.Size>(rawSupportedSizes);
        Collections.sort(supportedPreviewResolutions, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size a, Camera.Size b) {
                int aPixels = a.height * a.width;
                int bPixels = b.height * b.width;
                if (bPixels < aPixels) {
                    return -1;
                }
                if (bPixels > aPixels) {
                    return 1;
                }
                return 0;
            }
        });

        StringBuilder previewResolutionSb = new StringBuilder();
        for (Camera.Size supportedPreviewResolution : supportedPreviewResolutions) {
            previewResolutionSb.append(supportedPreviewResolution.width).append('x').append(supportedPreviewResolution.height)
                    .append(' ');
        }
        Log.v(TAG, "Supported preview resolutions: " + previewResolutionSb);


        // 移除不符合条件的分辨率
        double screenAspectRatio = (double) DisplayUtil.getScreenWidth(this)
                / (double) DisplayUtil.getScreenHeight(this);
        Iterator<Camera.Size> it = supportedPreviewResolutions.iterator();
        while (it.hasNext()) {
            Camera.Size supportedPreviewResolution = it.next();
            int width = supportedPreviewResolution.width;
            int height = supportedPreviewResolution.height;

            // 移除低于下限的分辨率，尽可能取高分辨率
            if (width * height < MIN_PREVIEW_PIXELS) {
                it.remove();
                continue;
            }

            // 在camera分辨率与屏幕分辨率宽高比不相等的情况下，找出差距最小的一组分辨率
            // 由于camera的分辨率是width>height，我们设置的portrait模式中，width<height
            // 因此这里要先交换然preview宽高比后在比较
            boolean isCandidatePortrait = width > height;
            int maybeFlippedWidth = isCandidatePortrait ? height : width;
            int maybeFlippedHeight = isCandidatePortrait ? width : height;
            double aspectRatio = (double) maybeFlippedWidth / (double) maybeFlippedHeight;
            double distortion = Math.abs(aspectRatio - screenAspectRatio);
            if (distortion > MAX_ASPECT_DISTORTION) {
                it.remove();
                continue;
            }

            // 找到与屏幕分辨率完全匹配的预览界面分辨率直接返回
            if (maybeFlippedWidth == DisplayUtil.getScreenWidth(this)
                    && maybeFlippedHeight == DisplayUtil.getScreenHeight(this)) {
                return supportedPreviewResolution;
            }
        }

        // 如果没有找到合适的，并且还有候选的像素，则设置其中最大比例的，对于配置比较低的机器不太合适
        if (!supportedPreviewResolutions.isEmpty()) {
            Camera.Size largestPreview = supportedPreviewResolutions.get(0);
            return largestPreview;
        }

        // 没有找到合适的，就返回默认的

        return defaultPreviewResolution;
    }




    //控制图像的正确显示方向
    private void setDispaly(Camera.Parameters parameters, Camera camera) {
        if (Build.VERSION.SDK_INT >= 8) {
            setDisplayOrientation(camera, 90);
        } else {
            parameters.setRotation(90);
        }
    }

    //实现的图像的正确显示
    private void setDisplayOrientation(Camera camera, int i) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation",
                    new Class[]{int.class});
            if (downPolymorphic != null) {
                downPolymorphic.invoke(camera, new Object[]{i});
            }
        } catch (Exception e) {

        }
    }


    /**
     * 将拍下来的照片存放在SD卡中
     *
     * @param data
     * @throws IOException
     */
    public String saveToSDCard(byte[] data) throws IOException {
        Bitmap croppedImage;

        //获得图片大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        PHOTO_SIZE = options.outWidth;
        height = options.outHeight;
        options.inJustDecodeBounds = false;
        Rect r;
        if (mCurrentCameraId == 1) {
            r = new Rect(0, 0, PHOTO_SIZE, height);
        } else {
            r = new Rect(0, 0, PHOTO_SIZE, height);
        }
        try {
            croppedImage = decodeRegionCrop(data, r);
        } catch (Exception e) {
            return null;
        }



        if (croppedImage != null) {
            smallbitmap = BitmapUtil.compressImage(croppedImage);

        }


        imagePath = ImageUtils.saveToFile(CameraActivity.this, FileUtils.getInst().getSystemPhotoPath(), true,
                smallbitmap);
        croppedImage.recycle();
        smallbitmap.recycle();

        return imagePath;
    }

    private Bitmap decodeRegionCrop(byte[] data, Rect rect) {

        InputStream is = null;
        System.gc();
        Bitmap croppedImage = null;
        try {
            is = new ByteArrayInputStream(data);
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, false);

            try {
                croppedImage = decoder.decodeRegion(rect, new BitmapFactory.Options());
            } catch (IllegalArgumentException e) {
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeStream(is);
        }
        Matrix m = new Matrix();
        m.setRotate(90, PHOTO_SIZE, height);
        if (mCurrentCameraId == 1) {
            m.postScale(1, -1);
        }
        Bitmap rotatedImage = Bitmap.createBitmap(croppedImage, 0, 0, PHOTO_SIZE, height, m, true);//
        if (rotatedImage != croppedImage)
            croppedImage.recycle();
        return rotatedImage;
    }

    /**
     * 闪光灯开关   开->关->自动
     *
     * @param mCamera
     */
    private void turnLight(Camera mCamera) {
        if (mCamera == null || mCamera.getParameters() == null
                || mCamera.getParameters().getSupportedFlashModes() == null) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        String flashMode = mCamera.getParameters().getFlashMode();
        List<String> supportedModes = mCamera.getParameters().getSupportedFlashModes();
        if (Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)
                && supportedModes.contains(Camera.Parameters.FLASH_MODE_ON)) {//关闭状态
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            mCamera.setParameters(parameters);
            flashBtn.setImageResource(R.drawable.camera_flash_on);
        } else if (Camera.Parameters.FLASH_MODE_ON.equals(flashMode)) {//开启状态
            if (supportedModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                flashBtn.setImageResource(R.drawable.camera_flash_auto);
                mCamera.setParameters(parameters);
            } else if (supportedModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                flashBtn.setImageResource(R.drawable.camera_flash_off);
                mCamera.setParameters(parameters);
            }
        } else if (Camera.Parameters.FLASH_MODE_AUTO.equals(flashMode)
                && supportedModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
            flashBtn.setImageResource(R.drawable.camera_flash_off);
        }
    }





    private class SavePicTask extends AsyncTask<Void, Void, String> {
        private byte[] data;

        protected void onPreExecute() {
            Toast.makeText(CameraActivity.this, "处理中", Toast.LENGTH_LONG).show();
        }


        SavePicTask(byte[] data) {
            this.data = data;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                return saveToSDCard(data);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("jxx", "1103   " + result);

            if (isNotEmpty(result)) {
                PhotoItem photo = new PhotoItem(result, System.currentTimeMillis());
                Uri uri = photo.getImageUri().startsWith("file:") ? Uri.parse(photo
                        .getImageUri()) : Uri.parse("file://" + photo.getImageUri());
                Bitmap bitmap = null;
                Log.e("jxx", "1110   " + uri);
                takePicture.setVisibility(View.INVISIBLE);
                mReplay.setVisibility(View.VISIBLE);
                mMakesure.setVisibility(View.VISIBLE);
                mCperture.setVisibility(View.INVISIBLE);
                mCperture1.setVisibility(View.INVISIBLE);
                tagtextview.setVisibility(View.GONE);
                desc_tv.setVisibility(View.GONE);
                try {
                    int[] location = new int[2];
                    mCperture.getLocationOnScreen(location);
                    int x = location[0];//  屏幕的起点x
                    int y = location[1];//  屏幕的起点y
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    int begainx = x * bitmap.getWidth() / usewidth;
                    int width = mCperture.getWidth() * bitmap.getWidth() / usewidth;
                    int begainy = y * bitmap.getHeight() / useHeight;
                    int height = mCperture.getHeight() * bitmap.getHeight() / useHeight;
                    Matrix matrix = new Matrix();
                    Bitmap endbitmap = Bitmap.createBitmap(bitmap, begainx, begainy, width, height + 10, matrix, true);




                    if (endbitmap != null) {
                        imagePath = ImageUtils.saveToFile(CameraActivity.this, FileUtils.getInst().getSystemPhotoPath(), true,
                                endbitmap);
                    }
                    mCperture.setImageBitmap(endbitmap);
                    mCperture.setVisibility(View.VISIBLE);
                    mCperture1.setVisibility(View.GONE);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {

            }
        }
    }


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }


        return optimalSize;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }




}
