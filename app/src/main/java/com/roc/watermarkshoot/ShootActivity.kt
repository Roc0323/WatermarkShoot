package com.roc.watermarkshoot

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.roc.baselibrary.JCameraView
import com.roc.baselibrary.listener.ClickListener
import com.roc.baselibrary.listener.ErrorListener
import com.roc.baselibrary.listener.JCameraListener
import com.roc.baselibrary.util.DisplayUtils
import com.roc.baselibrary.util.FileUtil
import com.roc.baselibrary.util.FileUtils
import com.roc.baselibrary.watermark.StampPadding
import com.roc.baselibrary.watermark.StampType
import com.roc.baselibrary.watermark.StampWatcher
import com.roc.baselibrary.watermark.Stamper
import io.microshow.rxffmpeg.RxFFmpegInvoke
import io.microshow.rxffmpeg.RxFFmpegSubscriber
import kotlinx.android.synthetic.main.activity_shoot.*
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by roc on 5/6/21 5:20 PM.
 * Desc: 拍摄页面
 */
class ShootActivity : AppCompatActivity(){

    private var featuresType :Int = 0
    private var filePath = ""
    private var addressInfo = ""
    private var nameAndTimeInfo = ""

    private var myRxFFmpegSubscriber: MyRxFFmpegSubscriber? = null
    private var strCommand = ""
    private var watermarkVideoPath = ""
    private var originalVideoPath = ""
    private var progressDialog: AlertDialog? = null
    var loadText: TextView? = null

    private lateinit var jCameraView: JCameraView

