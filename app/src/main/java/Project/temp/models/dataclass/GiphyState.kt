package Project.temp.models.dataclass

sealed class GiphyState {
    data object Loading : GiphyState()
    data class Success(val gifs: List<Gif>) : GiphyState()
    data class Error(val message: String) : GiphyState()
}