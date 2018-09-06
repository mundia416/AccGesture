package com.nosetrap.accgesture

import android.app.Activity
import android.provider.Settings
import android.text.TextUtils


class PermissionManager(private val activity: Activity) {


    /**
     * is accessiblity service Permission granted
     */
    fun isAccServicePermissionGranted(): Boolean{
        val accServiceId = activity.packageName+"/"+ AccService::class.java.canonicalName

        var enabled = 0
        try{
            enabled = Settings.Secure.getInt(activity.applicationContext.contentResolver,Settings.Secure.ACCESSIBILITY_ENABLED)

        }catch (e: Settings.SettingNotFoundException){

        }

        val colonSplitter: TextUtils.SimpleStringSplitter = TextUtils.SimpleStringSplitter(':')

        if(enabled == 1){
            val settingsValue = Settings.Secure.getString(activity.applicationContext.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)

            if(settingsValue != null){
                colonSplitter.setString(settingsValue)

                while(colonSplitter.hasNext()){
                    val accService = colonSplitter.next()

                    if(accService == accServiceId){
                        return true
                    }
                }
            }
        }

        return false
    }

}