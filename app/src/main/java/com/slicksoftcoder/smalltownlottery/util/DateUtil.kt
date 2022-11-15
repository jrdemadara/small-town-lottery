package com.slicksoftcoder.smalltownlottery.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class DateUtil () {

    fun currentDate(): String{
        val currentDate: String
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        currentDate = ("$year-$month-$day").toString()
        return  currentDate
    }

    fun currentYear(): String{
        val currentYear: String
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        currentYear = year.toString()
        return  currentYear
    }

    fun currentMonth(): String{
        val currentMonth: String
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        currentMonth = month.toString()
        return  currentMonth
    }

    fun currentDay(): String{
        val currentDay: String
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        currentDay = day.toString()
        return  currentDay
    }

    fun dateFormat(): String{
        val formatDate: String
        val currentDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        formatDate = currentDate.format(formatter)
        return formatDate
    }

}