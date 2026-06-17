package com.log.watchlog.domain.model

enum class ImageType { TICKET, PHOTO }

data class WatchLogImage(
    val id: Long = 0,
    val imageUrl: String,
    val imageType: ImageType,
    val sortOrder: Int,
)
