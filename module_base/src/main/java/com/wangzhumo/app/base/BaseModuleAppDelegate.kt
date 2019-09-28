package com.wangzhumo.app.base

import android.app.Application
import android.text.TextUtils
import com.alibaba.android.arouter.launcher.ARouter

import com.google.auto.service.AutoService
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.mars.xlog.Log
import com.tencent.mars.xlog.Xlog
import com.wangzhumo.app.base.delegate.AppDelegate
import com.wangzhumo.app.base.delegate.IApp
import com.wangzhumo.app.base.delegate.IAppConstant
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

/**
 * If you have any questions, you can contact by email {wangzhumoo@gmail.com}
 *
 * @author 王诛魔 2019-09-28  18:52
 */
@IApp(name = IAppConstant.BASE)
@AutoService(AppDelegate::class)
class BaseModuleAppDelegate : AppDelegate {
    override fun init(application: Application) {
        AppUtils.init(application)
        initARouter(application)
        initLogger()
        initBugly(application)
        initXLog(application)
    }

    /**
     * 腾讯xlog模块
     * {@see https://github.com/Tencent/mars/wiki/Mars-Android-%E6%8E%A5%E5%85%A5%E6%8C%87%E5%8D%97}
     */
    private fun initXLog(application: Application) {
        val logPath = application.getExternalFilesDir("inyu_xlog")?.absolutePath
        val public_key = "1152b1620b4fe6457a7ddabcf514987b3ba44e3d6c7554fbbf22767bba4b98b3c0b071de670676292a18f53d552da64d5820eb9a7992c97d4ee6915d49f224f1"

        // this is necessary, or may cash for SIGBUS
        val cachePath = "${application.filesDir}/inyu_xlog"
        if (BuildConfig.DEBUG) {
            Xlog.open(
                true,
                Xlog.LEVEL_DEBUG,
                Xlog.AppednerModeAsync,
                cachePath,
                logPath,
                "inyu",
                public_key
            )
            Xlog.setConsoleLogOpen(true)
        } else {
            Xlog.open(
                true,
                Xlog.LEVEL_INFO,
                Xlog.AppednerModeAsync,
                cachePath,
                logPath,
                "inyu",
                public_key
            )
            Xlog.setConsoleLogOpen(false)
        }
        Log.setLogImp(Xlog())
    }


    private fun initLogger() {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(true)  // (Optional) Whether to show thread info or not. Default true
            .methodCount(2)         // (Optional) How many method line to show. Default 2
            .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
            .build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))
    }


    /**
     * 加载ARouter
     */
    private fun initARouter(application: Application) {
        // 这两行必须写在init之前，否则这些配置在init过程中将无效
        if (BuildConfig.DEBUG) {
            // 打印日志
            ARouter.openLog()
            // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
            ARouter.openDebug()
        }
        // 尽可能早，推荐在Application中初始化
        ARouter.init(application)
    }


    /**
     * 加载Bugly
     */
    private fun initBugly(application: Application) {
        // 获取当前包名
        val packageName = application.packageName
        // 获取当前进程名
        val processName = getProcessName(android.os.Process.myPid())
        // 设置是否为上报进程
        val strategy = CrashReport.UserStrategy(application)
        strategy.isUploadProcess = processName == null || processName == packageName
        // 初始化Bugly
        CrashReport.initCrashReport(application, "962f4d00a2", BuildConfig.DEBUG, strategy)
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private fun getProcessName(pid: Int): String? {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
            var processName:String = reader.readLine()
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim { it <= ' ' }
            }
            return processName
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        } finally {
            try {
                reader?.close()
            } catch (exception: IOException) {
                exception.printStackTrace()
            }

        }
        return null
    }
}