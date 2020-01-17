package com.wangzhumo.app.module.opengl.fbo

import com.alibaba.android.arouter.facade.annotation.Route
import com.wangzhumo.app.base.IRoute
import com.wangzhumo.app.module.opengl.R
import com.wangzhumo.app.origin.BaseActivity
import kotlinx.android.synthetic.main.activity_glfbo_image.*


/**
 * If you have any questions, you can contact by email {wangzhumoo@gmail.com}
 *
 * @author 王诛魔 2020-01-08  17:53
 *
 * 显示一个图片纹理到OpenGL
 */
@Route(path = IRoute.OPENGL.IMAGE_TEXTURE_FBO)
class GLFboImageActivity : BaseActivity() {

    override fun getLayoutId(): Int = R.layout.activity_glfbo_image

    override fun onDestroy() {
        super.onDestroy()
        imageTextureView.release()
    }
}