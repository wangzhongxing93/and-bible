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
package net.bible.android.control.search

import org.crosswire.jsword.passage.Key
import java.util.ArrayList

class SearchResultsDto {
    val mainSearchResults: MutableList<Key> = ArrayList()
    val otherSearchResults: MutableList<Key> = ArrayList()
    fun add(resultKey: Key, isMain: Boolean) {
        if (isMain) {
            mainSearchResults.add(resultKey)
        } else {
            otherSearchResults.add(resultKey)
        }
    }

    val size: Int get() = mainSearchResults.size + otherSearchResults.size
}
