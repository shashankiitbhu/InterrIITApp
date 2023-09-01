package com.example.interriitapp.Network

import android.os.SystemClock
import com.example.interriitapp.Models.Recipe
import com.example.interriitapp.Models.TriviaQuestion
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


fun createEdamamService(): EdamamService {
    val dispatcher = Dispatcher()
    dispatcher.maxRequests = 1

    val interceptor = Interceptor { chain ->
        SystemClock.sleep(500)
        chain.proceed(chain.request())
    }
    val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(180, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true) // Enable automatic retry on connection failures
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Enable request/response logging for debugging
        })
        .addNetworkInterceptor(interceptor)
        .dispatcher(dispatcher)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.edamam.com")
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(EdamamService::class.java)

}
fun createTriviaService(): TriviaApiService {
    val dispatcher = Dispatcher()
    dispatcher.maxRequests = 1

    val interceptor = Interceptor { chain ->
        SystemClock.sleep(500)
        chain.proceed(chain.request())
    }
    val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(180, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true) // Enable automatic retry on connection failures
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Enable request/response logging for debugging
        })
        .addNetworkInterceptor(interceptor)
        .dispatcher(dispatcher)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://opentdb.com/")
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(TriviaApiService::class.java)

}
interface EdamamService{
    @GET("/api/recipes/v2")
    fun searchRecipes(
        @Query("q") query: String?,
        @Query("app_id") appId: String?,
        @Query("app_key") appKey: String?,
        @Query("type") type: String?
    ): Call<ApiResponse>
}

interface TriviaApiService {
    @GET("/api.php")
    fun getTriviaQuestions(
        @Query("amount") amount: Int,  // Number of questions to fetch
        @Query("category") category: Int?, // Category ID (optional)
        @Query("difficulty") difficulty: String?, // Difficulty level (optional)
        @Query("type") type: String? // Question type (optional)
    ): Call<TriviaResponse>
}
class ApiResponse {
    var hits: List<Recipe>? = null // Add more fields as needed
}

data class TriviaResponse(
    val responseCode: Int,
    val results: List<TriviaQuestion>
)
