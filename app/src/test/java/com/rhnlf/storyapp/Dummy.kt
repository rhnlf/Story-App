package com.rhnlf.storyapp

import com.rhnlf.storyapp.data.remote.response.ListStoryItem

object Dummy {
    fun generateDummyQuoteResponse(): List<ListStoryItem> {
        val item: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "https://assets.manutd.com/AssetPicker/images/0/0/16/247/1111858/Player_Profile_Thumbnail_Mens_2223_Kit_DDG1658217243574_thumb.jpg",
                "created at $i",
                "user $i",
                "description $i",
                i.toDouble(),
                "$i",
                i.toDouble()
            )
            item.add(story)
        }
        return item
    }
}