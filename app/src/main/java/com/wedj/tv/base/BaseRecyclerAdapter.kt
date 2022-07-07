package com.wedj.tv.base


import androidx.recyclerview.widget.RecyclerView


abstract class BaseRecyclerAdapter<VM : RecyclerView.ViewHolder> : RecyclerView.Adapter<VM>() {
    val TAG=javaClass.simpleName

}