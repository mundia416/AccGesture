package com.nosetrap.accgesture

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(!Settings.canDrawOverlays(this)){
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
        }

        val permissionManager = PermissionManager(this)
        if(!permissionManager.isAccServicePermissionGranted()){
            Repository.getInstance().isFromPermissionActivity = true
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        val intent = Intent(this,OverlayService::class.java)
        startService(intent)

    }

    override  fun onResume() {
        super.onResume()

    }
}
