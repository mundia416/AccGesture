package com.nosetrap.accgesturelib.util

import android.graphics.Point
import android.view.View

class ScreenUtil {
    companion object{
        fun getLocationOnScreen(view: View): Point{
            val startOutArray = IntArray(2)
            view.getLocationOnScreen(startOutArray)

            val x = startOutArray[0]
            val y = startOutArray[1]
            return  Point(x,y)
        }
    }
}