package com.cornellappdev.coursegrab.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.ui.theme.CourseGrabColors

private val HeaderShape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
private val HeaderTitle = TextStyle(fontWeight = FontWeight.Medium, fontSize = 24.sp)

@Composable
fun CourseGrabHeader(
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(HeaderShape)
            .meshGradientBackground()
            .statusBarsPadding()
            .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_cg_logo_bell),
                contentDescription = null,
                modifier = Modifier.size(width = 25.dp, height = 24.dp)
            )
            Text(
                text = stringResource(R.string.app_name),
                style = HeaderTitle,
                color = CourseGrabColors.White,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .weight(1f)
            )
            IconButton(onClick = onOpenSettings, modifier = Modifier.size(24.dp)) {
                Image(
                    painter = painterResource(R.drawable.ic_cg_gear),
                    contentDescription = stringResource(R.string.settings)
                )
            }
        }
        content()
    }
}

@Preview(widthDp = 402)
@Composable
private fun CourseGrabHeaderPreview() {
    CourseGrabHeader(onOpenSettings = {}) {
        SearchFieldButton(onClick = {})
    }
}
