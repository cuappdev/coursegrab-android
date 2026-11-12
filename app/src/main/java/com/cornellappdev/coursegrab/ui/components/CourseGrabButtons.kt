package com.cornellappdev.coursegrab.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.ui.theme.CourseGrabColors
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTextStyles

private val ButtonShape = RoundedCornerShape(8.dp)
private val ButtonPadding = PaddingValues(start = 18.dp, end = 14.dp, top = 12.dp, bottom = 12.dp)

@Composable
fun CourseGrabPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes trailingIcon: Int? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = ButtonShape,
        contentPadding = ButtonPadding,
        colors = ButtonDefaults.buttonColors(
            containerColor = CourseGrabColors.Black,
            contentColor = CourseGrabColors.White
        )
    ) {
        ButtonContent(trailingIcon) {
            Text(text.uppercase(), style = CourseGrabTextStyles.Label)
        }
    }
}

@Composable
fun CourseGrabOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes trailingIcon: Int? = null,
    content: @Composable RowScope.() -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = ButtonShape,
        contentPadding = ButtonPadding,
        border = BorderStroke(1.dp, CourseGrabColors.Gray),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = CourseGrabColors.White,
            contentColor = CourseGrabColors.Black
        )
    ) {
        ButtonContent(trailingIcon, content)
    }
}

@Composable
private fun RowScope.ButtonContent(
    @DrawableRes trailingIcon: Int?,
    label: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier.weight(1f),
        horizontalArrangement = if (trailingIcon == null) Arrangement.Center
        else Arrangement.SpaceBetween
    ) {
        label()
        if (trailingIcon != null) {
            Image(
                painter = painterResource(trailingIcon),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 380)
@Composable
private fun CourseGrabButtonsPreview() {
    Column(
        Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CourseGrabOutlinedButton(
                onClick = {},
                modifier = Modifier.weight(1f),
                trailingIcon = R.drawable.ic_cg_copy
            ) {
                Text("PIN: 1389", style = CourseGrabTextStyles.Label)
            }
            CourseGrabPrimaryButton(
                text = "Enroll",
                onClick = {},
                modifier = Modifier.weight(1f),
                trailingIcon = R.drawable.ic_cg_external_link
            )
        }
        CourseGrabPrimaryButton(
            text = "Stop tracking",
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}
