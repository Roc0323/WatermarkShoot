package com.roc.watermarkshoot

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.roc.baselibrary.JCameraView
import com.roc.baselibrary.kt.extend.clickWithTrigger
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var videoPath:String? = null
    private var imagePath:String? = null
    private var previewType = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initPermission()
    }

    private fun initPermission() {
        RxPermissions(this@MainActivity)
                .request(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe { aBoolean: Boolean ->
                    if (aBoolean) {
                        bindListener()
                    } else {
                        Toast.makeText(this@MainActivity,"请授予权限！", Toast.LENGTH_LONG).show()
                    }
                }
    }

    private fun bindListener(){
        btn_take_pictures.clickWithTrigger {
            val intent = Intent(this@MainActivity, ShootActivity::class.java)
            intent.putExtra("type", JCameraView.BUTTON_STATE_ONLY_CAPTURE)
            startActivityForResult(intent, 1001)
        }

        btn_record_video.clickWithTrigger {
            val intent = Intent(this@MainActivity, ShootActivity::class.java)
            intent.putExtra("type", JCameraView.BUTTON_STATE_ONLY_RECORDER)
            startActivityForResult(intent, 1002)
        }
        btn_preview.clickWithTrigger {
            val intent = Intent(this@MainActivity, PreviewActivity::class.java)
            intent.putExtra("video_path",videoPath)
            intent.putExtra("preview_type",previewType)
            intent.putExtra("image_path",imagePath)
            startActivityForResult(intent, 1002)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        var type = data!!.getIntExtra("type",0)
        btn_preview.isEnabled = true
        if (type==JCameraView.BUTTON_STATE_ONLY_CAPTURE){
            previewType = 1
            imagePath = data!!.getStringExtra("image_path")
            Toast.makeText(this@MainActivity,"图片保存路径：${imagePath}", Toast.LENGTH_LONG).show()
        }else{
            previewType = 2
            videoPath = data!!.getStringExtra("video_path")
            Toast.makeText(this@MainActivity,"视频保存路径：${videoPath}", Toast.LENGTH_LONG).show()
        }

    }
}