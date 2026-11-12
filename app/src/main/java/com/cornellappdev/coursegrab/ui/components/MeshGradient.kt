package com.cornellappdev.coursegrab.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.cornellappdev.coursegrab.ui.theme.CourseGrabColors
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

fun Modifier.meshGradientBackground(): Modifier = drawBehind {
    drawImage(
        image = MeshGradient.bitmap,
        srcSize = IntSize(MeshGradient.SIZE, MeshGradient.SIZE),
        dstOffset = IntOffset.Zero,
        dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt()),
        filterQuality = FilterQuality.Low
    )
}

private object MeshGradient {
    const val SIZE = 96

    private val axis = floatArrayOf(0f, 0.33f, 0.67f, 1f)

    // Rows top to bottom, columns left to right, as sRGB.
    private val colors = arrayOf(
        floatArrayOf(1f, 0.42f, 0.42f), floatArrayOf(1f, 0.64f, 0.42f),
        floatArrayOf(1f, 0.82f, 0.42f), floatArrayOf(1f, 0.82f, 0.4f),
        floatArrayOf(0.7f, 0.3f, 0.6f), floatArrayOf(0.8f, 0.55f, 0.5f),
        floatArrayOf(0.9f, 0.7f, 0.45f), floatArrayOf(0.5f, 0.7f, 0.35f),
        floatArrayOf(0.4f, 0.5f, 0.7f), floatArrayOf(0.35f, 0.65f, 0.65f),
        floatArrayOf(0.2f, 0.65f, 0.55f), floatArrayOf(0.1f, 0.55f, 0.7f),
        floatArrayOf(0.02f, 0.84f, 0.63f), floatArrayOf(0.1f, 0.7f, 0.65f),
        floatArrayOf(0.1f, 0.6f, 0.7f), floatArrayOf(0.07f, 0.54f, 0.7f)
    ).map { rgb -> FloatArray(3) { srgbToLinear(rgb[it]) } }

    val bitmap: ImageBitmap by lazy { render() }

    private fun render(): ImageBitmap {
        // Control points share x across rows and y across columns, so the mesh's position
        // mapping is the same 1-D curve on each axis. Invert it to find each pixel's parameter.
        val inverse = FloatArray(SIZE) { inverseAxis((it + 0.5f) / SIZE) }
        val background = CourseGrabColors.Bg.red
        val pixels = IntArray(SIZE * SIZE)
        for (y in 0 until SIZE) {
            for (x in 0 until SIZE) {
                val linear = meshColor(inverse[x], inverse[y])
                var argb = 0xFF shl 24
                for (channel in 0..2) {
                    val source = linearToSrgb(linear[channel].coerceIn(0f, 1f))
                    val blended = max(0f, background + 0.5f * source - 0.5f)
                    argb = argb or ((blended * 255f).roundToInt() shl (16 - channel * 8))
                }
                pixels[y * SIZE + x] = argb
            }
        }
        return Bitmap.createBitmap(pixels, SIZE, SIZE, Bitmap.Config.ARGB_8888).asImageBitmap()
    }

    private fun inverseAxis(position: Float): Float {
        var low = 0f
        var high = 1f
        repeat(24) {
            val mid = (low + high) / 2
            if (axisPosition(mid) < position) low = mid else high = mid
        }
        return (low + high) / 2
    }

    private fun axisPosition(t: Float): Float {
        val (index, local) = segment(t)
        val i = neighbors(index)
        return catmull(axis[i[0]], axis[i[1]], axis[i[2]], axis[i[3]], local)
    }

    private fun meshColor(u: Float, v: Float): FloatArray {
        val (ix, tx) = segment(u)
        val (iy, ty) = segment(v)
        val columns = neighbors(ix)
        val rows = neighbors(iy)
        return FloatArray(3) { channel ->
            val perRow = rows.map { row ->
                val c = columns.map { column -> colors[row * 4 + column][channel] }
                catmull(c[0], c[1], c[2], c[3], tx)
            }
            catmull(perRow[0], perRow[1], perRow[2], perRow[3], ty)
        }
    }

    private fun segment(value: Float): Pair<Int, Float> {
        val scaled = value.coerceIn(0f, 1f) * 3f
        val index = min(scaled.toInt(), 2)
        return index to scaled - index
    }

    private fun neighbors(index: Int) = intArrayOf(max(index - 1, 0), index, index + 1, min(index + 2, 3))

    private fun catmull(a: Float, b: Float, c: Float, d: Float, t: Float): Float {
        val t2 = t * t
        val t3 = t2 * t
        return 0.5f * ((2 * b) + (-a + c) * t + (2 * a - 5 * b + 4 * c - d) * t2 +
                (-a + 3 * b - 3 * c + d) * t3)
    }

    private fun srgbToLinear(value: Float): Float =
        if (value <= 0.04045f) value / 12.92f else ((value + 0.055f) / 1.055f).pow(2.4f)

    private fun linearToSrgb(value: Float): Float =
        if (value <= 0.0031308f) value * 12.92f else 1.055f * value.pow(1f / 2.4f) - 0.055f
}

@Preview(widthDp = 402)
@Composable
private fun MeshGradientPreview() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(160.dp)
            .meshGradientBackground()
    )
}
