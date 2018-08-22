package com.nosetrap.accgesturelib

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Point

class Gesture(private val accService: AccessibilityService) {

    /**
     * click on the screen at the coordinates specified in @param point
     *
     * @return true of gesture has been performed else false
     */
    fun click(point: Point): Boolean {
        val builder = GestureDescription.Builder()
        val p = Path()

        p.moveTo(point.x.toFloat(), point.y.toFloat())
        p.lineTo(point.x.toFloat(), point.y.toFloat())
        builder.addStroke(GestureDescription.StrokeDescription(p, 10, 200))

        val gesture = builder.build()

        return performGesture(gesture)

    }

    /**
     * perform the needed gesture  onto the screen
     */
    private fun performGesture(gesture: GestureDescription): Boolean {
        return accService.dispatchGesture(gesture, object : AccessibilityService.GestureResultCallback() {}, null)
    }

}