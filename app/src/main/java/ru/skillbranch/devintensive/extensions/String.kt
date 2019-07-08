package ru.skillbranch.devintensive.extensions

import java.util.Collections.replaceAll

fun String.truncate(value: Int = 16) : String?
{
    var result = this
    var _value = value
    val dots = "..."
    result = result.trim()

    if(result.length < (_value + 1))
        return "$result"
    else
    {
        result = result.substring(0,_value)
        if(result.last().isWhitespace())
        {
            result = result.substring(0, result.length - 1) + dots
            return "$result"
        }
        else return "${result+dots}"
    }
}

//fun String.stripHtml() : String?
//{
//    var result = this
//    result = result.replace(Regex("\\<[^>]*>"),"")
//    //result = result.replace(Regex("&\\d+;|&\\w+"),"")
//    result = result.replace(Regex("&[^ а-я]{1,4}?;"),"")
//    result = result.replace(Regex("&lt;[^&gt;]*"),"")
//
//    return "$result"
//}

fun String.stripHtml(): String? = this.replace(Regex("\\<[^>]*>"), "").
    replace(Regex("&[^ а-я]{1,4}?;"), "").
    replace(Regex("[^\\S\\r\\n]+"), " ")