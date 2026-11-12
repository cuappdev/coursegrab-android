package com.cornellappdev.coursegrab.ui.login

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.ui.theme.CourseGrabColors

private const val RING_DURATION_MS = 2000

@Composable
fun HeroBell(ring: Boolean, modifier: Modifier = Modifier) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(ring) {
        if (ring) progress.animateTo(1f, tween(RING_DURATION_MS, easing = LinearEasing))
    }

    Box(
        modifier = modifier
            .size(width = 243.dp, height = 236.dp)
            .clipToBounds()
            .graphicsLayer { rotationZ = BellRotation.valueAt(progress.value) }
    ) {
        Box(
            Modifier
                .offset {
                    IntOffset(
                        (92.dp + ClapperX.valueAt(progress.value).dp).roundToPx(),
                        (175.dp + ClapperY.valueAt(progress.value).dp).roundToPx()
                    )
                }
                .size(53.dp)
                .background(CourseGrabColors.Green, CircleShape)
        )
        Image(
            painter = painterResource(R.drawable.ic_cg_hero_bell),
            contentDescription = null
        )
    }
}

private class Keyframes(vararg frames: Pair<Float, Float>, private val easedUntil: Float) {
    private val frames = frames.toList()

    fun valueAt(fraction: Float): Float {
        val end = frames.indexOfFirst { it.first >= fraction }
        if (end <= 0) return frames.first().second
        val (startAt, from) = frames[end - 1]
        val (endAt, to) = frames[end]
        val local = (fraction - startAt) / (endAt - startAt)
        val eased = if (startAt < easedUntil) EaseInOut.transform(local) else local
        return from + (to - from) * eased
    }
}

// Figma keyframes for the 2s ring. Segments starting before `easedUntil` use ease-in-out.
private val BellRotation = Keyframes(
    0f to 0f, 0.07f to -22f, 0.16f to 17f, 0.25f to -19f, 0.34f to 13f, 0.43f to -12f,
    0.52f to 7f, 0.61f to -5f, 0.70f to 2f, 0.79f to 0f, 1f to 0f,
    easedUntil = 0.79f
)

private val ClapperX = Keyframes(
    0f to 0f, 0.09f to -18f, 0.18f to 16f, 0.27f to -15f, 0.36f to 12f, 0.45f to -10f,
    0.54f to 7f, 0.63f to -4f, 0.72f to 2f, 0.83f to 0f, 1f to 0f,
    easedUntil = 0.83f
)

private val ClapperY = Keyframes(
    0f to 0f, 0.09f to -6.804f, 0.18f to 2.784f, 0.27f to -5.4f, 0.36f to 2.376f,
    0.45f to -3.3f, 0.54f to 1.596f, 0.63f to -1.176f, 0.72f to 0.516f, 0.83f to 0f, 1f to 0f,
    easedUntil = 0.83f
)

@Preview
@Composable
private fun HeroBellPreview() {
    HeroBell(ring = false)
}
