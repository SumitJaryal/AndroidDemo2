package com.wedj.tv.television

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.wedj.tv.R
import com.wedj.tv.util.getVideoBannerUrl
import com.wedj.tv.util.loadBitmapIntoImageView

class TextCard : Presenter()  {

    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {



        val cardView = object : TextView(parent?.context) {
        }


        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        val cardView = viewHolder?.view as TextView

        val res = cardView.resources

        cardView.setBackgroundColor(Color.parseColor("#55FFFFFF"))

        cardView.setPadding(20 ,20,20,20)
        cardView.text =  res.getString(R.string.text_card_msg)
        cardView.setTextColor(Color.parseColor("#FFFFFF"))
        cardView.setTypeface(Typeface.DEFAULT_BOLD)

        /*   cardView.setLines(2)*/

    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        val cardView = viewHolder?.view as TextView
        // Remove references to images so that the garbage collector can free up memory
      //  cardView.text = null
        cardView.text = null
    }


}