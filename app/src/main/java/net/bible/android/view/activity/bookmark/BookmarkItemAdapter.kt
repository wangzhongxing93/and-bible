/*
 * Copyright (c) 2020 Martin Denham, Tuomas Airaksinen and the And Bible contributors.
 *
 * This file is part of And Bible (http://github.com/AndBible/and-bible).
 *
 * And Bible is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * And Bible is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with And Bible.
 * If not, see http://www.gnu.org/licenses/.
 *
 */
package net.bible.android.view.activity.bookmark

import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.bookmark_list_item.view.*
import net.bible.android.activity.R
import net.bible.android.control.bookmark.BookmarkControl
import net.bible.android.control.page.window.ActiveWindowPageManagerProvider
import net.bible.android.control.versification.toV11n
import net.bible.android.view.util.widget.BookmarkListItem
import net.bible.android.database.bookmarks.BookmarkEntities.Bookmark
import net.bible.service.sword.SwordContentFacade

/**
 * nice example here: http://shri.blog.kraya.co.uk/2010/04/19/android-multi-line-select-list/
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 */
class BookmarkItemAdapter(
    context: Context,
    items: List<Bookmark>,
    private val bookmarkControl: BookmarkControl,
    private val swordContentFacade: SwordContentFacade,
    private val activeWindowPageManagerProvider: ActiveWindowPageManagerProvider
) : ArrayAdapter<Bookmark>(context, R.layout.bookmark_list_item, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)!!

        // Pick up the TwoLineListItem defined in the xml file
        val view: BookmarkListItem
        view = if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.bookmark_list_item, parent, false) as BookmarkListItem
        } else {
            convertView as BookmarkListItem
        }
        val labels = bookmarkControl.labelsForBookmark(item)
        val isSpeak = labels.contains(bookmarkControl.speakLabel)
        if (isSpeak) {
            view.speakIcon.visibility = View.VISIBLE
        } else {
            view.speakIcon.visibility = View.GONE
        }
        labels.firstOrNull()?.bookmarkStyle?.backgroundColor?.also {
            view.setBackgroundColor(it)
        }

        // Set value for the first text field
        val versification = activeWindowPageManagerProvider.activeWindowPageManager.currentBible.versification
        val verseName = item.verseRange.toV11n(versification).name
        val book = item.speakBook
        if (isSpeak && book != null) {
            view.verseText.text = context.getString(R.string.something_with_parenthesis, verseName, book.abbreviation)
        } else {
            view.verseText.text = verseName
        }

        // Set value for the date text field
        val sDt = DateFormat.format("yyyy-MM-dd HH:mm", item.createdAt).toString()
        view.dateText.text = sDt

        // set value for the second text field
        try {
            val verseText = swordContentFacade.getBookmarkVerseText(item)
            view.verseContentText.text = verseText
        } catch (e: Exception) {
            Log.e(TAG, "Error loading label verse text", e)
            view.verseContentText.text = ""
        }
        return view
    }

    companion object {
        private const val TAG = "BookmarkItemAdapter"
    }

}
