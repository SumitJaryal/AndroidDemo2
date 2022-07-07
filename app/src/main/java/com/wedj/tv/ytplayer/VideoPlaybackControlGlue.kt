/*
 * Copyright (c) 2021 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.wedj.tv.ytplayer

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.PlaybackControlsRow
import com.wedj.tv.util.BaseUtil


class VideoPlaybackControlGlue(context: Context, embeddedPlayerAdapter: EmbeddedPlayerAdapter) :
    PlaybackTransportControlGlue<EmbeddedPlayerAdapter>(context, embeddedPlayerAdapter) {

    private lateinit var skipPreviousAction: PlaybackControlsRow.SkipPreviousAction
    private lateinit var skipNextAction: PlaybackControlsRow.SkipNextAction
    private lateinit var fastForwardAction: PlaybackControlsRow.FastForwardAction
    private lateinit var rewindAction: PlaybackControlsRow.RewindAction
    var check: Boolean? = true
    var check1: Boolean? = true


    override fun onCreatePrimaryActions(primaryActionsAdapter: ArrayObjectAdapter?) {
        super.onCreatePrimaryActions(primaryActionsAdapter)

        skipPreviousAction = PlaybackControlsRow.SkipPreviousAction(context)
        rewindAction = PlaybackControlsRow.RewindAction(context)
        fastForwardAction = PlaybackControlsRow.FastForwardAction(context)
        skipNextAction = PlaybackControlsRow.SkipNextAction(context)

        primaryActionsAdapter?.apply {
            add(skipPreviousAction)
            add(rewindAction)
            add(fastForwardAction)
            add(skipNextAction)
        }
    }

    override fun next() {
        // super.next()

        playerAdapter.next()
    }

    override fun previous() {
        // super.previous()

        playerAdapter.previous()
    }




    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        Log.d("keyCode", "$keyCode")
        BaseUtil.jsonFromModel(event)?.let { Log.d("click_event", it) }

      /*   when (event!!.keyCode) {
           KeyEvent.KEYCODE_DPAD_CENTER -> {}
           KeyEvent.KEYCODE_DPAD_DOWN -> {}
           KeyEvent.KEYCODE_DPAD_UP -> {}
           KeyEvent.KEYCODE_DPAD_RIGHT -> {
               if (check == true) {
                   Log.d("click", "rightClick")
                  playerAdapter.next()
                   
                   check = false

                   Handler(Looper.getMainLooper()).postDelayed({
                       check = true
                   }, 1000)
                   return true
               }
           }
           KeyEvent.KEYCODE_DPAD_LEFT -> {
               if (check1 == true)
                   Log.d("click", "LeftClick")
           //  playerAdapter.previous()

               previous()
               check1 = false

           Handler(Looper.getMainLooper()).postDelayed({
               check1 = true
               }, 1000)

               return true
           }
           KeyEvent.FLAG_KEEP_TOUCH_MODE -> {}
         }*/
       return super.onKey(v, keyCode, event)
      //   return false
    }

    override fun onActionClicked(action: Action?) {
        when (action) {
            rewindAction -> playerAdapter.rewind()
            fastForwardAction -> playerAdapter.fastForward()
            else -> super.onActionClicked(action)
        }
    }

}