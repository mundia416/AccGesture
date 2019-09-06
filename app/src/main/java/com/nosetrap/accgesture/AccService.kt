package com.nosetrap.accgesture

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import com.nosetrap.accgesturelib.Gesture

class AccService : AccessibilityService() {
    companion object {
        const val EXTRA_ACTION = "extra_action"
        const val ACTION_CLICK = 1
        const val ACTION_SCROLL = 2
    }

    val repo = Repository.getInstance()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val gesture = Gesture(this)
        val action = intent.getIntExtra(EXTRA_ACTION, -1)

        val gestureResultCallback = object: GestureResultCallback(){
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                LocalBroadcastManager.getInstance(this@AccService).sendBroadcast(Intent("GESTURE_COMPLETE"))
            }
        }
        when(action){
            ACTION_CLICK ->{
              //   repo.clickPosition.y += 2
                val gesturePerformed = gesture.click(repo.clickPosition,10,gestureResultCallback)
                print(gesturePerformed)
            }
            ACTION_SCROLL ->{
                //repo.scrollStart.y += 2
                gesture.scroll(repo.scrollStart,repo.scrollStop,1000,0,gestureResultCallback)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onInterrupt() {
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        // if this method is called from the settings screen opened by the app then the CheckPermissionsActiviy
        //is launched when Accessibility service is connected
        val repo = Repository.getInstance()
        if (repo.isFromPermissionActivity) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            repo.isFromPermissionActivity = false
            startActivity(intent)
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
    }
}