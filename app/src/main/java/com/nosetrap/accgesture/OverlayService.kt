package com.nosetrap.accgesture

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import android.view.WindowManager
import android.widget.Button
import com.nosetrap.draglib.DraggableOverlayOnTouchListener
import com.nosetrap.draglib.DraggableOverlayService

class OverlayService : DraggableOverlayService() {
    var inflatedControlPanel: View? = null
    var inflatedClick: View? = null
    var inflatedScrollStart: View? = null
    var inflatedScrollStop: View? = null

    var controlPanelParams: WindowManager.LayoutParams? = null
    var clickParams: WindowManager.LayoutParams? = null
    var scrollParamsStart: WindowManager.LayoutParams? = null
    var scrollParamsStop: WindowManager.LayoutParams? = null

    var controlPanelDragTouch: DraggableOverlayOnTouchListener? = null
    var clickDragTouch: DraggableOverlayOnTouchListener? = null
    var scrollStartDragTouch: DraggableOverlayOnTouchListener? = null
    var scrollStopDragTouch: DraggableOverlayOnTouchListener? = null

    val repo = Repository.getInstance()
    var overlayAdded = false

    lateinit var broadcastReceiver: BroadcastReceiver

    override fun code(intent: Intent?) {
        inflatedControlPanel = layoutInflater?.inflate(R.layout.control_panel,null,false)
        inflatedClick = layoutInflater?.inflate(R.layout.click_position,null,false)
        inflatedScrollStart = layoutInflater?.inflate(R.layout.scroll_start,null,false)
        inflatedScrollStop = layoutInflater?.inflate(R.layout.scroll_stop,null,false)

        controlPanelParams = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                else WindowManager.LayoutParams.TYPE_SYSTEM_ERROR, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT)

        clickParams = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                else WindowManager.LayoutParams.TYPE_SYSTEM_ERROR, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT)
         scrollParamsStart = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                         or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                 else WindowManager.LayoutParams.TYPE_SYSTEM_ERROR, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                 WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                 PixelFormat.TRANSLUCENT)
         scrollParamsStop = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                         or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                 else WindowManager.LayoutParams.TYPE_SYSTEM_ERROR, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                 WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                 PixelFormat.TRANSLUCENT)

        scrollParamsStop?.y = 50
        scrollParamsStop?.x = 50
        clickParams?.y = 50
        clickParams?.x = 50

        scrollParamsStart?.y = 50
        scrollParamsStart?.x = 50

        inflatedControlPanel?.layoutParams = controlPanelParams
        inflatedClick?.layoutParams = clickParams
        inflatedScrollStart?.layoutParams = scrollParamsStart
        inflatedScrollStop?.layoutParams = scrollParamsStop

        controlPanelDragTouch = DraggableOverlayOnTouchListener(inflatedControlPanel!!,controlPanelParams!!)
        clickDragTouch = DraggableOverlayOnTouchListener(inflatedClick!!,clickParams!!)
        scrollStartDragTouch = DraggableOverlayOnTouchListener(inflatedScrollStart!!,scrollParamsStart!!)
        scrollStopDragTouch = DraggableOverlayOnTouchListener(inflatedScrollStop!!,scrollParamsStop!!)

        val click = inflatedControlPanel?.findViewById<Button>(R.id.btnClick)
        val scroll = inflatedControlPanel?.findViewById<Button>(R.id.btnScroll)

        click?.setOnClickListener {
            inflatedClick?.visibility = View.GONE
            inflatedScrollStop?.visibility = View.GONE
            inflatedScrollStart?.visibility = View.GONE

            val outArray = IntArray(2)
             inflatedClick?.getLocationOnScreen(outArray)
            repo.clickPosition.x = outArray[0]
            repo.clickPosition.y = outArray[1]
            val serviceIntent = Intent(this,AccService::class.java)
            serviceIntent.putExtra(AccService.EXTRA_ACTION,AccService.ACTION_CLICK)
            startService(serviceIntent)
        }

        scroll?.setOnClickListener {
            inflatedClick?.visibility = View.GONE
            inflatedScrollStop?.visibility = View.GONE
            inflatedScrollStart?.visibility = View.GONE

            val startOutArray = IntArray(2)
            inflatedScrollStart?.getLocationOnScreen(startOutArray)
            repo.scrollStart.x = startOutArray[0]
            repo.scrollStart.y = startOutArray[1]

            val stopOutArray = IntArray(2)
            inflatedScrollStop?.getLocationOnScreen(stopOutArray)
            repo.scrollStop.x = stopOutArray[0]
            repo.scrollStop.y = stopOutArray[1]

            val serviceIntent = Intent(this,AccService::class.java)
            serviceIntent.putExtra(AccService.EXTRA_ACTION,AccService.ACTION_SCROLL)
            startService(serviceIntent)
        }

        try {
            if(!overlayAdded) {
                windowManager?.addView(inflatedControlPanel, controlPanelParams)
                windowManager?.addView(inflatedClick, clickParams)
                windowManager?.addView(inflatedScrollStart, scrollParamsStart)
                windowManager?.addView(inflatedScrollStop, scrollParamsStop)
            }
            overlayAdded = true
        }catch (e:Exception){

        }

        broadcastReceiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                inflatedClick?.visibility = View.VISIBLE
                inflatedScrollStop?.visibility = View.VISIBLE
                inflatedScrollStart?.visibility = View.VISIBLE
            }

        }

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, IntentFilter("GESTURE_COMPLETE"))
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }

    override fun registerDraggableTouchListener() {
        registerOnTouchListener(controlPanelDragTouch!!)
        registerOnTouchListener(clickDragTouch!!)
        registerOnTouchListener(scrollStartDragTouch!!)
        registerOnTouchListener(scrollStopDragTouch!!)

        inflatedControlPanel?.setOnTouchListener(controlPanelDragTouch)
        inflatedClick?.setOnTouchListener(clickDragTouch)
        inflatedScrollStop?.setOnTouchListener(scrollStopDragTouch)
        inflatedScrollStart?.setOnTouchListener(scrollStartDragTouch)
    }
}