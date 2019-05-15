package org.rydc.phototest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.rydc.phototest.utils.FileProviderUtils;

/**
 * 首页
 * -------------------------
 * 1、点击拍照，先判断是否有相机权限和文件写入读取权限，没有就请求，有就打开相机
 * 2、点击选择照片，先判断是否有文件读取权限，没有就请求，有就打开图册
 * 3、拿到照片返回进行剪裁
 * 4、剪裁成功后显示
 * @author D10NG
 * @date on 2019-05-15 09:02
 */
public class MainActivity extends AppCompatActivity {
    private Context mContext = this;
    private MainService mService;

    public static final int RC_CHOOSE_PHOTO = 10;
    public static final int RC_TAKE_PHOTO = 11;
    public static final int RC_CROP_PHOTO = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mService = new MainService(mContext);
        setContentView(mService.mView.getView());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Toast.makeText(mContext, "操作取消", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (requestCode) {
            case RC_CHOOSE_PHOTO:
                if (null == data) {
                    Toast.makeText(mContext, "没有拿到图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                Uri uri = data.getData();
                if (null == uri) {
                    Toast.makeText(mContext, "没有拿到图片路径", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 剪裁图片
                mService.cropPhoto(FileProviderUtils.getFilePathByUri(mContext, uri), 200);
                break;
            case RC_TAKE_PHOTO:
                // 剪裁图片
                mService.cropPhoto(mService.tempPhotoPath, 200);
                break;
            case RC_CROP_PHOTO:
                // 显示图片
                mService.showPhoto(mService.cropImgUri);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allPass = true;
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                allPass = false;
            }
        }
        if (!allPass) {
            Toast.makeText(mContext, "没有获得相应权限", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (requestCode) {
            case RC_CHOOSE_PHOTO:
                // 继续去打开图册
                mService.choosePhoto();
                break;
            case RC_TAKE_PHOTO:
                // 继续去拍照
                mService.takePhoto();
                break;
        }
    }
}
