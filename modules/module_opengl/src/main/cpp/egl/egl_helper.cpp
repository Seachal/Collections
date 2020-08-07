//
// Created by wangzhumo on 2020/8/7.
//

#include "../include/egl/egl_helper.h"

// 给参数赋值 - 初始化
EglHelper::EglHelper() {
    mEglDisplay = EGL_NO_DISPLAY;
    mEglSurface = EGL_NO_SURFACE;
    mEglContext = EGL_NO_CONTEXT;
    mEglConfig = NULL;
}


// 析构函数中销毁一下
EglHelper::~EglHelper() {

}


// 创建环境 - 0 成功  other 失败
int EglHelper::initEglEnv(EGLNativeWindowType windowType) {

    //1.创建Display,得到默认的显示设备
    mEglDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (mEglDisplay == EGL_NO_DISPLAY){
        // 如果没有加载到，那就是失败了
        LOGE("EglHelper initEglEnv eglGetDisplay fail");
        return -1;
    }

    //2.初始化默认的显示设备 --eglInitialize
    EGLint *version = new EGLint[2];
    EGLBoolean ret = eglInitialize(mEglDisplay,&version[0],&version[1]);
    if (!ret){
        LOGE("EglHelper initEglEnv eglInitialize error");
        return -1;
    }

    //3.设置显示设备的属性
    const EGLint attribs[] = {
            EGL_RED_SIZE,8,
            EGL_GREEN_SIZE,8,
            EGL_BLUE_SIZE,8,
            EGL_ALPHA_SIZE,8,
            EGL_DEPTH_SIZE,8,
            EGL_STENCIL_SIZE,8,
            EGL_RENDERABLE_TYPE,EGL_OPENGL_ES2_BIT,
            EGL_NONE
    };

    //4.获取系统中对应的属性配置   --eglChooseConfig
    EGLint config_result;
    // 这里的 1  ， &config_result  的1，是为了兼容一些手机
    // 第一次调用eglChooseConfig，得到了 config_result的值
    if(!eglChooseConfig(mEglDisplay,attribs,NULL,1,&config_result)){
        LOGE("EglHelper initEglEnv eglChooseConfig 1 error");
        return -1;
    }

    if(!eglChooseConfig(mEglDisplay,attribs,mEglConfig,config_result,&config_result)){
        LOGE("EglHelper initEglEnv eglChooseConfig 2 error");
        return -1;
    }

    //5.创建EglContext  --eglCreateContext
    int attrib_list[] = {
            EGL_CONTEXT_CLIENT_VERSION,2,
            EGL_NONE
    };
    // 此处的EGL_NO_CONTEXT 表示可以共享的一个 context,这里无需共享就传入 NO_CONTEXT
    eglCreateContext(mEglDisplay,mEglConfig,EGL_NO_CONTEXT,attrib_list);
    if (mEglContext == EGL_NO_CONTEXT){
        LOGE("EglHelper initEglEnv eglCreateContext  error");
        return -1;
    }

    //6.创建渲染的Surface  --eglCreateWidnowSurface
    mEglSurface = eglCreateWindowSurface(mEglDisplay,mEglConfig,windowType,NULL);
    if (mEglSurface == EGL_NO_SURFACE){
        LOGE("EglHelper initEglEnv eglCreateWindowSurface  error");
        return -1;
    }

    //7.绑定EglContext和Surface到显示设备中  --eglMakeCurrent
    // draw  read 同一个surface
    EGLBoolean eglBoolean = eglMakeCurrent(mEglDisplay,mEglSurface,mEglSurface,mEglSurface,mEglContext);
    if (!eglBoolean){
        LOGE("EglHelper initEglEnv eglMakeCurrent  error");
        return -1;
    }
    LOGE("EglHelper initEglEnv complete")
    return 0;
}


// 8.刷新数据，显示渲染场景  --eglSwapBuffer
int EglHelper::swapBuffer() {
    if (mEglDisplay != EGL_NO_DISPLAY && mEglSurface != EGL_NO_SURFACE){
        // 如果成功，返回一个 0
        if (eglSwapBuffers(mEglDisplay,mEglSurface)){
            return 0;
        }
    }
    return -1;
}


// 销毁这个EGL的环境
void EglHelper::release() {
    if (mEglDisplay != EGL_NO_DISPLAY){
        eglMakeCurrent(mEglDisplay,EGL_NO_SURFACE,EGL_NO_SURFACE,EGL_NO_CONTEXT);
    }
    if (mEglDisplay != EGL_NO_DISPLAY && mEglSurface != EGL_NO_SURFACE){
        eglDestroySurface(mEglDisplay,mEglSurface);
        mEglSurface = EGL_NO_SURFACE;
    }
    if (mEglDisplay != EGL_NO_DISPLAY && mEglContext != EGL_NO_CONTEXT){
        eglDestroyContext(mEglDisplay,mEglContext);
        mEglContext = EGL_NO_CONTEXT;
    }
    if (mEglDisplay != EGL_NO_DISPLAY){
        eglTerminate(mEglDisplay);
        mEglDisplay = EGL_NO_DISPLAY;
    }
}