package api.services

import com.olegator.mvvmcatsapp.api.models.PostCat
import com.olegator.mvvmcatsapp.api.models.VoteCat
import com.olegator.mvvmcatsapp.api.models.images.CatImage
import com.olegator.mvvmcatsapp.api.models.images.FavoritesResponse
import retrofit2.Response
import retrofit2.http.*

interface ImagesService {
    @GET("images/search")
    suspend fun getImages(@QueryMap parameters: Map<String, String> = emptyMap()): Response<MutableList<CatImage>>

    @GET("favourites")
    suspend fun getFavorites(@QueryMap parameters: Map<String, String> = emptyMap()): FavoritesResponse

    @POST("favourites")
    suspend fun setFavorite(@Body body: PostCat)

    @POST("votes")
    suspend fun setVote(@Body body: VoteCat)
}