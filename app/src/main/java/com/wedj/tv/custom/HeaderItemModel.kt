package com.wedj.tv.custom

import androidx.leanback.widget.HeaderItem

class HeaderItemModel(id: Long, name: String, icon: Int) : HeaderItem(id, name) {

    /**
     * Hold an icon resource id
     */
    private var mIconResId = icon

    fun getIconResId(): Int {
        return mIconResId
    }

    fun setIconResId(iconResId: Int) {
        mIconResId = iconResId
    }
}