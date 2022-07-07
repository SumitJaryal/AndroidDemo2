/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wedj.tv.television

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.wedj.tv.R
import com.wedj.tv.data.entities.model.managevideo.ManageVideoResponse
import com.wedj.tv.databinding.ItemExpandCardBinding
import com.wedj.tv.util.getVideoBannerUrl
import com.wedj.tv.util.loadBitmapIntoImageView

/**
 * Presents a [Video] as an [ImageCardView] with descriptive text based on the Video's type.
 */
class MovieCardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val context = parent.context
        val binding = ItemExpandCardBinding.inflate(LayoutInflater.from(context), parent, false)

        // Set the image size ahead of time since loading can take a while.
//        val resources = context.resources
//        binding.root.setMainImageDimensions(
//                resources.getDimensionPixelSize(R.dimen.image_card_width),
//                resources.getDimensionPixelSize(R.dimen.image_card_height))

        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        checkNotNull(item)
        val video = item as ManageVideoResponse
        val binding = ItemExpandCardBinding.bind(viewHolder.view)
        val videoUrlBanner = getVideoBannerUrl(video.vdid)

        if (!video.isSelected) {
            binding.ivActionVolume.visibility = View.VISIBLE
            binding.ivActionAdd.visibility = View.VISIBLE
            binding.ivActionDislike.visibility = View.VISIBLE
            binding.ivActionLike.visibility = View.VISIBLE
            binding.ivActionExpand.visibility = View.VISIBLE
            binding.ivActionPlay.visibility = View.VISIBLE
            binding.tvWatchAgeGroup.visibility = View.VISIBLE
            binding.tvWatchGenre.visibility = View.VISIBLE
            binding.tvWatchMatch.visibility = View.VISIBLE
            binding.tvWatchQuality.visibility = View.VISIBLE
            binding.tvWatchSeason.visibility = View.VISIBLE
        } else {
            binding.ivActionVolume.visibility = View.GONE
            binding.ivActionAdd.visibility = View.GONE
            binding.ivActionDislike.visibility = View.GONE
            binding.ivActionLike.visibility = View.GONE
            binding.ivActionExpand.visibility = View.GONE
            binding.ivActionPlay.visibility = View.GONE
            binding.tvWatchAgeGroup.visibility = View.GONE
            binding.tvWatchGenre.visibility = View.GONE
            binding.tvWatchMatch.visibility = View.GONE
            binding.tvWatchQuality.visibility = View.GONE
            binding.tvWatchSeason.visibility = View.GONE
        }
//        binding.tv = video.name
//        binding.root.contentText = getContentText(binding.root.resources, video)
        loadBitmapIntoImageView(
            binding.root.context,
            videoUrlBanner,
            R.drawable.movie,
            binding.ivCardImage
        )

    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {

    }

    /**
     * Returns a string to display as the "content" for an [ImageCardView].
     *
     * Since Watch Next behavior differs for episodes, movies, and clips, this string makes it
     * more clear which [VideoType] each [Video] is. For example, clips are never included in
     * Watch Next.
     */

}
