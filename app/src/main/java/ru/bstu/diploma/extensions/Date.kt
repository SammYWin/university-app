package ru.bstu.diploma.extensions

import java.text.SimpleDateFormat
import java.util.*

enum class TimeUnits {
    SECOND, MINUTE, HOUR, DAY;

    fun plural(value: Int): String? {
        var _value: Int = value
        var result: String = ""

        when (this) {
            SECOND -> when (_value % 10) {
                1 -> if (_value % 100 != 11) {
                    result = "$_value секунду"
                } else result = "$_value секунд"
                2, 3, 4 -> if (_value % 100 !in 12..14) {
                    result = "$_value секунды"
                } else result = "$_value секунд"
                else -> result = "$_value секунд"
            }

            MINUTE -> when (_value % 10) {
                1 -> if (_value % 100 != 11) {
                    result = "$_value минуту"
                } else result = "$_value минут"
                2, 3, 4 -> if (_value % 100 !in 12..14) {
                    result = "$_value минуты"
                } else result = "$_value минут"
                else -> result = "$_value минут"
            }

            HOUR -> when (_value % 10) {
                1 -> if (_value % 100 != 11) {
                    result = "$_value час"
                } else result = "$_value часов"
                2, 3, 4 -> if (_value % 100 !in 12..14) {
                    result = "$_value часа"
                } else result = "$_value часов"
                else -> result = "$_value часов"
            }

            DAY -> when (_value % 10) {
                1 -> if (_value % 100 != 11) {
                    result = "$_value день"
                } else result = "$_value дней"
                2, 3, 4 -> if (_value % 100 !in 12..14) {
                    result = "$_value дня"
                } else result = "$_value дней"
                else -> result = "$_value дней"
            }
        }
        return "$result"
    }
}

const val SECOND = 1000L
const val MINUTE = SECOND * 60
const val HOUR = MINUTE * 60
const val DAY = HOUR * 24

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.shortFormat(): String? {
    val pattern = if(this.isSameDay(Date())) "HH:mm" else "dd.MM.yy"
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))

    return dateFormat.format(this)
}

private fun Date.isSameDay(date: Date): Boolean {
    val day1 = this.time / DAY
    val day2 = date.time / DAY

    return day1 == day2
}

fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND): Date {
    var time = this.time

    time += when (units) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }
    this.time = time
    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    var value: Long = (date.time - this.time)
    println(
        """
       date.time = ${date.time}
       this.time = ${this.time}
   """.trimIndent()
    )
    var result: String = ""
    var inFuture: Boolean = false
    if (value < 0) {
        value *= -1
        inFuture = true
    }
    when (value / SECOND) {
        in 0..1 -> return ("только что")
        in 1..45 -> result = "несколько секунд"
        in 45..75 -> result = "минуту"
    }
    when (value) {
        in 75 * SECOND..45 * MINUTE ->
            when (value / MINUTE % 10) {
                1L -> if (value / MINUTE % 100 != 11L) {
                    result = "${(value / MINUTE).toInt()} минуту"
                } else result = "${(value / MINUTE).toInt()} минут"

                2L, 3L, 4L -> if (value / MINUTE % 100 !in 12L..14L) {
                    result = "${(value / MINUTE).toInt()} минуты"
                } else result = "${(value / MINUTE).toInt()} минут"

                else -> result = "${(value / MINUTE).toInt()} минут"
            }
        in 45 * MINUTE..75 * MINUTE -> result = "час"
        in 75 * MINUTE..22 * HOUR ->
            when (value / HOUR % 10) {
                1L -> if (value / HOUR % 100 != 11L) {
                    result = "${(value / HOUR).toInt()} час"
                } else result = "${(value / HOUR).toInt()} часов"

                2L, 3L, 4L -> if (value / HOUR % 100 !in 12L..14L) {
                    result = "${(value / HOUR).toInt()} часа"
                } else result = "${(value / HOUR).toInt()} часов"

                else -> result = "${(value / HOUR).toInt()} часов"
            }
        in 22 * HOUR..26 * HOUR -> result = "день"
        in 22 * HOUR..360 * DAY ->
            when (value / DAY % 10) {
                1L -> if (value / DAY % 100 != 11L) {
                    result = "${(value / DAY).toInt()} день"
                } else result = "${(value / DAY).toInt()} дней"

                2L, 3L, 4L -> if (value / DAY % 100 !in 12L..14L) {
                    result = "${(value / DAY).toInt()} дня"
                }else result = "${(value / DAY).toInt()} дней"

                else -> result = "${(value / DAY).toInt()} дней"
            }
        in 360 * DAY..Int.MAX_VALUE * DAY -> return if (inFuture) "более чем через год" else "более года назад"
    }

    return if (inFuture) "через $result" else "$result назад"
}


