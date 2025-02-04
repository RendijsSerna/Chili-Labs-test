package Project.temp.repository

import Project.temp.models.dataclass.GiphyResponse
import Project.temp.Network.RetrofitInstance
import retrofit2.Response

class GiphyRepository {
    suspend fun searchGifs(apiKey: String, query: String, limit: Int, offset: Int): Response<GiphyResponse> {
        return RetrofitInstance.api.searchGifs(apiKey, query, limit, offset)
    }
}