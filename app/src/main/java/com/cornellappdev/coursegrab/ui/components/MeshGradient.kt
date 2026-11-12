package com.cornellappdev.coursegrab.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.cornellappdev.coursegrab.R
import kotlin.math.roundToInt

fun Modifier.meshGradientBackground(): Modifier = this then MeshGradientElement

private data object MeshGradientElement : ModifierNodeElement<MeshGradientNode>() {
    override fun create() = MeshGradientNode()
    override fun update(node: MeshGradientNode) = Unit
    override fun InspectorInfo.inspectableProperties() {
        name = "meshGradientBackground"
    }
}

private class MeshGradientNode :
    Modifier.Node(),
    DrawModifierNode,
    CompositionLocalConsumerModifierNode {

    private var image: ImageBitmap? = null

    override fun ContentDrawScope.draw() {
        val image = image ?: ImageBitmap.imageResource(
            currentValueOf(LocalResources),
            R.drawable.bg_mesh_gradient
        ).also { image = it }
        drawImage(
            image = image,
            dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt()),
            filterQuality = FilterQuality.Low
        )
        drawContent()
    }
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
