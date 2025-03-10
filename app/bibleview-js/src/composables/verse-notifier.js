/*
 * Copyright (c) 2021-2022 Martin Denham, Tuomas Airaksinen and the AndBible contributors.
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
import {ref, watch} from "vue";
import {computed} from "vue";
import {setupWindowEventListener} from "@/utils";
import {throttle} from "lodash";

export function useVerseNotifier(config, calculatedConfig, mounted, {scrolledToOrdinal}, topElement, {isScrolling}) {
    const currentVerse = ref(null);
    watch(() => currentVerse.value,  value => scrolledToOrdinal(value));

    const lineHeight = computed(() => {
        config; // Update also when font settings etc are changed
        if(!mounted.value || !topElement.value) return 1;
        return parseFloat(window.getComputedStyle(topElement.value).getPropertyValue('line-height'));
        }
    );

    let lastDirection = "ltr";
    const step = 10;

    function *iterate(direction = "ltr") {
        if(direction === "ltr") {
            for (let x = window.innerWidth - step; x > 0; x -= step) {
                yield x;
            }
        } else {
            for (let x = step; x < window.innerWidth; x += step) {
                yield x;
            }
        }
    }

    const onScroll = throttle(() => {
        if(isScrolling.value) return;
        const y = calculatedConfig.value.topOffset + lineHeight.value*0.8;

        // Find element, starting from right
        let element;
        let directionChanged = true;
        while(directionChanged) {
            directionChanged = false;
            for(const x of iterate(lastDirection)) {
                element = document.elementFromPoint(x, y)
                if (element) {
                    element = element.closest(".ordinal");
                    if (element) {
                        const direction = window.getComputedStyle(element).getPropertyValue("direction");
                        if(direction !== lastDirection) {
                            directionChanged = true;
                            lastDirection = direction;
                            break;
                        }
                        currentVerse.value = parseInt(element.dataset.ordinal)
                        break;
                    }
                }
            }
        }
    }, 50);

    setupWindowEventListener('scroll', onScroll)
    return {currentVerse}
}