    companion object {
        var instance: ShootActivity? = null
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //状态栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window =window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            //黑色字体
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT

        }
        setContentView(R.layout.activity_shoot)
        initView()
    }

    private fun initView(){
        instance = this
        val path = FileUtils.getFilePath(this) + "/Peace"
        featuresType = intent.getIntExtra("type",JCameraView.BUTTON_STATE_BOTH)
        jCameraView = findViewById<JCameraView>(R.id.camera_view)
        //设置图片/视频保存路径
        jCameraView.setSaveVideoPath(path)
        jCameraView.setFeatures(featuresType)
        jCameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_MIDDLE)
        myRxFFmpegSubscriber = MyRxFFmpegSubscriber()
        if (featuresType==JCameraView.BUTTON_STATE_ONLY_CAPTURE){
            jCameraView.setTip("单击拍照")
        }else if (featuresType==JCameraView.BUTTON_STATE_ONLY_RECORDER){
            jCameraView.setTip("长按录制")
        }else{
            jCameraView.setTip("单击拍照,长按录制")
        }

        jCameraView.setErrorLisenter(object : ErrorListener {
            override fun onError() {
                //错误监听
                Log.i("CJT", "camera error")
                finish()
            }

            override fun AudioPermissionError() {
                Toast.makeText(this@ShootActivity,"录音未授权！",Toast.LENGTH_LONG).show()
            }
        })

        //JCameraView监听
        jCameraView.setJCameraLisenter(object : JCameraListener {
            override fun captureSuccess(bitmap: Bitmap?) {
//                val watermark = BitmapFactory.decodeResource(resources, R.drawable.icon_watermark_positioning)
                val watermark = createWatermarkBitmap()
                //获取图片bitmap
                Stamper.with(this@ShootActivity)
                        .setLabel(addressInfo)
                        .setLabel2(nameAndTimeInfo)
                        .setLabelColor(resources.getColor(R.color.white)) //Color.rgb(255, 60, 70)
                        .setLabelSize(DisplayUtils.sp2px(this@ShootActivity,10f))
                        .setMasterBitmap(bitmap)
                        .setWatermark(watermark)
                        .setStampType(StampType.IMAGE)
                        .setStampPadding(StampPadding(DisplayUtils.dip2px(this@ShootActivity,15f).toFloat(), (bitmap!!.getHeight() - DisplayUtils.dip2px(this@ShootActivity,15f)).toFloat()))
                        .setStampWatcher(object : StampWatcher() {
                            override fun onSuccess(newBitmap: Bitmap?, requestId: Int) {
                                //super.onSuccess(bitmap, requestId)
                                if(requestId==1001){
                                    if (newBitmap!=null){
                                        filePath = FileUtil.saveBitmap(this@ShootActivity, newBitmap)
                                        Log.i("CJT", "加水印后的本地路径url = $filePath")
                                    }else{
                                        filePath = FileUtil.saveBitmap(this@ShootActivity, bitmap)
                                        Log.i("CJT", "不加水印的本地路径url = $filePath")
                                    }
                                    val intent = Intent()
                                    intent.putExtra("image_path", filePath)
                                    intent.putExtra("type", featuresType)
                                    setResult(Activity.RESULT_OK, intent)
                                    finish()
                                }
                            }

                            override fun onError(error: String?, requestId: Int) {
                                //super.onError(error, requestId)
                                finish()
                            }
                        })
                        .setRequestId(1001)
                        .build()
            }

            override fun recordSuccess(url: String, firstFrame: Bitmap?) {
                originalVideoPath = url
                var watermarkImagePath = createWatermarkImage()
//                var watermarkBitmap = createWatermarkBitmap()
                val path = FileUtils.getFilePath(this@ShootActivity) + "/Peace"
                val dataTake = System.currentTimeMillis()
                watermarkVideoPath = path + File.separator + "watermark_" + dataTake + ".mp4"
                var videoHeight = 1280
                Log.i("TAG","Bitmap宽："+firstFrame!!.width)
                Log.i("TAG","Bitmap高："+firstFrame!!.height)
                if (firstFrame!!.width>firstFrame!!.height){
                    //横屏
                    videoHeight = 720
                }else{
                    //竖屏
                    videoHeight = 1280
                }
                var height = videoHeight- DisplayUtils.dip2px(this@ShootActivity,50f)
                strCommand = "ffmpeg -y -i ${url} -i ${watermarkImagePath} -filter_complex [0:v]scale=iw:ih[outv0];[1:0]scale=0.0:0.0[outv1];[outv0][outv1]overlay=20:${height} -preset superfast ${watermarkVideoPath}"
                val commands: Array<String> = strCommand.split(" ".toRegex()).toTypedArray()
                loadText = showProgressDialog()
                loadText!!.text = "视频处理中 0%"
                //开始执行FFmpeg命令
                RxFFmpegInvoke.getInstance()
                        .runCommandRxJava(commands)
                        .subscribe(myRxFFmpegSubscriber)
            }
        })

        jCameraView.setLeftClickListener(object : ClickListener {
            override fun onClick() {
                finish()
            }
        })

        var params: FrameLayout.LayoutParams = ll_watermark_info.getLayoutParams() as FrameLayout.LayoutParams
        params.width = DisplayUtils.dip2px(this,250f)
        params.height = FrameLayout.LayoutParams.WRAP_CONTENT
        ll_watermark_info.layoutParams = params



    }

    override fun onResume() {
        super.onResume()
        if (jCameraView!=null){
            jCameraView.onResume()
        }

    }

    override fun onPause() {
        super.onPause()
        if (jCameraView!=null){
            jCameraView.onPause()
        }

    }

    /**
     * 将视频路径回调到上个页面
     */
    fun callback(isWatermark : Boolean){
        val intent = Intent()
        intent.putExtra("image_path", "")
        if(isWatermark){
            intent.putExtra("video_path", watermarkVideoPath)
        }else{
            intent.putExtra("video_path", originalVideoPath)
        }
        intent.putExtra("type", featuresType)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }


    /**
     * 创建水印图片
     */
    private fun createWatermarkImage(): String{
        val path = FileUtils.getFilePath(this@ShootActivity) + "/Peace"
        val dataTake = System.currentTimeMillis()
        val imagePath = path + File.separator + "watermark_" + dataTake + ".png"
        //得到水印view的bitmap图片
        var bitmap = Bitmap.createBitmap(ll_watermark_info.getWidth(), ll_watermark_info.getHeight(), Bitmap.Config.ARGB_8888)
        ll_watermark_info.draw(Canvas(bitmap))
        //这步是根据视频尺寸来调整图片宽高,和视频保持一致
        val matrix = Matrix()
        matrix.postScale(0.85f, 0.85f)
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        val fos = FileOutputStream(File(imagePath))
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        if (imagePath!=null){
            return imagePath
        }else{
            return ""
        }
    }
    /**
     * 创建水印Bitmap
     */
    private fun createWatermarkBitmap(): Bitmap{
        val path = FileUtils.getFilePath(this@ShootActivity) + "/Peace"
        val dataTake = System.currentTimeMillis()
        val imagePath = path + File.separator + "watermark_" + dataTake + ".png"
        //得到水印view的bitmap图片
        var bitmap = Bitmap.createBitmap(ll_watermark_info.getWidth(), ll_watermark_info.getHeight(), Bitmap.Config.ARGB_8888)
        ll_watermark_info.draw(Canvas(bitmap))
        val fos = FileOutputStream(File(imagePath))
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        return bitmap
    }


    class MyRxFFmpegSubscriber() : RxFFmpegSubscriber() {
        private val mWeakReference: WeakReference<String>
        override fun onFinish() {
            Log.i("CJT", "水印视频完成")
            ShootActivity.instance!!.closeProgressDialog()
            ShootActivity.instance!!.callback(true)
        }
        override fun onProgress(progress: Int, progressTime: Long) {
            Log.i("CJT", "水印视频进度："+progress)
            if (ShootActivity.instance!!.loadText!=null){
                if (progress>0){
                    ShootActivity.instance!!.loadText!!.text = "视频处理中 ${progress}%"
                }
            }
        }
        override fun onCancel() {

        }
        override fun onError(message: String) {
            Log.i("CJT", "水印视频错误："+message)
            ShootActivity.instance!!.closeProgressDialog()
            ShootActivity.instance!!.callback(false)
        }
        init {
            mWeakReference = WeakReference<String>("")
        }
    }

    fun showProgressDialog(): TextView? {
        var builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        var view = View.inflate(this@ShootActivity, R.layout.dialog_video_loading, null)
        builder.setView(view)
        var tv_hint = view.findViewById<TextView>(R.id.tv_hint_text)
        progressDialog = builder.create()
        progressDialog!!.show()
        return tv_hint
    }

    fun closeProgressDialog() {
        try {
            if (progressDialog != null) {
                progressDialog!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if (myRxFFmpegSubscriber!=null){
            myRxFFmpegSubscriber!!.dispose()
        }
    }


}