package com.nosetrap.accgesturelib

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Point

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
    fun click(point: Point,strokeStartTime: Long = 10,gestureResultCallback: AccessibilityService.GestureResultCallback =
            object : AccessibilityService.GestureResultCallback() {}): Boolean {
        val builder = GestureDescription.Builder()
        val p = Path()

        p.moveTo(point.x.toFloat(), point.y.toFloat())
        p.lineTo(point.x.toFloat(), point.y.toFloat())
        builder.addStroke(GestureDescription.StrokeDescription(p, strokeStartTime, 200))

        val gesture = builder.build()

        return performGesture(gesture,gestureResultCallback)

    }

    /**
     * perform a scroll gesture from @param from to @param to
     * @param startTime The time, in milliseconds, from the time the gesture starts to the
     * time the stroke should start. Must not be negative.
     * */
    fun scroll(from: Point, to: Point, scrollDuration: Long = 1000,strokeStartTime: Long = 0,
               gestureResultCallback: AccessibilityService.GestureResultCallback =
            object : AccessibilityService.GestureResultCallback() {}): Boolean{
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
    private fun performGesture(gesture: GestureDescription,gestureResultCallback: AccessibilityService.GestureResultCallback): Boolean {
        return accService.dispatchGesture(gesture, gestureResultCallback, null)
    }

}