package com.wedj.tv.ytplayer

interface UpdateYIdCallBack {
    fun onActionUpdateYIdCallBack(id:String)
    fun onActionSaveCurrentPlayingYtId(YtId:String)
    fun onActionFadeIn()
    fun onActionFadeOut()
    fun onActionBackToHome()
    fun onActionCheckVideoStatus(YtId:String)
    fun onActionCurrentApi(YtId:String)
    fun onActionSaveCurrentVID(vId:String)




}