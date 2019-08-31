package ru.skillbranch.devintensive.utils

import android.content.res.Resources
import android.service.voice.AlwaysOnHotwordDetector
import androidx.annotation.Px

object Utils
{
    fun parseFullName(fullName: String?) : Pair<String?, String?>
    {
        val firstName : String?
        val lastName : String?

        if(fullName.isNullOrBlank())
        {
           firstName = null
           lastName = null
        }
        else
        {
            val newFullName : String? = fullName.trim()
            val parts: List<String>? = newFullName?.split(" ")

            firstName = parts?.getOrNull(0)
            lastName = parts?.getOrNull(1)
        }

        return firstName to lastName
    }

    fun toInitials(firstName: String?, lastName: String?): String?
    {
        if(!firstName.isNullOrBlank() && !lastName.isNullOrBlank())
            return ("${firstName.first().toUpperCase()}${lastName.first().toUpperCase()}")
        else if(!firstName.isNullOrBlank() && lastName.isNullOrBlank())
            return ("${firstName.first().toUpperCase()}")
        else if(firstName.isNullOrBlank() && !lastName.isNullOrBlank())
            return ("${lastName.first().toUpperCase()}")
        else return null
    }

    fun transliteration(payload: String, divider: String = " "): String
    {
        var name: String = payload
        if(!name.isNullOrBlank())
        {
            var map: HashMap<String, String> = hashMapOf(
                "а" to "a",

                "б" to "b",

                "в" to "v",

                "г" to "g",

                "д" to "d",

                "е" to "e",

                "ё" to "e",

                "ж" to "zh",

                "з" to "z",

                "и" to "i",

                "й" to "i",

                "к" to "k",

                "л" to "l",

                "м" to "m",

                "н" to "n",

                "о" to "o",

                "п" to "p",

                "р" to "r",

                "с" to "s",

                "т" to "t",

                "у" to "u",

                "ф" to "f",

                "х" to "h",

                "ц" to "c",

                "ч" to "ch",

                "ш" to "sh",

                "щ" to "sh'",

                "ъ" to "",

                "ы" to "i",

                "ь" to "",

                "э" to "e",

                "ю" to "yu",

                "я" to "ya"
            )

            name = name.replace(" ", divider)

            name.forEach {
                if (map[it.toLowerCase().toString()] != null) {
                    if (it.isUpperCase()) {
                        val tempIt = it.toLowerCase()
                        name = name.replace(it.toString(), map[tempIt.toString()].toString().capitalize())
                    } else name = name.replace(it.toString(), map[it.toString()].toString())
                }
            }
        }
        else name = ""
        return name
    }

    fun convertDpToPx(dp : Float) = (dp * Resources.getSystem().displayMetrics.density).toInt()
    fun convertPxToDp(px : Int) = (px / Resources.getSystem().displayMetrics.density).toInt()
    fun convertSpToPx(sp: Int) = (sp * Resources.getSystem().displayMetrics.scaledDensity)
}