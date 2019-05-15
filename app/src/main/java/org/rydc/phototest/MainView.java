package org.rydc.phototest;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * @author D10NG
 * @date on 2019-05-15 09:08
 */
public class MainView implements View.OnClickListener {
    private Button btnTakePhoto;
    private Button btnChoosePhoto;
    private ImageView imgPhoto;

    private Context mContext;
    private Handler mHandler;
    private View mView = null;

    public MainView(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
        initView();
    }

    private void initView() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        mView = layoutInflater.inflate(R.layout.activity_main, null);

        btnTakePhoto = mView.findViewById(R.id.btn_take_photo);
        btnChoosePhoto = mView.findViewById(R.id.btn_choose_photo);
        imgPhoto = mView.findViewById(R.id.img_photo);

        btnTakePhoto.setOnClickListener(this);
        btnChoosePhoto.setOnClickListener(this);
    }

    public View getView() {
        return mView;
    }

    @Override
    public void onClick(View v) {
        Message message = new Message();
        message.what = MainService.CLICK_VIEW;
        message.arg1 = v.getId();
        mHandler.sendMessage(message);
    }

    /**
     * 显示图片
     * @param bitmap
     */
    public void setImgPhoto(Bitmap bitmap) {
        imgPhoto.setImageBitmap(bitmap);
    }
}
