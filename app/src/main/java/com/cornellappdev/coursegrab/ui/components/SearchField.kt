package com.cornellappdev.coursegrab.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.ui.theme.CourseGrabColors
import com.cornellappdev.coursegrab.ui.theme.CourseGrabTextStyles

fun Modifier.searchFieldSurface(): Modifier = this
    .courseGrabShadow(PillShape, blur = 2.dp)
    .clip(PillShape)
    .background(CourseGrabColors.LightGray)
    .padding(horizontal = 16.dp, vertical = 12.dp)

@Composable
fun SearchFieldRow(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = modifier.searchFieldSurface(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.ic_cg_search_field),
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        content()
    }
}

@Composable
fun SearchFieldButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    SearchFieldRow(
        modifier = modifier
            .fillMaxWidth()
            .clip(PillShape)
            .clickable(role = Role.Button, onClick = onClick)
    ) {
        Text(
            text = stringResource(R.string.search_for_a_course),
            style = CourseGrabTextStyles.H4,
            color = CourseGrabColors.DarkGray
        )
    }
}

@Preview(showBackground = true, widthDp = 380)
@Composable
private fun SearchFieldButtonPreview() {
    SearchFieldButton(onClick = {}, modifier = Modifier.padding(16.dp))
}
