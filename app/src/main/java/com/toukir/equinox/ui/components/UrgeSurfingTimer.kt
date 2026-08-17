package com.toukir.equinox.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Surfing
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toukir.equinox.R
import com.toukir.equinox.ui.theme.ColorVictory
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
fun UrgeSurfingTimer(
    modifier: Modifier = Modifier
) {
    val totalSeconds = 900 // 15 minutes
    var secondsElapsed by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(true) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(isRunning) {
        if (!isRunning) return@LaunchedEffect
        while (isRunning && secondsElapsed < totalSeconds) {
            delay(1000)
            secondsElapsed++
            if (secondsElapsed == 180 || secondsElapsed == 420 || secondsElapsed == 720 || secondsElapsed == 900) {
                try { haptic.performHapticFeedback(HapticFeedbackType.LongPress) } catch (e: Exception) {}
            }
        }
    }

    val remainingSeconds = (totalSeconds - secondsElapsed).coerceAtLeast(0)
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val progress = (secondsElapsed.toFloat() / totalSeconds).coerceIn(0f, 1f)

    val (milestoneTextRes, milestoneColor) = when {
        secondsElapsed < 180 -> Pair(R.string.urge_surfing_milestone_1, MaterialTheme.colorScheme.primary)
        secondsElapsed < 420 -> Pair(R.string.urge_surfing_milestone_2, MaterialTheme.colorScheme.tertiary)
        secondsElapsed < 720 -> Pair(R.string.urge_surfing_milestone_3, ColorVictory)
        else -> Pair(R.string.urge_surfing_milestone_4, ColorVictory)
    }

    val waveTransition = rememberInfiniteTransition(label = "wave_anim")
    val waveOffset by waveTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_offset"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Description
        Text(
            text = stringResource(R.string.urge_surfing_desc),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Visual Wave Craving Curve Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
            contentAlignment = Alignment.Center
        ) {
            val primaryColor = MaterialTheme.colorScheme.primary
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val midY = h / 2f

                // Draw Craving Natural Bell Wave (Peaks at 25% width, then decays)
                val path = Path()
                val steps = 80
                for (i in 0..steps) {
                    val x = (i.toFloat() / steps) * w
                    val normalizedX = (i.toFloat() / steps)
                    // Bell curve envelope peaking at x=0.25
                    val envelope = kotlin.math.exp(-((normalizedX - 0.25) * (normalizedX - 0.25)) / 0.05).toFloat()
                    val waveY = midY - (envelope * 36f) + (sin(normalizedX * 12f + waveOffset) * 6f)
                    if (i == 0) path.moveTo(x, waveY) else path.lineTo(x, waveY)
                }

                drawPath(
                    path = path,
                    color = primaryColor.copy(alpha = 0.7f),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Current time position beacon along the wave
                val currentX = progress * w
                val currentNormX = progress
                val currentEnvelope = kotlin.math.exp(-((currentNormX - 0.25) * (currentNormX - 0.25)) / 0.05).toFloat()
                val currentY = midY - (currentEnvelope * 36f) + (sin(currentNormX * 12f + waveOffset) * 6f)

                drawCircle(
                    color = primaryColor,
                    radius = 7.dp.toPx(),
                    center = Offset(currentX, currentY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 3.dp.toPx(),
                    center = Offset(currentX, currentY)
                )
            }

            // Big Live Countdown in the center
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Progress Bar
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Dynamic Milestone Prompt
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(milestoneColor.copy(alpha = 0.12f))
                .padding(horizontal = 14.dp, vertical = 7.dp)
        ) {
            Text(
                text = stringResource(milestoneTextRes),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                fontWeight = FontWeight.Bold,
                color = milestoneColor,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Controls (Play/Pause & Reset)
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalButton(
                onClick = { isRunning = !isRunning },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isRunning) stringResource(R.string.urge_breath_pause) else stringResource(R.string.urge_breath_resume),
                    style = MaterialTheme.typography.labelMedium
                )
            }

            IconButton(
                onClick = {
                    secondsElapsed = 0
                    isRunning = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.RestartAlt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
