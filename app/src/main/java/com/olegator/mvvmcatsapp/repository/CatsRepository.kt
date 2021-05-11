package com.olegator.mvvmcatsapp.repository


import api.services.ImagesService
import com.olegator.mvvmcatsapp.api.models.PostCat
import com.olegator.mvvmcatsapp.api.models.VoteCat
import com.olegator.mvvmcatsapp.api.models.images.CatImage


class CatsRepository(
    private val service: ImagesService
) {
    suspend fun getCatsImages(limit: Int, pageNumber: Int) =
        service.getImages(parameters = mapOf("limit" to "$limit", "page" to "$pageNumber"))

    suspend fun getCatsByBreed(limit: Int, pageNumber: Int, breed: String) =
        service.getImages(parameters = mapOf("limit" to "$limit", "page" to "$pageNumber", "breed_ids" to breed))

    suspend fun getFavorites(sub_id: String, limit: Int, pageNumber: Int): MutableList<CatImage> {
        val rawResponse =
            service.getFavorites(
                parameters = mapOf(
                    "sub_id" to sub_id,
                    "limit" to "$limit",
                    "page" to "$pageNumber"
                )
            )
        return rawResponse.map { CatImage(it.image.id, it.image.url) }.toMutableList()
    }


    suspend fun setFavorite(image_id: String?, sub_id: String?) =
        service.setFavorite(body = PostCat(image_id, sub_id))

    suspend fun setVote(image_id: String?, sub_id: String?, value: Int?) =
        service.setVote(body = VoteCat(image_id, sub_id, value))
}