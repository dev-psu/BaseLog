package com.log.watchlog.adapter.output.persistence

import com.log.common.domain.KboTeam
import com.log.watchlog.domain.model.*
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "watch_log")
class WatchLogJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val memberId: Long,

    val gameId: Long?,

    val gameSeason: Short?,

    val venue: String?,

    @Enumerated(EnumType.STRING)
    val watchType: WatchType,

    @Enumerated(EnumType.STRING)
    val cheeringTeam: KboTeam,

    @Enumerated(EnumType.STRING)
    val result: WatchResult,

    val title: String?,

    @Column(columnDefinition = "TEXT")
    val content: String?,

    val seatGrade: String?,
    val seatSection: String?,
    val seatRow: String?,
    val seatNumber: String?,

    val companions: String?,

    @Enumerated(EnumType.STRING)
    val mood: Mood?,

    val ticketCost: Int?,
    val foodCost: Int?,
    val transportCost: Int?,

    @Enumerated(EnumType.STRING)
    val weatherCondition: WeatherCondition?,

    @Column(precision = 4, scale = 1)
    val temperature: BigDecimal?,

    val isPublic: Boolean,

    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,

    @OneToMany(mappedBy = "watchLog", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    val images: MutableList<WatchLogImageJpaEntity> = mutableListOf(),
) {
    fun toDomain() = WatchLog(
        id = id,
        memberId = memberId,
        gameId = gameId,
        gameSeason = gameSeason?.toInt(),
        venue = venue,
        watchType = watchType,
        cheeringTeam = cheeringTeam,
        result = result,
        title = title,
        content = content,
        seatInfo = buildSeatInfo(),
        companions = companions,
        mood = mood,
        cost = buildCost(),
        weather = buildWeather(),
        isPublic = isPublic,
        images = images.map { it.toDomain() },
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    private fun buildSeatInfo(): SeatInfo? {
        if (seatGrade == null && seatSection == null && seatRow == null && seatNumber == null) return null
        return SeatInfo(grade = seatGrade, section = seatSection, row = seatRow, number = seatNumber)
    }

    private fun buildCost(): WatchCost? {
        if (ticketCost == null && foodCost == null && transportCost == null) return null
        return WatchCost(ticketCost = ticketCost, foodCost = foodCost, transportCost = transportCost)
    }

    private fun buildWeather(): WeatherInfo? {
        if (weatherCondition == null) return null
        return WeatherInfo(condition = weatherCondition, temperatureCelsius = temperature?.toDouble())
    }

    companion object {
        fun fromDomain(watchLog: WatchLog): WatchLogJpaEntity = WatchLogJpaEntity(
            id = watchLog.id,
            memberId = watchLog.memberId,
            gameId = watchLog.gameId,
            gameSeason = watchLog.gameSeason?.toShort(),
            venue = watchLog.venue,
            watchType = watchLog.watchType,
            cheeringTeam = watchLog.cheeringTeam,
            result = watchLog.result,
            title = watchLog.title,
            content = watchLog.content,
            seatGrade = watchLog.seatInfo?.grade,
            seatSection = watchLog.seatInfo?.section,
            seatRow = watchLog.seatInfo?.row,
            seatNumber = watchLog.seatInfo?.number,
            companions = watchLog.companions,
            mood = watchLog.mood,
            ticketCost = watchLog.cost?.ticketCost,
            foodCost = watchLog.cost?.foodCost,
            transportCost = watchLog.cost?.transportCost,
            weatherCondition = watchLog.weather?.condition,
            temperature = watchLog.weather?.temperatureCelsius?.toBigDecimal(),
            isPublic = watchLog.isPublic,
            createdAt = watchLog.createdAt,
            updatedAt = watchLog.updatedAt,
        )
    }
}
