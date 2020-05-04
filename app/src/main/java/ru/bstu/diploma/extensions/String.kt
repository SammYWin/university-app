package ru.bstu.diploma.extensions

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

fun String.stripHtml(): String? = this.replace(Regex("\\<[^>]*>"), "").
    replace(Regex("&[^ а-я]{1,4}?;"), "").
    replace(Regex("[^\\S\\r\\n]+"), " ")