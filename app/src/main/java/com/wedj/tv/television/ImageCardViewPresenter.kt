package com.wedj.tv.television

import android.content.Context
import android.view.ContextThemeWrapper
import androidx.leanback.widget.ImageCardView
import com.bumptech.glide.Glide
import com.wedj.tv.R

open class ImageCardViewPresenter : AbstractCardPresenter<ImageCardView>() {

  /*  open fun ImageCardViewPresenter(context: Context?, cardThemeResId: Int) {
        super(ContextThemeWrapper(context, cardThemeResId))
    }

    open fun ImageCardViewPresenter(context: Context?) {
        this(context, R.style.DefaultCardTheme)
    }*/

    override fun onCreateView(): ImageCardView {
        //        imageCardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getContext(), "Clicked on ImageCardView", Toast.LENGTH_SHORT).show();
//            }
//        });
        return ImageCardView(getContext())
    }



    override fun onBindViewHolder(card: Card?, cardView: ImageCardView) {
        cardView.tag = card
      /*  cardView.titleText = card.getTitle()
        cardView.contentText = card.getDescription()*/
        if (card!!.getLocalImageResourceName() != null) {
            val resourceId = getContext()!!.resources
                .getIdentifier(
                    card.getLocalImageResourceName(),
                    "drawable", getContext()!!.packageName
                )
            Glide.with(getContext()!!)
                .asBitmap()
                .load(resourceId)
                .into(cardView.mainImageView)
        }
    }


}