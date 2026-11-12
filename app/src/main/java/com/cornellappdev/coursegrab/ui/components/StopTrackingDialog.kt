package com.cornellappdev.coursegrab.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.ui.theme.CourseGrabColors
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTextStyles

private val DialogShape = RoundedCornerShape(14.dp)
private val PromptStyle = TextStyle(fontWeight = FontWeight.Normal, fontSize = 18.sp, lineHeight = 24.sp)
private const val SCRIM_ALPHA = 0.25f

@Composable
fun StopTrackingDialog(
    courseCode: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val window = (LocalView.current.parent as? DialogWindowProvider)?.window
        SideEffect { window?.setDimAmount(SCRIM_ALPHA) }
        StopTrackingCard(courseCode, onConfirm, onDismiss)
    }
}

@Composable
private fun StopTrackingCard(courseCode: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .width(300.dp)
            .courseGrabShadow(DialogShape, blur = 7.5.dp, offsetY = 4.dp, alpha = 0.25f)
            .clip(DialogShape)
            .background(CourseGrabColors.White)
            .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_cg_illustration_tracking),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        Text(
            text = stringResource(R.string.stop_tracking_prompt, courseCode),
            style = PromptStyle,
            color = CourseGrabColors.Black,
            textAlign = TextAlign.Center
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CourseGrabPrimaryButton(
                text = stringResource(R.string.stop_tracking),
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(43.dp)
            )
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(43.dp)
            ) {
                Text(
                    text = stringResource(R.string.cancel).uppercase(),
                    style = CourseGrabTextStyles.Label,
                    color = Color(0xFF8D8D8D)
                )
            }
        }
    }
}

@Preview
@Composable
private fun StopTrackingCardPreview() {
    StopTrackingCard(courseCode = "HD 4790", onConfirm = {}, onDismiss = {})
}
