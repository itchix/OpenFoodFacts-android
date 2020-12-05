/*
 * Copyright 2016-2020 Open Food Facts
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package openfoodfacts.github.scrachx.openfood.utils

import android.content.DialogInterface

interface QuestionActionListeners {
    fun onPositiveFeedback(dialog: QuestionDialog)
    fun onNegativeFeedback(dialog: QuestionDialog)
    fun onAmbiguityFeedback(dialog: QuestionDialog)
    fun onCancelListener(dialog: DialogInterface)
}