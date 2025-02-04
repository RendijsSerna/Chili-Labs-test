package Project.temp.models.dataclass


// TODO maybe needs to take low quality from API instead
data class GifImages(
    val downsized: GifDetails,
    val original: GifDetails
) {
    constructor(original: GifDetails) : this(
        downsized = reduceQuality(original),
        original = original
    )

    companion object {
        private const val REDUCED_SIZE_FACTOR = 4
        private const val DIMENSION_REDUCTION_FACTOR = 2

        private fun reduceQuality(original: GifDetails): GifDetails {
            return GifDetails(
                url = original.url,
                width = original.width / DIMENSION_REDUCTION_FACTOR,
                height = original.height / DIMENSION_REDUCTION_FACTOR,
                size = original.size / REDUCED_SIZE_FACTOR
            )
        }
    }
}