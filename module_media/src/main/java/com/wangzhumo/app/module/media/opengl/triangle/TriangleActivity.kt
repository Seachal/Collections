package com.wangzhumo.app.module.media.opengl.triangle

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.wangzhumo.app.base.IRoute
import com.wangzhumo.app.module.media.R
import com.wangzhumo.app.origin.BaseActivity
import kotlinx.android.synthetic.main.activity_triangle.*

/**
 * If you have any questions, you can contact by email {wangzhumoo@gmail.com}
 *
 * @author 王诛魔 2019-11-28  17:07
 *
 * 使用opengl绘制一个三角形
 */
@Route(path = IRoute.MEDIA_OPENGL_TRIANGLE)
class TriangleActivity : BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_triangle

    override fun initViews(savedInstanceState: Bundle?) {
        gl_surface_view.setRenderer(TriangleRenderer())
    }
}
