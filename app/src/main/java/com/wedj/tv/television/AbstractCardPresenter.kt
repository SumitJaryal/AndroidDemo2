package com.wedj.tv.television

import android.content.Context
import android.view.ViewGroup
import androidx.leanback.widget.BaseCardView
import androidx.leanback.widget.Presenter

abstract class AbstractCardPresenter<T: BaseCardView> : Presenter() {

    private val TAG = "AbstractCardPresenter"
    private var mContext: Context? = null

    /**
     * @param context The current context.
     */
    open fun AbstractCardPresenter(context: Context?) {
        mContext = context
    }

    open fun getContext(): Context? {
        return mContext
    }

    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val cardView = onCreateView()
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val card: Card? = item as Card?
        onBindViewHolder(card, viewHolder.view as T)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        onUnbindViewHolder(viewHolder.view as T)
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
    abstract fun onBindViewHolder(card: Card?, cardView: T)

}