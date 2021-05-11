package com.olegator.mvvmcatsapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.olegator.mvvmcatsapp.api.models.images.CatImage
import com.olegator.mvvmcatsapp.repository.CatsRepository
import com.olegator.mvvmcatsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class CatsViewModel(
    private val catsRepository: CatsRepository
) : ViewModel() {

    val catsImages: MutableLiveData<Resource<MutableList<CatImage>>> = MutableLiveData()
    private var catsImagesPage = 0
    private var catsImagesResponse: MutableList<CatImage>? = null

    val favouriteCats: MutableLiveData<Resource<MutableList<CatImage>>> = MutableLiveData()
    private var favoriteCatsPage = 0
    private var favoriteCatsResponse: MutableList<CatImage>? = null

    init {
        getImages(10)
        getFavorites("user", 10)
    }

    fun getImages(limit: Int) = viewModelScope.launch {
        catsImages.postValue(Resource.Loading())
        val response = catsRepository.getCatsImages(limit, catsImagesPage)
        catsImages.postValue(handleCatsImageResponse(response))
    }

    fun getFavorites(sub_id: String, limit: Int) = viewModelScope.launch {
        favouriteCats.postValue(Resource.Loading())
        val response = catsRepository.getFavorites(sub_id, limit, favoriteCatsPage)
        favouriteCats.postValue(handleFavoriteCatsResponse(response))
    }

    private fun handleCatsImageResponse(response: Response<MutableList<CatImage>>): Resource<MutableList<CatImage>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                catsImagesPage++
                if (catsImagesResponse == null) {
                    catsImagesResponse = resultResponse
                } else {
                    val oldCatImages = catsImagesResponse
                    oldCatImages?.addAll(resultResponse)
                }
                return Resource.Success(catsImagesResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleFavoriteCatsResponse(response: MutableList<CatImage>): Resource<MutableList<CatImage>> {
        favoriteCatsPage++
        if (favoriteCatsResponse == null) {
            favoriteCatsResponse = response
        } else {
            val oldCatImages = favoriteCatsResponse
            oldCatImages?.addAll(response)
        }
        return Resource.Success(favoriteCatsResponse ?: response)
    }

    fun saveCat(cat: CatImage) = viewModelScope.launch {
        catsRepository.setFavorite(cat.id, "user")
    }

    fun likeCat(cat: CatImage) = viewModelScope.launch {
        catsRepository.setVote(cat.id, "user", 1)
    }

    fun nopeCat(cat: CatImage) = viewModelScope.launch {
        catsRepository.setVote(cat.id, "user", 0)
    }
}