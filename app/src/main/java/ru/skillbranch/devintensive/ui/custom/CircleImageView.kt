package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.ImageView
import ru.skillbranch.devintensive.utils.Utils

class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context ,attrs,defStyleAttr)
{

    override fun onDraw(canvas: Canvas) {
        //super.onDraw(canvas)
        val paintBorder = Paint(Paint.ANTI_ALIAS_FLAG)
        val paintBitmap = Paint(Paint.ANTI_ALIAS_FLAG)
        val bitmap = Bitmap.createBitmap(Utils.convertDpToPx(150), Utils.convertDpToPx(150), Bitmap.Config.ARGB_8888)

        val subCanvas = Canvas(bitmap)
        paintBitmap.color = Color.RED
        subCanvas.drawRect(Rect(350, 310, 90, 90), paintBitmap)

        canvas.drawBitmap(bitmap,0f,0f, null)
    }
}