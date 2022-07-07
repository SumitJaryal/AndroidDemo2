package com.wedj.tv.custom

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.RowHeaderPresenter
import com.wedj.tv.R

class IconHeaderItem : RowHeaderPresenter() {
    private var mUnselectedAlpha = 0f

    override fun onCreateViewHolder(parent: ViewGroup?): Presenter.ViewHolder {
        mUnselectedAlpha = parent?.resources
            ?.getFraction(R.fraction.lb_browse_header_unselect_alpha, 1, 1) ?: 0f

        val inflater = parent?.context
            ?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.item_header, null)


        view.alpha = mUnselectedAlpha // Initialize icons to be at half-opacity.


        return ViewHolder(view)
    }


    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder?, item: Any?) {
        val headerItem = (item as ListRow).headerItem as HeaderItemModel
        val rootView = viewHolder!!.view
        rootView.isFocusable = true
        val iconView = rootView.findViewById<View>(R.id.iv_header_icon) as ImageView
        if (headerItem.getIconResId() != 0) {
            val icon = rootView.resources.getDrawable(headerItem.getIconResId(), null)
            iconView.setImageDrawable(icon)

        }
//        val icon = rootView.resources.getDrawable(headerItem.getIconResId(), null)
//        if (icon!=null)
//        iconView.setImageDrawable(icon)

        val label = rootView.findViewById<View>(R.id.tv_header_name) as TextView
        label.text = headerItem.name
    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder?) {

    }

    override fun onSelectLevelChanged(holder: ViewHolder?) {
        holder!!.view.alpha = mUnselectedAlpha + holder.selectLevel *
                (1.0f - mUnselectedAlpha)
    }
}