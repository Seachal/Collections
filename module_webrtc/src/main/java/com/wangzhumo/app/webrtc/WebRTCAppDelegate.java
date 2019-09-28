package com.wangzhumo.app.webrtc;

import android.app.Application;
import android.util.Log;

import com.wangzhumo.app.base.delegate.AppDelegate;
import com.wangzhumo.app.base.delegate.IApp;
import com.wangzhumo.app.base.delegate.IAppConstant;

/**
 * If you have any questions, you can contact by email {wangzhumoo@gmail.com}
 *
 * @author 王诛魔 2019-09-28  17:52
 */
@IApp(name = IAppConstant.WEBRTC)
public class WebRTCAppDelegate implements AppDelegate {
    @Override
    public void init(Application application) {
        Log.e("AppDelegate", "WebRTCAppDelegate  = " + application);
    }
}
