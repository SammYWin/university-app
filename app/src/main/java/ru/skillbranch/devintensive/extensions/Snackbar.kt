package ru.skillbranch.devintensive.extensions

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import com.google.android.material.snackbar.Snackbar
import ru.skillbranch.devintensive.R

fun Snackbar.config(context: Context){
    val params = this.view.layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(20, 20, 20, 20)

    this.view.layoutParams = params
    this.view.background = context.getDrawable(R.drawable.bg_snackbar)

    ViewCompat.setElevation(this.view, 6f)
}