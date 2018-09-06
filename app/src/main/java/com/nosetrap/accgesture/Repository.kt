package com.nosetrap.accgesture

import android.graphics.Point

class Repository private constructor(){
    var clickPosition = Point(0,0)
    var scrollStart = Point(0,0)
    var scrollStop = Point(0,0)

    var isFromPermissionActivity = false

    companion object {
       private var uniqueInstance: Repository? = null
        fun getInstance(): Repository{
            if(uniqueInstance == null){
                uniqueInstance = Repository()
            }
            return  uniqueInstance!!
        }
    }
}