package openfoodfacts.github.scrachx.openfood.images

import com.fasterxml.jackson.databind.JsonNode

/**
 * Extract images informations form json.
 */
object ImageNameJsonParser {
    /**
     * @param rootNode json representing images entries given by api/v0/product/XXXX.json?fields=images
     */
    @JvmStatic
    fun extractImagesNameSortedByUploadTimeDesc(rootNode: JsonNode): List<String> {
        // a json object referring to images
        val namesWithTime = mutableListOf<NameUploadedTimeKey>()
        rootNode["product"]["images"]?.let{ images ->
            images.fields()?.let {
                while (it.hasNext()) {
                    val image = it.next()
                    val imageName = image.key
                    // do not include images with contain nutrients, ingredients or other in their names
                    // as they are duplicate and do not load as well
                    if (!isNameAccepted(imageName)) {
                        continue
                    }
                    namesWithTime.add(NameUploadedTimeKey(imageName, image.value["uploaded_t"].asLong()))
                }
            }
        }

        return namesWithTime.sorted().map { it.name }
    }

    private fun isNameAccepted(namesString: String): Boolean {
        return (namesString.isNotBlank()
                && !namesString.contains("n")
                && !namesString.contains("f")
                && !namesString.contains("i")
                && !namesString.contains("o"))
    }

    private data class NameUploadedTimeKey(
            val name: String,
            private val timestamp: Long
    ) : Comparable<NameUploadedTimeKey> {

        /**
         * to be ordered from newer to older.
         */
        override fun compareTo(other: NameUploadedTimeKey): Int {
            val deltaInTime = other.timestamp - timestamp
            return when {
                deltaInTime > 0 -> 1
                deltaInTime < 0 -> -1
                else -> name.compareTo(other.name)
            }
        }
    }
}