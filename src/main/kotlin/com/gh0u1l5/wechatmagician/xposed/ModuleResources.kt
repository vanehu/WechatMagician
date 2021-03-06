package com.gh0u1l5.wechatmagician.xposed

import android.content.res.XModuleResources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.gh0u1l5.wechatmagician.R
import java.io.InputStream

// ModuleResources describes the localized resources used by the module.
object ModuleResources {
    lateinit var menuSnsForward: String
    lateinit var promptWait: String
    lateinit var menuSnsScreenshot: String
    lateinit var promptScreenShot: String

    lateinit var labelEasterEgg: String
    lateinit var labelDeleted: String
    lateinit var bitmapRecalled: Bitmap

    lateinit var buttonSelectAll: String

    fun init(res: XModuleResources) {
        menuSnsForward = res.getString(R.string.menu_sns_forward)
        promptWait = res.getString(R.string.wait_prompt)
        menuSnsScreenshot = res.getString(R.string.menu_sns_screenshot)
        promptScreenShot = res.getString(R.string.screenshot_prompt)

        labelEasterEgg = res.getString(R.string.easter_egg)
        labelDeleted = res.getString(R.string.label_deleted)

        buttonSelectAll = res.getString(R.string.button_select_all)

        var imgStream: InputStream? = null
        try {
            imgStream = res.assets.open("image_recall_${res.getString(R.string.language)}.jpg")
            bitmapRecalled = BitmapFactory.decodeStream(imgStream)
        } finally {
            imgStream?.close()
        }
    }
}