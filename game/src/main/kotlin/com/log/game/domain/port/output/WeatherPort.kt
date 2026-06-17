package com.log.game.domain.port.output

import com.log.game.domain.model.GameWeather

interface WeatherPort {
    fun getCurrentWeather(venue: String): GameWeather?
}
