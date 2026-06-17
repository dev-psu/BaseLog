package com.log.watchlog.domain.model

enum class WeatherCondition {
    SUNNY,
    PARTLY_CLOUDY,
    CLOUDY,
    RAINY,
    SLEET,
    SNOWY,
    SHOWER,
}

data class WeatherInfo(
    val condition: WeatherCondition,
    val temperatureCelsius: Double?,
)
