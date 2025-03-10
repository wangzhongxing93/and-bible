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
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.ArrayAdapter
import android.widget.ListView

import net.bible.android.activity.R
import net.bible.android.activity.databinding.ListBinding
import net.bible.android.control.readingplan.ReadingPlanControl
import net.bible.android.view.activity.base.Dialogs
import net.bible.android.view.activity.base.ListActivityBase
import net.bible.service.readingplan.ReadingPlanInfoDto

import javax.inject.Inject

/** do the search and show the search results
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 */
class ReadingPlanSelectorList : ListActivityBase() {

    private lateinit var mReadingPlanList: List<ReadingPlanInfoDto>
    private lateinit var mPlanArrayAdapter: ArrayAdapter<ReadingPlanInfoDto>

    @Inject lateinit var readingPlanControl: ReadingPlanControl

    /** Called when the activity is first created.  */
    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState, true)
        Log.i(TAG, "Displaying Reading Plan List")
        val binding = ListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        buildActivityComponent().inject(this)
        try {
            mReadingPlanList = readingPlanControl.readingPlanList
            if (readingPlanControl.readingPlanUserDuplicates)
                Dialogs.showErrorMsg(getString(R.string.plan_duplicate_user_plan))

            mPlanArrayAdapter = ReadingPlanItemAdapter(this, LIST_ITEM_TYPE, mReadingPlanList)
            listAdapter = mPlanArrayAdapter

            registerForContextMenu(listView)
        } catch (e: Exception) {
            Log.e(TAG, "Error occurred analysing reading lists", e)
            Dialogs.showErrorMsg(R.string.error_occurred, e)
            finish()
        }

        isIntegrateWithHistoryManager = false
        Log.i(TAG, "Finished displaying Reading Plan list")
    }

    /** if a plan is selected then ask confirmation, save plan, and go straight to first day
     */
    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        try {
            readingPlanControl.startReadingPlan(mReadingPlanList[position])

            setResult(RESULT_OK, Intent(mReadingPlanList[position].planCode))
            finish()
        } catch (e: Exception) {
            Log.e(TAG, "Plan selection error", e)
            Dialogs.showErrorMsg(R.string.error_occurred, e)
        }

    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.reading_plan_list_context_menu, menu)
    }


    override fun onContextItemSelected(item: MenuItem): Boolean {
        super.onContextItemSelected(item)
        val menuInfo = item.menuInfo as AdapterContextMenuInfo
        val plan = mReadingPlanList[menuInfo.position]
        Log.i(TAG, "Selected " + plan.planCode)
		when (item.itemId) {
			R.id.reset -> {
				readingPlanControl.reset(plan.planCode)
				return true
			}
		}
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val TAG = "ReadingPlanList"

        private const val LIST_ITEM_TYPE = android.R.layout.simple_list_item_2
    }
}
