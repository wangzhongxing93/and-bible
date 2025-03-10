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
package net.bible.android.control.page

import android.view.Menu
import net.bible.android.view.activity.base.ActivityBase
import org.crosswire.jsword.book.Book
import org.crosswire.jsword.passage.Key

/**
 * @author Martin Denham [mjdenham at gmail dot com]
 */
interface CurrentPage {
    val currentDocumentAbbreviation: String get () = currentDocument?.abbreviation?: ""
    val currentDocumentName: String get() = currentDocument?.name?:""

    val documentCategory: DocumentCategory
    val pageManager: CurrentPageManager

    fun startKeyChooser(context: ActivityBase)

    operator fun next()
    fun previous()
    /** get incremented key according to the type of page displayed - verse, chapter, ...
     */
    fun getKeyPlus(num: Int): Key

    /** add or subtract a number of pages from the current position and return Page
     */
    fun getPagePlus(num: Int): Key

    /** set key without updating screens  */
    fun doSetKey(key: Key?)

    val isSingleKey: Boolean
    // bible and commentary share a key (verse)
    val isShareKeyBetweenDocs: Boolean

	var _key: Key?

    /** get current key */
    val key: Key?

    val displayKey: Key?

	/** set key and update screens  */
	fun setKey(key: Key)

    /** get key for 1 verse instead of whole chapter if bible
     */
    val singleKey: Key?

    val currentDocument: Book?

	fun setCurrentDocument(doc: Book?)
    fun setCurrentDocumentAndKey(doc: Book, key: Key)

    fun checkCurrentDocumenInstalled(): Boolean
    /** get a page to display  */
    val currentPageContent: Document

    fun getPageContent(key: Key): Document

    var isInhibitChangeNotifications: Boolean
    val isSearchable: Boolean
    val isSpeakable: Boolean
    val isSyncable: Boolean
    //screen offset as a percentage of total height of screen
    var anchorOrdinal: Int?

}
