package yfl.backpacker.buglog.ui

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import kotlinx.android.synthetic.main.activity_crash_list.*
import yfl.backpacker.buglog.R
import yfl.backpacker.buglog.listener.MOnItemClickListener
import yfl.backpacker.buglog.ui.adapter.CrashInfoAdapter
import yfl.backpacker.buglog.utils.MFileUtils
import java.io.File
import java.util.*

class CrashListActivity : CrashBaseActivity() {

    private var fileList: List<File>? = null
    private val handler = Handler()
    private var crashInfoAdapter: CrashInfoAdapter? = null
    private var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash_list)
        initListener()
        //设置刷新球颜色
        swipeRefreshLayout.setColorSchemeColors(
            Color.BLACK,
            Color.YELLOW,
            Color.RED,
            Color.GREEN
        )
        try {
            swipeRefreshLayout.post(Runnable {
                swipeRefreshLayout.setRefreshing(true)
                initCrashFileList()
            })
        }catch (e:Exception){

        }
    }

    private fun initListener() {
        swipeRefreshLayout.setOnRefreshListener {
            initCrashFileList()
        }
        btn_delete.setOnClickListener {
            //弹出Dialog是否删除全部

            //弹出Dialog是否删除全部
            val builder = AlertDialog.Builder(this)
            builder.setTitle("提示")
            builder.setMessage("是否删除全部日志?")
            builder.setNegativeButton("取消", null)
            builder.setPositiveButton(
                "删除"
            ) { dialogInterface, i ->
                progressDialog =
                    ProgressDialog.show(this@CrashListActivity, "提示", "正在删除...")
                progressDialog?.show()

                //删除全部
                Thread(Runnable {
                    val fileCrash =
                        File(MFileUtils.getCrashLogPath(context))
                    MFileUtils.deleteAllFiles(fileCrash)

                    //重新获取
                    getCrashList()
                }).start()
            }
            builder.show()
        }
        btn_back.setOnClickListener {
            finish()
        }
    }


    private fun initCrashFileList() {
        //获取日志
        Thread(Runnable { getCrashList() }).start()
    }

    private fun getCrashList() {
        //重新获取
        val fileCrash = File(MFileUtils.getCrashLogPath(context))
        fileList = MFileUtils.getFileList(fileCrash)

        //排序
        Collections.sort(
            fileList,
            object : Comparator<File?> {
                override fun compare(file01: File?, file02: File?): Int {
                    return try {
                        //根据修改时间排序
                        val lastModified01 = file01!!.lastModified()
                        val lastModified02 = file02!!.lastModified()
                        if (lastModified01 > lastModified02) {
                            -1
                        } else {
                            1
                        }
                    } catch (e: java.lang.Exception) {
                        1
                    }
                }
            })

        //通知页面刷新
        handler.post {
            if (progressDialog != null && progressDialog!!.isShowing()) {
                progressDialog?.hide()
            }
            initAdapter()
        }
    }

    private fun initAdapter() {
        if (crashInfoAdapter == null) {
            crashInfoAdapter = CrashInfoAdapter(context, fileList)
            recycleView.setAdapter(crashInfoAdapter)
            crashInfoAdapter?.setOnItemClickLitener(object : MOnItemClickListener {
                override  fun onItemClick(view: View?, position: Int) {
                    val intent = Intent(context, CrashDetailsActivity::class.java)
                    val file = fileList!![position]
                    intent.putExtra(CrashDetailsActivity.IntentKey_FilePath, file.absolutePath)
                    startActivity(intent)
                }

               override fun onLongClick(view: View?, position: Int) {
                    //弹出Dialog是否删除当前
                    val builder =
                        AlertDialog.Builder(context)
                    builder.setTitle("提示")
                    builder.setMessage("是否删除当前日志?")
                    builder.setNegativeButton("取消", null)
                    builder.setPositiveButton(
                        "删除"
                    ) { dialogInterface, i ->
                        progressDialog = ProgressDialog.show(
                            this@CrashListActivity,
                            "提示",
                            "正在删除..."
                        )
                        progressDialog!!.show()
                        //删除单个
                        Thread(Runnable {
                            val file = fileList!![position]
                            MFileUtils.deleteFile(file.path)
                            //重新获取
                            getCrashList()
                        }).start()
                    }
                    builder.show()
                }
            })
        } else {
            crashInfoAdapter?.updateDatas(fileList)
        }
        swipeRefreshLayout.isRefreshing = false
    }
}