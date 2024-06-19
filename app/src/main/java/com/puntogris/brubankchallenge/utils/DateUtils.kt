package com.puntogris.brubankchallenge.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateUtils {

    fun getYearFromDate(dateString: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(dateString, formatter)
        return date.year.toString()
    }
}