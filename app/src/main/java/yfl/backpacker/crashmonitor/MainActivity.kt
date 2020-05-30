package yfl.backpacker.crashmonitor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import yfl.backpacker.buglog.MCrashMonitor

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_throw_exception.setOnClickListener {
            throwException()
        }
        btn_log_list.setOnClickListener {
            //打开日志列表
            MCrashMonitor.startCrashListPage(this)
        }
        btn_next_page.setOnClickListener {
//            startActivity(Intent(mContext, MainActivity3::class.java))
        }
        btn_add_extra_info.setOnClickListener {
            val extraInfo = """
                用户手机号码：16666666666
                用户网络环境：xxx
                """.trimIndent()
            MCrashMonitor.setCrashLogExtraInfo(extraInfo)
        }
        btn_get_log_path.setOnClickListener {
            val crashLogFilesPath = MCrashMonitor.getCrashLogFilesPath(this)
            Toast.makeText(this, "崩溃日志文件夹的路径：$crashLogFilesPath", Toast.LENGTH_SHORT).show()
            textView!!.text = "崩溃日志文件夹的路径：\n$crashLogFilesPath"
        }
    }
    private fun throwException() {
        //手动造成一个Crash
        throw NullPointerException("自定义异常抛出")
    }
}