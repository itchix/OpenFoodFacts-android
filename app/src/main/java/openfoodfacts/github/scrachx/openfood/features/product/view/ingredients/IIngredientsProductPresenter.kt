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
package openfoodfacts.github.scrachx.openfood.features.product.view.ingredients

import io.reactivex.disposables.Disposable
import openfoodfacts.github.scrachx.openfood.models.entities.additive.AdditiveName
import openfoodfacts.github.scrachx.openfood.models.entities.allergen.AllergenName
import openfoodfacts.github.scrachx.openfood.utils.ProductInfoState

/**
 * Created by Lobster on 17.03.18.
 */
interface IIngredientsProductPresenter {
    interface Actions: Disposable {
        fun loadAdditives()
        fun loadAllergens()
    }

    interface View {
        fun showAdditives(additives: List<AdditiveName>)
        fun setAdditivesState(state: ProductInfoState)

        fun showAllergens(allergens: List<AllergenName>)
        fun setAllergensState(state: ProductInfoState)
    }
}