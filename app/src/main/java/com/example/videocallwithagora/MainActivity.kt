package com.example.videocallwithagora

import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.ContentValues.TAG
import android.graphics.Color
import android.graphics.drawable.Icon
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import com.example.videocallwithagora.databinding.ActivityMainBinding
import io.agora.agorauikit_android.AgoraButton
import io.agora.agorauikit_android.AgoraConnectionData
import io.agora.agorauikit_android.AgoraSettings
import io.agora.agorauikit_android.AgoraVideoViewer
import io.agora.agorauikit_android.requestPermission
import io.agora.rtc2.Constants.CLIENT_ROLE_BROADCASTER
import io.agora.rtc2.RtcEngine

private const val PERMISSION_REQ_ID = 22
private val REQUESTED_PERMISSIONS = arrayOf<String>(
    RECORD_AUDIO,
    CAMERA,
    WRITE_EXTERNAL_STORAGE
)

@OptIn(ExperimentalUnsignedTypes::class)
class MainActivity : AppCompatActivity() {
    var agView: AgoraVideoViewer? = null
    private lateinit var rtcEngine: RtcEngine
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        try {
            agView = AgoraVideoViewer(
                this,
                AgoraConnectionData("4a4d327b3b73447483c3256c322e125f"),
                agoraSettings = this.settingsWithExtraButtons()
            )
        } catch (e: Exception) {
            println("VideoUIKit App" + " Could not initialise AgoraVideoViewer. Check your App ID is valid. ${e.message}")
            return
        }
        this.addContentView(
            agView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        if (AgoraVideoViewer.requestPermission(this)) {
            agView!!.join("test", role = CLIENT_ROLE_BROADCASTER)
        } else {
            val joinButton = Button(this)
            joinButton.text = "Allow Camera and Microphone, then click here"
            joinButton.setOnClickListener {
                // When the button is clicked, check permissions again and join channel
                // if permissions are granted.
                if (AgoraVideoViewer.requestPermission(this)) {
                    (joinButton.parent as ViewGroup).removeView(joinButton)
                    agView!!.join("test", role = CLIENT_ROLE_BROADCASTER)
                }
            }
            joinButton.setBackgroundColor(Color.GREEN)
            joinButton.setTextColor(Color.RED)
            this.addContentView(
                joinButton,
                FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 300)
            )
        }
    }

    fun settingsWithExtraButtons(): AgoraSettings {
        val agoraSettings = AgoraSettings()

        val agBeautyButton = AgoraButton(this)
        agBeautyButton.clickAction = {
            it.isSelected = !it.isSelected
            agBeautyButton.setImageResource(
                if (it.isSelected) android.R.drawable.star_on else android.R.drawable.star_off
            )
            it.background.setTint(if (it.isSelected) Color.GREEN else Color.GRAY)
            this.agView?.agkit?.setBeautyEffectOptions(it.isSelected, this.agView?.beautyOptions)
        }
        agBeautyButton.setImageResource(android.R.drawable.star_off)

        return agoraSettings
    }
}