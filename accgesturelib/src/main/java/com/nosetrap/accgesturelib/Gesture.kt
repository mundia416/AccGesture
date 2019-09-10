package com.nosetrap.accgesturelib

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Point
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

/**
 * class used to perform gestures
 * make sure that the xml accessibility config file has '  android:canPerformGestures="true"  '
 */
class Gesture(private val accService: AccessibilityService) {

    /**
     * click on the screen at the coordinates specified in @param point
     ** @param startTime The time, in milliseconds, from the time the gesture starts to the
     * time the stroke should start. Must not be negative.
     * @return true of gesture has been performed else false
     */
    fun click(point: Point,strokeStartTime: Long = 10,
              gestureResultCallback: AccessibilityService.GestureResultCallback? = null): Boolean {
        return prepareGesture(point,point,strokeStartTime,200,gestureResultCallback)
    }

    fun hold(point: Point, strokeStartTime: Long = 10, holdDuration: Long = TimeUnit.SECONDS.toMillis(2),
             gestureResultCallback: AccessibilityService.GestureResultCallback? = null): Boolean{
        return prepareGesture(point,point,strokeStartTime,holdDuration,gestureResultCallback)
    }

    fun doubleTap(point: Point,strokeStartTime: Long = 10,
                  gestureResultCallback: AccessibilityService.GestureResultCallback? = null){
        GlobalScope.launch {
            withContext(Dispatchers.IO){
                performDoubleTap(point, strokeStartTime, gestureResultCallback)
            }
        }
    }

    private  suspend fun  performDoubleTap(point: Point,strokeStartTime: Long = 10,
                                           gestureResultCallback: AccessibilityService.GestureResultCallback? = null){
        click(point, strokeStartTime, gestureResultCallback)
        delay(TimeUnit.SECONDS.toMillis(1))
        click(point, strokeStartTime, gestureResultCallback)

    }

    /**
     * perform a scroll gesture from @param from to @param to
     * @param startTime The time, in milliseconds, from the time the gesture starts to the
     * time the stroke should start. Must not be negative.
     * */
    fun scroll(from: Point, to: Point, scrollDuration: Long = 1000,strokeStartTime: Long = 0,
               gestureResultCallback: AccessibilityService.GestureResultCallback? = null): Boolean{
        return prepareGesture(from, to, strokeStartTime, scrollDuration, gestureResultCallback)
    }

    private fun prepareGesture(from: Point, to: Point,strokeStartTime: Long,scrollDuration: Long,
                               gestureResultCallback: AccessibilityService.GestureResultCallback?): Boolean {
        val builder = GestureDescription.Builder()
        val p = Path()

        p.moveTo(from.x.toFloat(), from.y.toFloat())
        p.lineTo(to.x.toFloat(), to.y.toFloat())
        builder.addStroke(GestureDescription.StrokeDescription(p, strokeStartTime, scrollDuration))

        val gesture = builder.build()

        return performGesture(gesture, gestureResultCallback)
    }

    /**
     * perform the needed gesture  onto the screen
     */
    private fun performGesture(gesture: GestureDescription,gestureResultCallback: AccessibilityService.GestureResultCallback?): Boolean {
        return accService.dispatchGesture(gesture, gestureResultCallback, null)
    }

}