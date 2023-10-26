package com.lamz.reneapps

import com.lamz.reneapps.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "photoUrl + $i",
                "createdAt + $i",
                "name $i",
                "description $i",
                33.000,
                "id $i",
                32.322
            )
            items.add(story)
        }
        return items
    }
}