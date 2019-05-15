package org.rydc.phototest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import org.rydc.phototest.utils.FileProviderUtils;

import java.io.File;

/**
 * @author D10NG
 * @date on 2019-05-15 09:15
 */
public class MainService {

    private Context mContext;
    public MainView mView;

    /** 拍照输出真实路径 */
    public String tempPhotoPath;
    /** 剪裁输出uri路径 */
    public final Uri cropImgUri = Uri.parse("file:///"+Environment.getExternalStorageDirectory()+"/photo_crop.jpg");

    public static final int CLICK_VIEW = 1;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CLICK_VIEW:
                    // 页面控件点击事件
                    switch (msg.arg1) {
                        case R.id.btn_take_photo:
                            takePhoto();
                            break;
                        case R.id.btn_choose_photo:
                            choosePhoto();
                            break;
                    }
                    break;
            }
        }
    };

    public MainService(Context context) {
        mContext = context;
        mView = new MainView(mContext, mHandler);
    }

    /**
     * 打开相机
     */
    public void takePhoto() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // 未授权，申请授权
            ActivityCompat.requestPermissions((Activity)mContext,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA},
                    MainActivity.RC_TAKE_PHOTO);
            return;
        }
        // 已授权
        Intent intentToTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 设置照片输出位置
        File photoFile = new File(Environment.getExternalStorageDirectory(), "photo.jpg");
        tempPhotoPath = photoFile.getAbsolutePath();
        Uri tempImgUri = FileProviderUtils.getUriForFile(mContext, photoFile);
        intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri);
        ((Activity)mContext).startActivityForResult(intentToTakePhoto, MainActivity.RC_TAKE_PHOTO);
    }

    /**
     * 选图
     */
    public void choosePhoto() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 未授权，申请授权(从相册选择图片需要读取存储卡的权限)
            ActivityCompat.requestPermissions((Activity)mContext,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MainActivity.RC_CHOOSE_PHOTO);
            return;
        }
        // 已授权，获取照片
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        ((Activity)mContext).startActivityForResult(intentToPickPic, MainActivity.RC_CHOOSE_PHOTO);
    }

    /**
     * 剪裁图片
     *
     * @param path
     * @param size
     */
    public void cropPhoto(String path, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        FileProviderUtils.setIntentDataAndType(mContext, intent, "image/*", new File(path), true);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImgUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        ((Activity)mContext).startActivityForResult(intent, MainActivity.RC_CROP_PHOTO);
    }

    /**
     * 显示图片
     */
    public void showPhoto(Uri uri) {
        String path = FileProviderUtils.getFilePathByUri(mContext, uri);
        Log.e("main", "path=" + path);
        if (!TextUtils.isEmpty(path)) {
            // 从文件路径读取文件
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            mView.setImgPhoto(bitmap);
        } else {
            Log.e("main", "没有图片");
        }
    }
}
