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

package net.bible.android.view.activity.readingplan.actionbar

import net.bible.android.control.ApplicationScope

import org.crosswire.jsword.book.Book

import javax.inject.Inject

/**
 * @author Martin Denham [mjdenham at gmail dot com]
 */
@ApplicationScope
class ReadingPlanCommentaryActionBarButton @Inject
constructor() : ReadingPlanQuickDocumentChangeButton() {


    override fun getSuggestedDocument(): Book? =
        activeWindowPageManagerProvider.activeWindowPageManager.currentCommentary.currentDocument

    /**
     * Portrait actionbar is a bit squashed if speak controls are displayed so hide commentary
     */
    override val canShow get(): Boolean = super.canShow && (isWide || !isSpeakMode)
}
