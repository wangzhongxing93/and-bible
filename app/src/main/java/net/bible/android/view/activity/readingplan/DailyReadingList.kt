/*
 * Copyright (c) 2020-2022 Martin Denham, Tuomas Airaksinen and the AndBible contributors.
 *
 * This file is part of AndBible: Bible Study (http://github.com/AndBible/and-bible).
 *
 * AndBible is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * AndBible is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with AndBible.
 * If not, see http://www.gnu.org/licenses/.
 */

package net.bible.android.view.activity.readingplan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView

import net.bible.android.activity.R
import net.bible.android.activity.databinding.ListBinding
import net.bible.android.control.readingplan.ReadingPlanControl
import net.bible.android.view.activity.base.Dialogs
import net.bible.android.view.activity.base.ListActivityBase
import net.bible.service.readingplan.OneDaysReadingsDto

import javax.inject.Inject

/** show a history list and allow to go to history item
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 */
class DailyReadingList : ListActivityBase() {
    private lateinit var binding: ListBinding

    @Inject lateinit var readingPlanControl: ReadingPlanControl

    private lateinit var readingsList: List<OneDaysReadingsDto>
    private lateinit var adapter: ArrayAdapter<OneDaysReadingsDto>

    /** Called when the activity is first created.  */
    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState, true)
        Log.i(TAG, "Displaying General Book Key chooser")
        binding = ListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        buildActivityComponent().inject(this)

		readingsList = readingPlanControl.currentPlansReadingList

        adapter = DailyReadingItemAdapter(this, R.layout.two_line_list_item, readingsList)
        listAdapter = adapter

        listView.isFastScrollEnabled = true

        isIntegrateWithHistoryManager = false
        Log.i(TAG, "Finished displaying Search view")
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        try {
            itemSelected(readingsList[position])
        } catch (e: Exception) {
            Log.e(TAG, "Selection error", e)
            Dialogs.showErrorMsg(R.string.error_occurred, e)
        }

    }

    private fun itemSelected(oneDaysReadingsDto: OneDaysReadingsDto) {
        Log.i(TAG, "Day selected:$oneDaysReadingsDto")
        try {
            setResult(RESULT_OK, Intent(oneDaysReadingsDto.day.toString()))
            finish()
        } catch (e: Exception) {
            Log.e(TAG, "error on select of gen book key", e)
        }

    }

    companion object {
        private const val TAG = "DailyReadingList"
    }
}
