package com.wedj.tv.playing

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.wedj.tv.MainActivity.Companion.context
import com.wedj.tv.R
import okhttp3.internal.notify

 class NextPlayingListingAdapter( var nextPlayingList: ArrayList<String>) :
    RecyclerView.Adapter<NextPlayingListingAdapter.MyViewHolder>() {
      class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemTextView: TextView = view.findViewById(R.id.itemTextView)
    }
    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.listing_items, parent, false)
        return MyViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val postion_value = position



        val item = nextPlayingList[position]
        holder.itemTextView.text = item

        if (position == 0){
            holder.itemTextView.setTextColor(
                ContextCompat.getColor(
                context!!,
             R.color.search_opaque))
        }

    }
    override fun getItemCount(): Int {
        return nextPlayingList.size
    }

    fun ItemList(name_List: List<String>) {

      // throw RuntimeException("Test Crash") // Force a crash // just test


        nextPlayingList.clear()
        nextPlayingList.addAll(name_List)
        notifyDataSetChanged()


    }
}