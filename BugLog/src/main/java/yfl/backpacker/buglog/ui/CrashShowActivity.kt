package yfl.backpacker.buglog.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_crash_show.*
import yfl.backpacker.buglog.MCrashMonitor
import yfl.backpacker.buglog.R

class CrashShowActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash_show)

        try {
            initViews()
        } catch (e: Exception) {
        }
    }

    private fun initViews() {
        btn_restart_app.setOnClickListener {
            //重启app代码

            val intent = baseContext.packageManager
                .getLaunchIntentForPackage(baseContext.packageName)
            intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
        btn_crash_list.setOnClickListener {
            MCrashMonitor.startCrashListPage(this@CrashShowActivity)
        }

    }
}