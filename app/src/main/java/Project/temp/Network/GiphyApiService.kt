package Project.temp.Network

import Project.temp.models.dataclass.GiphyResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


// Sealed class for API response states
sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val message: String) : ApiResult<Nothing>()
}

// Retrofit API Service
interface GiphyApiService {
    @GET("v1/gifs/search")
    suspend fun searchGifs(
        @Query("api_key") apiKey: String,
        @Query("q") query: String,
        @Query("limit") limit: Int = 25,
        @Query("offset") offset: Int = 0
    ): Response<GiphyResponse>
}

// Retrofit instance with custom configuration
object RetrofitInstance {
    private const val BASE_URL = "https://api.giphy.com/"

    val api: GiphyApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                okhttp3.OkHttpClient.Builder()
                    .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS) // Set connection timeout
                    .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS) // Set read timeout
                    .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS) // Set write timeout
                    .build()
            )
            .build()
            .create(GiphyApiService::class.java)
    }
}

// Helper function to handle API responses
suspend fun <T> handleApiResponse(
    apiCall: suspend () -> Response<T>
): ApiResult<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful && response.body() != null) {
            ApiResult.Success(response.body()!!)
        } else {
            ApiResult.Error("Error: ${response.message()}")
        }
    } catch (e: Exception) {
        ApiResult.Error("Failed to fetch data: ${e.message}")
    }
}