<!--
  - Copyright (c) 2021-2022 Martin Denham, Tuomas Airaksinen and the AndBible contributors.
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
  <div>
    <h3 v-if="severity === ErrorSeverity.ERROR">{{strings.errorTitle}}</h3>
    <h3 v-if="severity === ErrorSeverity.WARNING">{{strings.warningTitle}}</h3>
    <h3 v-if="severity === ErrorSeverity.NORMAL">{{strings.normalTitle}}</h3>
    <OsisSegment convert :osis-template="document.errorMessage"/> <a v-if="severity > ErrorSeverity.NORMAL" href="ab-error://">{{strings.reportError}}</a>
  </div>
</template>

<script>
import {useCommon} from "@/composables";
import {computed} from "vue";
import OsisSegment from "@/components/documents/OsisSegment";

const ErrorSeverity = {
  NORMAL: 1,
  WARNING: 2,
  ERROR: 3,
}

export default {
  name: "ErrorDocument",
  components: {OsisSegment},
  props: {
    document: {type: Object, required: true},
  },
  setup(props) {
    const severity = computed(() => ErrorSeverity[props.document.severity]);
    return {severity, ...useCommon(), ErrorSeverity}
  }
}
</script>

