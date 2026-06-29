package com.akashicsoft.crm.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

    @Composable
    fun ShimmerBox(
        modifier: Modifier,
        cornerRadius: Dp = 8.dp
    ) {
        val transition = rememberInfiniteTransition()
        val translate by transition.animateFloat(
            initialValue = -450f,
            targetValue = 950f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200),
                repeatMode = RepeatMode.Restart
            )
        )
        val base = MaterialTheme.colors.onSurface.copy(alpha = 0.07f)
        val highlight = MaterialTheme.colors.onSurface.copy(alpha = 0.13f)
        val brush = Brush.linearGradient(
            colors = listOf(base, highlight, base),
            start = Offset(translate, translate),
            end = Offset(translate + 320f, translate + 320f)
        )

        Box(
            modifier = modifier
                .clip(RoundedCornerShape(cornerRadius))
                .background(brush)
        )
    }

@Composable
fun LeadListSkeleton(
    modifier: Modifier = Modifier,
    rows: Int = 7
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(rows) {
            LeadCardSkeleton()
        }
    }
}
@Composable
private fun LeadCardSkeleton() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ShimmerBox(
                    Modifier.height(18.dp).weight(1f)
                )
                Spacer(Modifier.width(12.dp))
                ShimmerBox(
                    Modifier.size(
                        width = 72.dp,
                        height = 24.dp
                    ), cornerRadius = 12.dp
                )
            }
            ShimmerBox(
                Modifier.fillMaxWidth(0.86f).height(14.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ShimmerBox(
                    Modifier.size(
                        width = 84.dp,
                        height = 24.dp
                    ), cornerRadius = 12.dp
                )
                ShimmerBox(
                    Modifier.size(
                        width = 92.dp,
                        height = 24.dp
                    ), cornerRadius = 12.dp
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                ShimmerBox(
                    Modifier.size(28.dp),
                    cornerRadius = 14.dp
                )
                Spacer(Modifier.width(8.dp))
                ShimmerBox(
                    Modifier.height(12.dp).weight(1f)
                )
            }
        }
    }
}