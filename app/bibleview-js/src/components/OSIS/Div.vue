<!--
  - Copyright (c) 2020-2022 Martin Denham, Tuomas Airaksinen and the AndBible contributors.
  -
  - This file is part of AndBible: Bible Study (http://github.com/AndBible/and-bible).
  -
  - AndBible is free software: you can redistribute it and/or modify it under the
  - terms of the GNU General Public License as published by the Free Software Foundation,
  - either version 3 of the License, or (at your option) any later version.
  -
  - AndBible is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
  - without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
  - See the GNU General Public License for more details.
  -
  - You should have received a copy of the GNU General Public License along with AndBible.
  - If not, see http://www.gnu.org/licenses/.
  -->

<template>
  <template v-if="isParagraph">
    <span v-if="verseInfo" class="paragraphBreak">&nbsp;</span>
    <div v-else class="paragraphBreak">&nbsp;</div>
  </template>
  <VerseNumber v-else-if="isPreVerse && shown" :verse-num="verseInfo.verse"/>
  <template v-else-if="isCanonical || (!isCanonical && config.showNonCanonical)">
    <span v-if="verseInfo" :class="{'skip-offset': !isCanonical}"><slot/></span>
    <div v-else :class="{'skip-offset': !isCanonical}"><slot/></div>
  </template>
</template>

<script>
import {inject, ref} from "vue";
import VerseNumber from "@/components/VerseNumber";
import {checkUnsupportedProps, useCommon} from "@/composables";
import {computed} from "vue";

export default {
  name: "Div",
  components: {VerseNumber},
  setup(props) {
    checkUnsupportedProps(props, "type",
                          ["x-p", "x-gen", "x-milestone", "section", "majorSection",
                           "paragraph", "book", "variant", "introduction", "colophon"]);
    checkUnsupportedProps(props, "canonical", ["true", "false"]);
    checkUnsupportedProps(props, "subType", ["x-preverse"]);
    checkUnsupportedProps(props, "annotateRef");
    checkUnsupportedProps(props, "annotateType");

    const verseInfo = inject("verseInfo", null);
    let shown = false;

    function isPreVerse(type, subType) {
      return type === "x-milestone" && subType === "x-preverse";
    }

    if(isPreVerse(props) && verseInfo) {
      // eslint-disable-next-line vue/no-ref-as-operand
      shown = ref(true);
      for (const oldValue of verseInfo.showStack) {
        oldValue.value = false;
      }
      verseInfo.showStack.push(shown);
    }
    const common = useCommon();
    const isParagraph = computed(() => ['x-p', 'paragraph', 'colophon'].includes(props.type) && props.sID);
    const isCanonical = computed(() => props.canonical !== "false");

    return {
      verseInfo,
      isPreVerse: computed(() => isPreVerse(props.type, props.subType)),
      isParagraph,
      isCanonical,
      shown,
      ...common
    };
  },
  props: {
    osisID: {type: String, default: null},
    sID: {type: String, default: null},
    eID: {type: String, default: null},
    type: {type: String, default: null},
    subType: {type: String, default: null},
    annotateRef: {type: String, default: null},
    canonical: {type: String, default: null},
    annotateType: {type: String, default: null},
  },
}
</script>

<style lang="scss" scoped>
  @import "~@/common.scss";
</style>
