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
     *
     * @return true of gesture has been performed else false
     */
    fun click(point: Point,gestureResultCallback: AccessibilityService.GestureResultCallback =
            object : AccessibilityService.GestureResultCallback() {}): Boolean {
        val builder = GestureDescription.Builder()
        val p = Path()

        p.moveTo(point.x.toFloat(), point.y.toFloat())
        p.lineTo(point.x.toFloat(), point.y.toFloat())
        builder.addStroke(GestureDescription.StrokeDescription(p, 10, 200))

        val gesture = builder.build()

        return performGesture(gesture,gestureResultCallback)

    }

    /**
     * perform a scroll gesture from @param from to @param to
     */
    fun scroll(from: Point, to: Point, scrollDuration: Long = 1000,gestureResultCallback: AccessibilityService.GestureResultCallback =
            object : AccessibilityService.GestureResultCallback() {}): Boolean{
        val builder = GestureDescription.Builder()
        val p = Path()

        p.moveTo(from.x.toFloat(), from.y.toFloat())
        p.lineTo(to.x.toFloat(), to.y.toFloat())
        builder.addStroke(GestureDescription.StrokeDescription(p, 10, scrollDuration))

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