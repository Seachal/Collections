package com.wangzhumo.app.module.media.opengl.camera

import android.graphics.SurfaceTexture
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLSurface
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.view.TextureView


/**
 * If you have any questions, you can contact by email {wangzhumoo@gmail.com}
 *
 * @author 王诛魔 2019-11-21  17:32
 *
 * 1.initEGLContext
 * 2.loadOESTexture()  提供 SurfaceTexture 给Camera
 * 3.HandlerThread  初始化/渲染
 */
class TextureEGLHelper : SurfaceTexture.OnFrameAvailableListener {

    /**
     * 渲染/初始化 线程
     */
    private var mHandlerThread: HandlerThread? = null

    /**
     * 渲染器
     */
    private var mRenderer: ITextureRenderer? = null
    /**
     * mHandlerThread 内部的Handler
     */
    private var mHandler: Handler? = null

    /**
     * 最终显示的TextureView
     */
    private var mTextureView: TextureView? = null

    /**
     * 纹理ID
     */
    private var mOESTextureId = 0

    /**
     * 显示设备
     */
    private var mEGLDisplay = EGL14.EGL_NO_DISPLAY

    /**
     * EGL上下文
     */
    private var mEGLContext = EGL14.EGL_NO_CONTEXT

    /**
     * 描述帧缓冲区配置参数
     */
    private val configs = arrayOfNulls<EGLConfig>(1)

    /**
     * EGL绘图表面
     */
    private var mEglSurface: EGLSurface? = null

    /**
     * 自定义的SurfaceTexture - 实际上接收Camera上的数据
     */
    private val mOESSurfaceTexture: SurfaceTexture? = null


    /**
     * @param textureView  外部的TextureView
     * @param textureId    提供的TextureID
     */
    fun initEGL(textureView: TextureView, textureId: Int) {
        mTextureView = textureView
        mOESTextureId = textureId
        mHandlerThread = HandlerThread("Renderer Thread")
        mHandlerThread?.start()

        //此处的Handler，用于处理各种发送过来的命令
        mHandler = object : Handler(mHandlerThread?.looper) {
            override fun handleMessage(msg: Message) {

                when (msg.what) {
                    MSG_INIT -> {
                        //initEGLContext
                        initEGLContext()   //初始化EGL环境
                        initEGLRenderer()   //初始化渲染器
                        //初始化 Renderer.
                    }
                    MSG_RENDER -> {
                        //开始渲染，onFrameAvailable中发送，由mHandlerThread负责渲染的调用。
                    }
                    MSG_DESTROY -> {
                        //mHandlerThread 关闭
                        //停止mHandler
                        //销毁资源
                    }
                    else -> return
                }
            }
        }
    }


    /**
     * 初始化EGLContext
     */
    private fun initEGLContext() {
        mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        //需判断是否成功获取EGLDisplay
        if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            throw  RuntimeException("Unable to get EGL14 display")
        }
        val versions = IntArray(2)
        versions[0] = 3
        if (!EGL14.eglInitialize(mEGLDisplay, versions, 0, versions, 1)) {
            throw RuntimeException("eglInitialize failed! " + EGL14.eglGetError())
        }

        //egl的一些配置
        val eglConfigAttribute = intArrayOf(
            EGL14.EGL_BUFFER_SIZE, 32,
            EGL14.EGL_RED_SIZE, 8,
            EGL14.EGL_GREEN_SIZE, 8,
            EGL14.EGL_BLUE_SIZE, 8,
            EGL14.EGL_ALPHA_SIZE, 8,
            EGL14.EGL_RENDERABLE_TYPE, 4,
            EGL14.EGL_SURFACE_TYPE, EGL14.EGL_WINDOW_BIT,
            EGL14.EGL_NONE
        )
        val numConfig = IntArray(1)
        val eglConfig = arrayOfNulls<EGLConfig>(1)

        //EGL配置
        val eglChooseFlag = EGL14.eglChooseConfig(
            mEGLDisplay,
            eglConfigAttribute, 0,
            eglConfig, 0, 1,
            numConfig, 0
        )
        if (!eglChooseFlag) {
            throw RuntimeException("eglChooseConfig failed! " + EGL14.eglGetError())
        }

        //创建EGL显示的窗口
        val surfaceAttribute = intArrayOf(
            EGL14.EGL_NONE
        )
        mEglSurface = EGL14.eglCreateWindowSurface(
            mEGLDisplay,
            eglConfig[0],
            mTextureView?.surfaceTexture,
            surfaceAttribute, 0
        )

        //创建上下文
        val ctxAttribute = intArrayOf(
            EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL14.EGL_NONE
        )
        mEGLContext = EGL14.eglCreateContext(
            mEGLDisplay,
            eglConfig[0],
            EGL14.EGL_NO_CONTEXT,
            ctxAttribute, 0
        )

        //校验1
        if (mEGLDisplay === EGL14.EGL_NO_DISPLAY || mEGLContext === EGL14.EGL_NO_CONTEXT) {
            throw RuntimeException("eglCreateContext fail failed! " + EGL14.eglGetError())
        }
        //校验2
        val makeFlag = EGL14.eglMakeCurrent(mEGLDisplay, mEglSurface, mEglSurface, mEGLContext)
        if (!makeFlag) {
            throw RuntimeException("eglMakeCurrent failed! " + EGL14.eglGetError())
        }
    }

    /**
     * 初始化Renderer
     */
    private fun initEGLRenderer() {
       //mRenderer = ITextureRenderer()
    }

    /*
     * setOnFrameAvailableListener   给 loadOESTexture 提供的SurfaceTexture使用
     */
    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        //frame可用之后，开始渲染
    }


    companion object {
        var MSG_INIT = 100
        var MSG_RENDER = 200
        var MSG_DESTROY = 300
    }


}