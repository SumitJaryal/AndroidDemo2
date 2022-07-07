package com.wedj.tv.television

import android.content.Context
import android.graphics.Color
import android.util.Log
import com.google.gson.annotations.SerializedName
import java.net.URI
import java.net.URISyntaxException

class Card {

    @SerializedName("card")
    private var mImageUrl: String? = null

    @SerializedName("footerColor")
    private var mFooterColor: String? = null

    @SerializedName("selectedColor")
    private var mSelectedColor: String? = null

    @SerializedName("localImageResource")
    private var mLocalImageResource: String? = null

    @SerializedName("footerIconLocalImageResource")
    private var mFooterResource: String? = null

    @SerializedName("type")
    private var mType: Type? = null

    @SerializedName("id")
    private var mId = 0

    @SerializedName("width")
    private var mWidth = 0

    @SerializedName("height")
    private var mHeight = 0


    fun getLocalImageResource(): String? {
        return mLocalImageResource
    }

    fun setLocalImageResource(localImageResource: String?) {
        mLocalImageResource = localImageResource
    }

    fun getFooterResource(): String? {
        return mFooterResource
    }

    fun setFooterResource(footerResource: String?) {
        mFooterResource = footerResource
    }

    fun setType(type: Type?) {
        mType = type
    }

    fun setId(id: Int) {
        mId = id
    }

    fun setWidth(width: Int) {
        mWidth = width
    }

    fun setHeight(height: Int) {
        mHeight = height
    }

    fun getWidth(): Int {
        return mWidth
    }

    fun getHeight(): Int {
        return mHeight
    }

    fun getId(): Int {
        return mId
    }

    fun getType(): Type? {
        return mType
    }



    fun getFooterColor(): Int {
        return if (mFooterColor == null) -1 else Color.parseColor(mFooterColor)
    }

    fun setFooterColor(footerColor: String?) {
        mFooterColor = footerColor
    }

    fun getSelectedColor(): Int {
        return if (mSelectedColor == null) -1 else Color.parseColor(mSelectedColor)
    }

    fun getImageUrl(): String? {
        return mImageUrl
    }

    fun setSelectedColor(selectedColor: String?) {
        mSelectedColor = selectedColor
    }

    fun setImageUrl(imageUrl: String?) {
        mImageUrl = imageUrl
    }

    fun getImageURI(): URI? {
        return if (getImageUrl() == null) null else try {
            URI(getImageUrl())
        } catch (e: URISyntaxException) {
            Log.d("URI exception: ", getImageUrl()!!)
            null
        }
    }

    fun getLocalImageResourceId(context: Context): Int {
        return context.resources.getIdentifier(
            getLocalImageResourceName(), "drawable",
            context.packageName
        )
    }

    fun getLocalImageResourceName(): String? {
        return mLocalImageResource
    }

    fun getFooterLocalImageResourceName(): String? {
        return mFooterResource
    }

    enum class Type {
        MOVIE_COMPLETE, MOVIE, MOVIE_BASE, ICON, SQUARE_BIG, SINGLE_LINE, GAME, SQUARE_SMALL, DEFAULT, SIDE_INFO, SIDE_INFO_TEST_1, TEXT, CHARACTER, GRID_SQUARE, VIDEO_GRID
    }
}