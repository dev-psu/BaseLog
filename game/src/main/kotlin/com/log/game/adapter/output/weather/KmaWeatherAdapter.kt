package com.log.game.adapter.output.weather

import com.log.game.domain.model.GameWeather
import com.log.game.domain.model.WeatherCondition
import com.log.game.domain.port.output.WeatherPort
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriComponentsBuilder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class KmaWeatherAdapter(
    @Value("\${weather.kma.service-key:}") private val serviceKey: String,
) : WeatherPort {

    private val log = LoggerFactory.getLogger(javaClass)
    private val restClient = RestClient.create()

    @Cacheable(cacheNames = ["stadium-weather"], key = "#venue", unless = "#result == null")
    override fun getCurrentWeather(venue: String): GameWeather? {
        if (serviceKey.isBlank()) return null
        val grid = StadiumGrid.find(venue) ?: return null

        val adjusted = LocalDateTime.now().minusMinutes(30)
        val baseDate = adjusted.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val baseTime = "%02d00".format(adjusted.hour)

        return try {
            val uri = UriComponentsBuilder.fromUriString("$BASE_URL/getUltraSrtNcst")
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", "10")
                .queryParam("pageNo", "1")
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", grid.nx)
                .queryParam("ny", grid.ny)
                .build(true)
                .toUri()

            restClient.get().uri(uri).retrieve().body(KmaResponse::class.java)?.toGameWeather()
        } catch (e: Exception) {
            log.warn("기상청 API 호출 실패: venue=$venue, ${e.message}")
            null
        }
    }

    companion object {
        private const val BASE_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0"
    }
}

data class KmaResponse(val response: KmaBody?)
data class KmaBody(val body: KmaItems?)
data class KmaItems(val items: KmaItemList?)
data class KmaItemList(val item: List<KmaItem>?)
data class KmaItem(val category: String, val obsrValue: String)

fun KmaResponse.toGameWeather(): GameWeather? {
    val items = response?.body?.items?.item ?: return null
    val pty = items.find { it.category == "PTY" }?.obsrValue?.trim()?.toIntOrNull() ?: return null
    val t1h = items.find { it.category == "T1H" }?.obsrValue?.trim()?.toDoubleOrNull()

    val condition = when (pty) {
        1 -> WeatherCondition.RAINY
        2 -> WeatherCondition.SLEET
        3 -> WeatherCondition.SNOWY
        4 -> WeatherCondition.SHOWER
        5, 6 -> WeatherCondition.RAINY
        7 -> WeatherCondition.SNOWY
        else -> WeatherCondition.SUNNY
    }
    return GameWeather(condition = condition, temperatureCelsius = t1h)
}
