package com.log.game.adapter.output.weather

data class Grid(val nx: Int, val ny: Int)

object StadiumGrid {

    private val GRIDS: Map<String, Grid> = mapOf(
        "잠실" to Grid(61, 126),
        "고척" to Grid(58, 125),
        "수원kt위즈파크" to Grid(60, 121),
        "수원" to Grid(60, 121),
        "인천SSG랜더스필드" to Grid(54, 124),
        "인천" to Grid(54, 124),
        "창원NC파크" to Grid(90, 77),
        "창원" to Grid(90, 77),
        "사직" to Grid(97, 74),
        "부산" to Grid(97, 74),
        "대구삼성라이온즈파크" to Grid(89, 90),
        "대구" to Grid(89, 90),
        "광주기아챔피언스필드" to Grid(58, 74),
        "광주" to Grid(58, 74),
        "대전한화생명이글스파크" to Grid(67, 100),
        "대전" to Grid(67, 100),
    )

    fun find(venue: String): Grid? =
        GRIDS.entries.firstOrNull { venue.contains(it.key) }?.value
}
