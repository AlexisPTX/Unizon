package bapale.rioc.unizon.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class Joke(
    val id: Int,
    val type: String,
    val setup: String,
    val punchline: String
)

data class DogResponse(
    val message: String,
    val status: String
)

interface JokeApiService {
    @GET("random_joke")
    suspend fun getRandomJoke(): Joke
}

interface DogApiService {
    @GET("breeds/image/random")
    suspend fun getRandomDog(): DogResponse
}

object RetrofitInstance {
    private val jokeRetrofit = Retrofit.Builder()
        .baseUrl("https://official-joke-api.appspot.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val dogRetrofit = Retrofit.Builder()
        .baseUrl("https://dog.ceo/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val jokeService: JokeApiService = jokeRetrofit.create(JokeApiService::class.java)
    val dogService: DogApiService = dogRetrofit.create(DogApiService::class.java)
}
