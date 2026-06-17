package com.log.game.adapter.output.persistence

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import tools.jackson.module.kotlin.jacksonObjectMapper
import tools.jackson.module.kotlin.readValue

data class InningsData(val away: List<Int>, val home: List<Int>)

@Converter
class InningsConverter : AttributeConverter<InningsData, String> {
    private val mapper = jacksonObjectMapper()

    override fun convertToDatabaseColumn(attribute: InningsData): String =
        mapper.writeValueAsString(attribute)

    override fun convertToEntityAttribute(dbData: String): InningsData =
        mapper.readValue(dbData)
}
