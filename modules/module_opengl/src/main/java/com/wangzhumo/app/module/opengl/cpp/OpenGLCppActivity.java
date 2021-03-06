package com.wangzhumo.app.module.opengl.cpp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.wangzhumo.app.base.IRoute;
import com.wangzhumo.app.module.opengl.R;
import com.wangzhumo.app.module.opengl.cpp.opengl.CppSurfaceView;
import com.wangzhumo.app.module.opengl.cpp.opengl.NativeOpenGl;
import com.wangzhumo.app.module.opengl.cpp.opengl.SurfaceLifecycle;
import com.wangzhumo.app.module.opengl.databinding.ActivityOpenglCppBinding;
import com.wangzhumo.app.origin.base.BaseBindingActivity;

import java.nio.ByteBuffer;

@Route(path = IRoute.CPPGLES.CPP_GLES)
public class OpenGLCppActivity extends BaseBindingActivity<ActivityOpenglCppBinding> {

    int[] imageResource = {
            R.drawable.image_ash,
            R.drawable.image_dva,
            R.drawable.image_toba,
            R.drawable.image_dva_2,
            R.drawable.opengl_ic_city_night
    };

    private int currentImageIndex = 0;


    @Override
    protected void initViews(@Nullable Bundle savedInstanceState) {
        super.initViews(savedInstanceState);
        // find view
        CppSurfaceView surfaceView = vBinding.surfaceView;

        //native
        NativeOpenGl nativeOpenGl = new NativeOpenGl();
        surfaceView.setNativeOpenGl(nativeOpenGl);
        surfaceView.setLifecycle(new SurfaceLifecycle() {
            @Override
            public void onCreate() {
                Bitmap image = getBitmapByIndex();
                Log.e("Bitmap Bitmap Bitmap ", " width = " + image.getWidth() + " , height = " + image.getHeight());
                nativeOpenGl.setImageData(image.getWidth(), image.getHeight(), getImageByte(image));
                image.recycle();
            }

            @Override
            public void onChange(int width, int height) {

            }
        });

        vBinding.btFilterChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 切换滤镜
                nativeOpenGl.surfaceChangeFilter("gray");
            }
        });

        vBinding.btTextureChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap image = getBitmapByIndex();
                Log.e("Bitmap   ", " width = " + image.getWidth() + " , height = " + image.getHeight());
                nativeOpenGl.setImageData(image.getWidth(), image.getHeight(), getImageByte(image));
                image.recycle();
            }
        });
    }


    public Bitmap getBitmapByIndex() {
        int imageRes = imageResource[currentImageIndex % 5];
        currentImageIndex++;
        return BitmapFactory.decodeResource(getResources(), imageRes);
    }

    public byte[] getImageByte(Bitmap city) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(city.getHeight() * city.getWidth() * 4);
        city.copyPixelsToBuffer(byteBuffer);
        byteBuffer.flip();
        byte[] pixelArr = byteBuffer.array();
        city.recycle();
        return pixelArr;
    }
}