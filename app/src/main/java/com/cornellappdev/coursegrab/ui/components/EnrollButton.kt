package com.cornellappdev.coursegrab.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTheme
import java.util.Locale

@Composable
fun EnrollButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(3.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = EnrollBackground,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
    ) {
        Text(
            text = stringResource(R.string.enroll_button).uppercase(Locale.ROOT),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

private val EnrollBackground = Color(0xFF000000)

@Preview(showBackground = true)
@Composable
private fun EnrollButtonPreview() {
    CourseGrabTheme { EnrollButton(onClick = {}) }
}
