package com.wedj.tv.television

import com.wedj.tv.data.entities.model.managevideo.ManageVideoResponse

interface ItemCallback {

    fun onActionPlay(item: ManageVideoResponse)
    fun onActionLike(item: ManageVideoResponse)
    fun onActionDislike(item: ManageVideoResponse)
    fun onActionAddToPlaylist(item: ManageVideoResponse)
    fun onActionVolume(item: ManageVideoResponse)
    fun onActionExpand(item: ManageVideoResponse)

}