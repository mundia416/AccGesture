package com.nosetrap.accgesture

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.view.View
import android.view.WindowManager
import android.widget.Button
import com.nosetrap.accgesturelib.util.ScreenUtil
import com.nosetrap.draglib.DragOverlayService
import com.nosetrap.draglib.DragTouchListener

class OverlayService : DragOverlayService() {
    val controlPanelDragListener by lazy { createDragTouchListener(R.layout.control_panel)}
    val clickDragListener by lazy { createDragTouchListener(R.layout.click_position)}
    val scrollStartDragListener by lazy { createDragTouchListener(R.layout.scroll_start)}
    val scrollStopDragListener by lazy { createDragTouchListener(R.layout.scroll_stop)}

    val repo = Repository.getInstance()
    var overlayAdded = false

    lateinit var broadcastReceiver: BroadcastReceiver

    override fun code(intent: Intent?) {

        scrollStopDragListener.layoutParams.y = 50
        scrollStopDragListener.layoutParams.x = 50
        clickDragListener.layoutParams.y = 50
        clickDragListener.layoutParams.x = 50

        scrollStartDragListener.layoutParams.y = 50
        scrollStartDragListener.layoutParams.x = 50

        controlPanelDragListener.findView(R.id.btnClick).setOnClickListener {
            clickDragListener.view.visibility = View.GONE
            scrollStopDragListener.view.visibility = View.GONE
            scrollStartDragListener.view.visibility = View.GONE

            repo.clickPosition = ScreenUtil.getLocationOnScreen(clickDragListener.view)

            val serviceIntent = Intent(this,AccService::class.java)
            serviceIntent.putExtra(AccService.EXTRA_ACTION,AccService.ACTION_CLICK)
            startService(serviceIntent)
        }

        controlPanelDragListener.findView(R.id.btnScroll).setOnClickListener {
            clickDragListener.view.visibility = View.GONE
            scrollStopDragListener.view.visibility = View.GONE
            scrollStartDragListener.view.visibility = View.GONE

            repo.scrollStart = ScreenUtil.getLocationOnScreen(scrollStartDragListener.view)
            repo.scrollStop = ScreenUtil.getLocationOnScreen(scrollStopDragListener.view)

            val serviceIntent = Intent(this,AccService::class.java)
            serviceIntent.putExtra(AccService.EXTRA_ACTION,AccService.ACTION_SCROLL)
            startService(serviceIntent)
        }

        try {
            if(!overlayAdded) {
                addViewToWindow(controlPanelDragListener)
                addViewToWindow(clickDragListener)
                addViewToWindow(scrollStartDragListener)
                addViewToWindow(scrollStopDragListener)
            }
            overlayAdded = true
        }catch (e:Exception){
            e.printStackTrace()
        }

        broadcastReceiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                clickDragListener.view.visibility = View.VISIBLE
                scrollStopDragListener.view.visibility = View.VISIBLE
                scrollStartDragListener.view.visibility = View.VISIBLE
            }

        }

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, IntentFilter("GESTURE_COMPLETE"))
    }

    override fun onDestroy() {
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }
}