package com.olegator.mvvmcatsapp.api.models.images

data class FavoritesResponseItem(
    val created_at: String,
    val id: Int,
    val image: CatImage,
    val image_id: String,
    val sub_id: String,
    val user_id: String
)