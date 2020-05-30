package yfl.backpacker.buglog.ui

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.TextUtils
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_crash_details.*
import yfl.backpacker.buglog.R
import yfl.backpacker.buglog.utils.*
import java.io.File

/**
 * @Author : YFL  is Creating a porject in del
 * @Package yfl.backpacker.buglog.ui
 * @Email : yufeilong92@163.com
 * @Time :2020/5/30 14:03
 * @Purpose :崩溃详情页面展示
 */
class CrashDetailsActivity : CrashBaseActivity() {


    companion object{
        /**
         * Intent 传递的文件路径
         */
        val IntentKey_FilePath = "IntentKey_FilePath"

    }

    /**
     * 文件路径
     */
    private var filePath: String? = null

    /**
     * 崩溃日志的内容
     */
    private var crashContent: String? = null

    /**
     * 具体的异常类型
     */
    private var matchErrorInfo: String? = null

    /**
     * 所有Activity的集合
     */
    private var activitiesClass: List<Class<*>>? = null

    private val handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash_details)
        initTryCatrah()
    }
    private fun initTryCatrah(){
        try {
            initIntent()
            initDatas()
            initListener()
        } catch (e: Exception) {
        }
    }

    private fun initListener() {
        btn_back.setOnClickListener {
            finish()
        }
        btn_share.setOnClickListener {
            //先把文件转移到外部存储文件
            //请求权限
            //检查版本是否大于M

            //先把文件转移到外部存储文件
            //请求权限
            //检查版本是否大于M
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        10086
                    )
                } else {
                    shareLogs()
                }
            } else {
                //6.0之下判断有没有权限
                if (MPermission5Utils.hasWritePermission()) {
                    shareLogs()
                } else {
                    Toast.makeText(context, "缺少存储权限", Toast.LENGTH_SHORT).show()
                }
            }
        }
        btn_copy.setOnClickListener {
            //复制
            putTextIntoClip()
            Toast.makeText(context, "复制内容成功", Toast.LENGTH_SHORT).show()
        }
        btn_screenshot.setOnClickListener {
            //直接保存
            saveScreenShot()
        }
    }

    private fun initIntent() {
        filePath = intent.getStringExtra(IntentKey_FilePath)
    }

    private fun initDatas() {
         showProgressLoading("加载中....")
        Thread(Runnable {
            dismissProgressLoading()
            //获取文件夹名字匹配异常信息高亮显示
            val file = File(filePath)
            val splitNames =
                file.name.replace(".txt", "").split("_".toRegex()).toTypedArray()
            if (splitNames.size == 3) {
                val errorMsg = splitNames[2]
                if (!TextUtils.isEmpty(errorMsg)) {
                    matchErrorInfo = errorMsg
                }
            }
            //获取内容
            crashContent = MFileUtils.readFile2String(filePath)
            if (handler == null) {
                return@Runnable
            }
            //获取所有Activity
            activitiesClass =
                MActivityListUtil.getActivitiesClass(context, packageName, null)

            //富文本显示
            var spannable =
                Spannable.Factory.getInstance().newSpannable(crashContent)

            //匹配错误信息
            if (!TextUtils.isEmpty(matchErrorInfo)) {
                spannable = MSpannableUtils.addNewSpanable(
                    context,
                    spannable,
                    crashContent,
                    matchErrorInfo,
                    Color.parseColor("#FF0006"),
                    18
                )
            }

            //匹配包名
            val packageName = packageName
            spannable = MSpannableUtils.addNewSpanable(
                context,
                spannable,
                crashContent,
                packageName,
                Color.parseColor("#0070BB"),
                0
            )

            //匹配Activity
            if (activitiesClass != null && activitiesClass!!.size > 0) {
                for (i in activitiesClass!!.indices) {
                    spannable = MSpannableUtils.addNewSpanable(
                        context,
                        spannable,
                        crashContent,
                        activitiesClass!!.get(i).simpleName,
                        Color.parseColor("#55BB63"),
                        16
                    )
                }
            }

            //主线程处理
            val finalSpannable = spannable
            handler.post {
                if (textView != null) {
                    try {
                        textView.setText(finalSpannable)
                    } catch (e: java.lang.Exception) {
                        textView.setText(crashContent)
                    }
                }
            }
        }).start()
    }

    /**
     * 保存截图
     */
    private fun saveScreenShot() {
        showProgressLoading("正在保存截图...")
        //生成截图
        val bitmap: Bitmap = MScreenShotUtil.getBitmapByView(scrollViewCrashDetails)
        Thread(Runnable {
            if (bitmap != null) {
                val crashPicPath = MFileUtils.getCrashPicPath(context)
                    .toString() + "/crash_pic_" + System.currentTimeMillis() + ".jpg"
                val saveBitmap: Boolean =
                    MBitmapUtil.saveBitmap(context, bitmap, crashPicPath)
                if (saveBitmap) {
                    showToast("保存截图成功，请到相册查看\n路径：$crashPicPath")
                    val bitmapCompress: Bitmap =
                        MBitmapUtil.getBitmap(File(crashPicPath), 200, 200)
                    handler.post {
                        dismissProgressLoading()
                        //设置图片
                        iv_screen_shot.setImageBitmap(bitmapCompress)
                        //显示
                        iv_screen_shot.visibility = View.VISIBLE
                        //设置宽高
                        val layoutParams =
                            iv_screen_shot.layoutParams
                        layoutParams.width = MSizeUtils.getScreenWidth(context)
                        layoutParams.height =
                            bitmapCompress.height * layoutParams.width / bitmapCompress.width
                        iv_screen_shot.layoutParams = layoutParams
                        //设置显示动画
                        iv_screen_shot.pivotX = 0f
                        iv_screen_shot.pivotY = 0f
                        val animatorSetScale = AnimatorSet()
                        val scaleX =
                            ObjectAnimator.ofFloat(iv_screen_shot, "scaleX", 1f, 0.2f)
                        val scaleY =
                            ObjectAnimator.ofFloat(iv_screen_shot, "scaleY", 1f, 0.2f)
                        animatorSetScale.duration = 1000
                        animatorSetScale.interpolator = DecelerateInterpolator()
                        animatorSetScale.play(scaleX).with(scaleY)
                        animatorSetScale.start()

                        //三秒后消失
                        handler.postDelayed({ iv_screen_shot.visibility = View.GONE }, 3000)
                    }
                } else {
                    showToast("保存截图失败")
                    dismissProgressLoading()
                }
            } else {
                showToast("保存截图失败")
                dismissProgressLoading()
            }
        }).start()
    }
    private fun showToast(msg: String) {
        handler.post { Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == 10086) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                shareLogs()
            } else {
                Toast.makeText(context, "权限已拒绝", Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    private fun shareLogs() {
        //先把文件转移到外部存储文件
        val srcFile = File(filePath)
        val destFilePath =
            MFileUtils.getCrashSharePath().toString() + "/CrashShare.txt"
        val destFile = File(destFilePath)
        val copy = MFileUtils.copyFile(srcFile, destFile)
        if (copy) {
            //分享
            MShareUtil.shareFile(context, destFile)
        } else {
            Toast.makeText(context, "文件保存失败", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    /**
     * 添加到剪切板
     */
    fun putTextIntoClip() {
        val clipboardManager =
            context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        //创建ClipData对象
        val clipData = ClipData.newPlainText("CrashLog", crashContent)
        //添加ClipData对象到剪切板中
        clipboardManager.setPrimaryClip(clipData)
    }
}