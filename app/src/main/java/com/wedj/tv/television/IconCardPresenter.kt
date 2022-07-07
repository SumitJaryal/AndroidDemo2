package com.wedj.tv.television

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.leanback.widget.ImageCardView
import com.wedj.tv.R

public  class IconCardPresenter : ImageCardViewPresenter() {
    private val ANIMATION_DURATION = 200





    override fun onCreateView(): ImageCardView {
        val imageCardView = super.onCreateView()
        val image = imageCardView.mainImageView
        image.setBackgroundResource(R.drawable.icon_focused)
        image.background.alpha = 0
        imageCardView.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            animateIconBackground(
                image.background,
                hasFocus
            )
        }
        return imageCardView
    }

    private fun animateIconBackground(drawable: Drawable, hasFocus: Boolean) {
        if (hasFocus) {
            ObjectAnimator.ofInt(drawable, "alpha", 0, 255).setDuration(ANIMATION_DURATION.toLong())
                .start()
        } else {
            ObjectAnimator.ofInt(drawable, "alpha", 255, 0).setDuration(ANIMATION_DURATION.toLong())
                .start()
        }
    }

}