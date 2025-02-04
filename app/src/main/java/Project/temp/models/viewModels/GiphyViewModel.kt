package Project.temp.models.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import Project.temp.models.dataclass.Gif
import Project.temp.models.dataclass.GifObject
import Project.temp.models.dataclass.GiphyState
import Project.temp.repository.GiphyRepository
import com.example.project.BuildConfig
import java.net.UnknownHostException

class GiphyViewModel(private val repository: GiphyRepository) : ViewModel() {
    private val _giphyState = MutableStateFlow<GiphyState>(GiphyState.Success(emptyList()))
    val giphyState: StateFlow<GiphyState> = _giphyState.asStateFlow()

    private var currentQuery = ""
    private var offset = 0
    private val limit = 25
    private val apiKey = BuildConfig.GIPHY_API_KEY

    fun searchGifs(query: String) {
        if (query.isBlank()) {
            _giphyState.update { GiphyState.Success(emptyList()) }
            return
        }

        currentQuery = query
        offset = 0
        _giphyState.update { GiphyState.Loading }

        viewModelScope.launch {
            fetchGifs(query)
        }
    }

    fun loadMoreGifs() {
        if (_giphyState.value !is GiphyState.Success) return

        offset += limit
        viewModelScope.launch {
            fetchGifs(currentQuery)
        }
    }

    private suspend fun fetchGifs(query: String) {
        try {
            val response = repository.searchGifs(apiKey, query, limit, offset)
            if (response.isSuccessful) {
                val gifs = response.body()?.data?.map { gifObject -> mapToGif(gifObject) } ?: emptyList()
                val currentGifs = (_giphyState.value as? GiphyState.Success)?.gifs ?: emptyList()
                _giphyState.update { GiphyState.Success(currentGifs + gifs) }
            } else {
                _giphyState.update { GiphyState.Error("Error: ${response.message()}") }
            }
        } catch (e: UnknownHostException) {
            _giphyState.update { GiphyState.Error("No internet connection. Please check your network.") }
        } catch (e: Exception) {
            _giphyState.update { GiphyState.Error("Failed to fetch GIFs. Please try again.") }
            e.printStackTrace()
        }
    }

    private fun mapToGif(gifObject: GifObject): Gif {
        return Gif(
            lowQualityUrl = gifObject.images.downsized.url,
            highQualityUrl = gifObject.images.original.url
        )
    }
}