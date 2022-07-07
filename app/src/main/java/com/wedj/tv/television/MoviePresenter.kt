package com.wedj.tv.television

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.wedj.tv.R
import com.wedj.tv.data.entities.model.managevideo.ManageVideoResponse
import com.wedj.tv.util.getVideoBannerUrl
import com.wedj.tv.util.loadBitmapIntoImageView
import com.wedj.tv.util.loadDrawableIntoImageView

const val DEFAULT_CARD_HEIGHT: Int = 400

class MoviePresenter : Presenter() {
    private val TAG = javaClass.simpleName

    private var defaultCardImage: Drawable? = null
    private var selectedBackgroundColor: Int = 0
    private var defaultBackgroundColor: Int = 0
    private val mContext: Context? = null
    var count:Int = 1

    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        parent?.context?.let { context ->
            defaultBackgroundColor = ContextCompat.getColor(context, R.color.app_black_light)
            selectedBackgroundColor = ContextCompat.getColor(context, R.color.default_background)
            defaultCardImage = ContextCompat.getDrawable(context, R.drawable.ic_banner_1)
        }
        val cardView = object : ImageCardView(parent?.context) {
            override fun setSelected(selected: Boolean) {
                updateCardBackgroundColor(this, selected)
                super.setSelected(selected)
            }
        }

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        updateCardBackgroundColor(cardView, false)
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {

        Log.d(TAG, "viewHolder = $viewHolder, item = $item")
        val videoItem = item as ManageVideoResponse

        val checklogout = videoItem.logout



        if (checklogout?.isNotEmpty() == true) {
            val cardView = viewHolder?.view as ImageCardView

            cardView.titleText = videoItem.titles
            cardView.contentText = videoItem.fullName
            // Set card size from dimension resources.
            val res = cardView.resources
            val width = res.getDimensionPixelSize(R.dimen.logout_width)
            val height = res.getDimensionPixelSize(R.dimen.logout_height)

            cardView.setMainImageDimensions(width, height)
          //l  cardView.setBackgroundColor(Color.)
            loadDrawableIntoImageView(
                cardView.context,
                R.drawable.ic_baseline_power_settings,
                cardView.mainImageView
            )
        } else {
            val videoUrlBanner = getVideoBannerUrl(videoItem.vdid)
            val cardView = viewHolder?.view as ImageCardView

            cardView.titleText = count.toString()+ ".  " + videoItem.titles
            cardView.contentText = videoItem.fullName
            // Set card size from dimension resources.
            val res = cardView.resources
            val width = res.getDimensionPixelSize(R.dimen.card_width)
            val height = res.getDimensionPixelSize(R.dimen.card_height)
            cardView.setMainImageDimensions(width, height)
            loadBitmapIntoImageView(
                cardView.context,
                videoUrlBanner,
                R.drawable.ic_banner_1,
                cardView.mainImageView
            )

            count ++
        }


    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        val cardView = viewHolder?.view as ImageCardView
        // Remove references to images so that the garbage collector can free up memory
        cardView.badgeImage = null
        cardView.mainImage = null
    }

    private fun updateCardBackgroundColor(view: ImageCardView, selected: Boolean) {
        val color = if (selected) selectedBackgroundColor else defaultBackgroundColor
        // Both background colors should be set because the view"s background is temporarily visible
        // during animations.
        view.setBackgroundColor(color)
        view.setInfoAreaBackgroundColor(color)
    }

//    private fun updateCardBackgroundColor(view: MovieCardView, selected: Boolean) {
//        val color: Int =
//            if (selected) sSelectedBackgroundColor else sDefaultBackgroundColor
//
//        // Both background colors should be set because the view's background is temporarily visible
//        // during animations.
//        view.setBackgroundColor(color)
////        view.findViewById(R.id.info_field).setBackgroundColor(color)
//    }
}