package com.log.game.domain.model

enum class WeatherCondition {
    SUNNY,
    CLOUDY,
    RAINY,
    SLEET,
    SNOWY,
    SHOWER,
}

data class GameWeather(
    val condition: WeatherCondition,
    val temperatureCelsius: Double?,
)
