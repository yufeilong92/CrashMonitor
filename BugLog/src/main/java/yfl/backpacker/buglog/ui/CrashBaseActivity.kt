package yfl.backpacker.buglog.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import yfl.backpacker.buglog.R
import yfl.backpacker.buglog.utils.MStatusBarUtils

/**
 * @Author : YFL  is Creating a porject in del
 * @Package yfl.backpacker.buglog.ui
 * @Email : yufeilong92@163.com
 * @Time :2020/5/30 14:02
 * @Purpose :基类
 */
open class CrashBaseActivity : AppCompatActivity() {
    var context: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        //设置状态栏
        //设置状态栏
        MStatusBarUtils.setColor(this, resources.getColor(R.color.crash_tool_bar_color), 50)

    }
    fun showProgressLoading() {
        showProgressLoading("加载中...")
    }

    fun showProgressLoading(msg: String?) {
        val progress_view = findViewById<LinearLayout>(R.id.progress_view)
        val tv_progressbar_msg = findViewById<TextView>(R.id.tv_progressbar_msg)
        if (progress_view != null) {
            progress_view.visibility = View.VISIBLE
            tv_progressbar_msg.text = msg
            progress_view.setOnClickListener {
                //空
            }
        }
    }

    fun dismissProgressLoading() {
        val progress_view = findViewById<LinearLayout>(R.id.progress_view)
        val tv_progressbar_msg = findViewById<TextView>(R.id.tv_progressbar_msg)
        if (progress_view != null) {
            progress_view.visibility = View.GONE
            tv_progressbar_msg.text = "加载中..."
        }
    }
}