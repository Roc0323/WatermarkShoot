package com.roc.watermarkshoot

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dueeeke.videocontroller.StandardVideoController
import com.dueeeke.videocontroller.component.*
import com.roc.baselibrary.kt.extend.setVisible
import kotlinx.android.synthetic.main.activity_preview.*

/**
 * Created by roc on 5/7/21 1:37 PM.
 * Desc:
 */
class PreviewActivity : AppCompatActivity(){

    private var previewType = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        previewType = intent.getIntExtra("preview_type",0)
        if (previewType==1){
            iv_photo.setVisible()
            Glide.with(this).load(intent.getStringExtra("image_path")).into(iv_photo)
        }
        if (previewType==2){
            player.setVisible()
        }
        if (intent.hasExtra("video_path")){
            val controller = StandardVideoController(this)
            //根据屏幕方向自动进入/退出全屏
            controller.setEnableOrientation(true)
            val prepareView = PrepareView(this) //准备播放界面
//            val thumb = prepareView.findViewById<ImageView>(R.id.thumb) //封面图
//            val cover_path = intent.getStringExtra("cover_path")
//            Glide.with(this).load(cover_path).into(thumb)
            controller.addControlComponent(prepareView)
            controller.addControlComponent(CompleteView(this)) //自动完成播放界面
            controller.addControlComponent(ErrorView(this)) //错误界面
            val vodControlView = VodControlView(this) //点播控制条
            controller.addControlComponent(vodControlView)
            val gestureControlView = GestureView(this) //滑动控制视图
            controller.addControlComponent(gestureControlView)
            player.setVideoController(controller)
            player.setUrl(intent.getStringExtra("video_path"))
            player.start()
        }
    }

    override fun onResume() {
        super.onResume()
        if (player != null && previewType ==2) {
            player.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (player != null && previewType ==2) {
            player.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (player != null && previewType ==2) {
            player.release()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (player == null || !player.onBackPressed() && previewType ==2) {
            super.onBackPressed()
        }
    }

}