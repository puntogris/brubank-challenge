package com.puntogris.brubankchallenge.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import coil.size.Size
import coil.transform.Transformation

class BlackAndWhiteTransformation : Transformation {

    override val cacheKey: String
        get() = "BlackAndWhiteTransformation"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {

        val output = Bitmap.createBitmap(input.width, input.height, input.config)

        val colorMatrix = ColorMatrix().apply {
            setSaturation(0.2f)
        }

        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(colorMatrix)
        }

        Canvas(output).drawBitmap(input, 0f, 0f, paint)

        return output
    }
}
