package com.wedj.tv.television

import android.view.ViewGroup
import androidx.leanback.widget.BaseCardView
import androidx.leanback.widget.Presenter
import com.wedj.tv.data.entities.model.managevideo.ManageVideoResponse

abstract class BaseCardPresenter<T : BaseCardView> : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val cardView = onCreateView()
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        val card: ManageVideoResponse = item as ManageVideoResponse
        onBindViewHolder(card, viewHolder!!.view as T)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        onUnbindViewHolder(viewHolder!!.view as T)

    }


    open fun onUnbindViewHolder(cardView: T) {
        // Nothing to clean up. Override if necessary.
    }

    /**
     * Invoked when a new view is created.
     *
     * @return Returns the newly created view.
     */
    protected abstract fun onCreateView(): T

    /**
     * Implement this method to update your card's view with the data bound to it.
     *
     * @param card The model containing the data for the card.
     * @param cardView The view the card is bound to.
     * @see Presenter.onBindViewHolder
     */
    abstract fun onBindViewHolder(card: ManageVideoResponse?, cardView: T)

}