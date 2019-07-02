package ru.skillbranch.devintensive.extensions

import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*

enum class TimeUnits
{
    SECOND, MINUTE, HOUR, DAY
}

const val SECOND = 1000L
const val MINUTE = SECOND * 60
const val HOUR = MINUTE * 60
const val DAY = HOUR * 24

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy" ) : String
{
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND) : Date
{
    var time = this.time

    time += when(units)
    {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR  -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }
    this.time = time
    return this
}

fun Date.humanizeDiff(date: Date = Date()): String
{
    var value:Long = (date.time - this.time)
    println("""
       date.time = ${date.time}
       this.time = ${this.time}
   """.trimIndent())
    var result: String =""
    var inFuture:Boolean = false
    if(value<0) {
        value *= -1
        inFuture = true
    }
    when (value / SECOND)
    {
        in 0..1-> return("только что")
        in 1..45-> result = "несколько секунд"
        in 45..75-> result = "минуту"
    }
    when (value)
    {
        in 75 * SECOND..45 * MINUTE->
            when (value/ MINUTE%10)
            {
                1L->if (value%100!= 11L)
                {
                    result = "${(value / MINUTE).toInt()} минуту"
                }
                2L,3L,4L->if (value%100 !in 12L..14L)
                {
                    result = "${(value / MINUTE).toInt()} минуты"
                }
                else -> result = "${(value / MINUTE).toInt()} минут"
            }
        in 45 * MINUTE..75 * MINUTE-> result = "час"
        in 75 * MINUTE..22 * HOUR->
            when(value / HOUR%10)
            {
                1L->if(value%100 != 11L)
                {
                    result = "${(value / HOUR).toInt()} час"
                }
                2L,3L,4L->if (value%100 !in 12L..14L)
                {
                    result = "${(value / HOUR).toInt()} часа"
                }
                else -> result = "${(value / HOUR).toInt()} часов"
            }
        in 22 * HOUR..26 * HOUR-> result="день"
        in 22* HOUR..360 * DAY->
            when(value / DAY%10)
            {
                1L->if(value%100 != 11L)
                {
                    result = "${(value / DAY).toInt()} день"
                }
                2L,3L,4L->if (value%100 !in 12L..14L)
                {
                    result = "${(value / DAY).toInt()} дня"
                }
                else -> result = "${(value / DAY).toInt()} дней"
            }
        in 360 * DAY..Int.MAX_VALUE * DAY-> return if(inFuture) "более чем через год" else "более года назад"
    }

    return if (inFuture) "через $result" else "$result назад"
}


