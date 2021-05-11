package api

import api.interceptors.ApiKeyRequestInterceptor
import api.services.ImagesService
import com.olegator.mvvmcatsapp.util.Constants.Companion.BASE_URL
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkProvider {

    private val retrofit: Retrofit

    init {
        retrofit = createProvider()
    }

    private fun createProvider(): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(ApiKeyRequestInterceptor())
            .build()

        return Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun createImagesService(): ImagesService = retrofit.create(ImagesService::class.java)

}