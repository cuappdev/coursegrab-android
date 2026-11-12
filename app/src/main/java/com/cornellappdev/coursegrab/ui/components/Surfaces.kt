package com.cornellappdev.coursegrab.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.cornellappdev.coursegrab.ui.theme.CourseGrabColors
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTextStyles

val CardShape = RoundedCornerShape(10.dp)
val PillShape = RoundedCornerShape(100.dp)

fun Modifier.courseGrabShadow(
    shape: Shape,
    blur: Dp = 5.dp,
    offsetY: Dp = 2.dp,
    alpha: Float = 0.1f
): Modifier =
    dropShadow(
        shape = shape,
        shadow = Shadow(
            radius = blur,
            color = Color.Black.copy(alpha = alpha),
            offset = DpOffset(0.dp, offsetY)
        )
    )

fun Modifier.outlinedSurface(
    shape: Shape,
    borderWidth: Dp = 0.5.dp,
    background: Color = CourseGrabColors.White
): Modifier = this
    .courseGrabShadow(shape)
    .clip(shape)
    .background(background)
    .border(borderWidth, CourseGrabColors.Gray, shape)

@Composable
fun CourseGrabCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .outlinedSurface(CardShape)
            .padding(16.dp),
        content = content
    )
}

@Preview(showBackground = true, widthDp = 380)
@Composable
private fun CourseGrabCardPreview() {
    Column(Modifier.padding(16.dp)) {
        CourseGrabCard {
            Text("ANTHR 1400", style = CourseGrabTextStyles.H3)
            Spacer(Modifier.height(8.dp))
            Text(
                "Introduction to Sociocultural Anthropology",
                style = CourseGrabTextStyles.H3
            )
        }
    }
}
