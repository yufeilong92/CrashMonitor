package yfl.backpacker.crashmonitor

import android.app.Application
import android.util.Log
import yfl.backpacker.buglog.MCrashMonitor
import yfl.backpacker.buglog.listener.MCrashCallBack
import java.io.File

/**
 * @Author : YFL  is Creating a porject in CrashMonitor
 * @Package yfl.backpacker.crashmonitor
 * @Email : yufeilong92@163.com
 * @Time :2020/5/30 14:46
 * @Purpose :
 */
class BaseApplication:Application() {
    companion object{
        val TAG = "MyApplication"
    }
    override fun onCreate() {
        super.onCreate()
        initCrashMonitor()
    }

    private fun initCrashMonitor() {
        /**
         * 初始化日志系统
         * context :    上下文
         * isDebug :    是不是Debug模式,true:崩溃后显示自定义崩溃页面 ;false:关闭应用,不跳转奔溃页面(默认)
         * CrashCallBack : 回调执行
         */
        MCrashMonitor.init(this, true, object : MCrashCallBack {
            override fun onCrash(file: File) {
                //可以在这里保存标识，下次再次进入把日志发送给服务器
                Log.i(
                    TAG,
                    "CrashMonitor回调:" + file.absolutePath
                )
            }
        })
    }
}