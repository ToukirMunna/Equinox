package com.toukir.equinox.ui.components

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
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

enum class BreathingPhase(val phaseIndex: Int, val durationSeconds: Int) {
    INHALE(0, 4),
    HOLD_IN(1, 4),
    EXHALE(2, 4),
    HOLD_OUT(3, 4)
}

@Composable
fun BreathingPacer(
    modifier: Modifier = Modifier
) {
    var isRunning by remember { mutableStateOf(true) }
    var currentPhase by remember { mutableStateOf(BreathingPhase.INHALE) }
    var secondsLeft by remember { mutableIntStateOf(4) }
    var completedCycles by remember { mutableIntStateOf(1) }
    val haptic = LocalHapticFeedback.current

    // Synchronized phase countdown & cycle progression loop
    LaunchedEffect(isRunning) {
        if (!isRunning) return@LaunchedEffect
        while (isRunning) {
            // Phase 1: Inhale
            currentPhase = BreathingPhase.INHALE
            try { haptic.performHapticFeedback(HapticFeedbackType.LongPress) } catch (e: Exception) {}
            for (i in 4 downTo 1) {
                secondsLeft = i
                delay(1000)
            }

            // Phase 2: Hold In
            currentPhase = BreathingPhase.HOLD_IN
            try { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) } catch (e: Exception) {}
            for (i in 4 downTo 1) {
                secondsLeft = i
                delay(1000)
            }

            // Phase 3: Exhale
            currentPhase = BreathingPhase.EXHALE
            try { haptic.performHapticFeedback(HapticFeedbackType.LongPress) } catch (e: Exception) {}
            for (i in 4 downTo 1) {
                secondsLeft = i
                delay(1000)
            }

            // Phase 4: Hold Out / Rest
            currentPhase = BreathingPhase.HOLD_OUT
            try { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) } catch (e: Exception) {}
            for (i in 4 downTo 1) {
                secondsLeft = i
                delay(1000)
            }

            completedCycles++
        }
    }

    // Target scale calculation: Inhale/Hold = 1.15f, Exhale/Rest = 0.78f
    val targetScale = when (currentPhase) {
        BreathingPhase.INHALE, BreathingPhase.HOLD_IN -> 1.15f
        BreathingPhase.EXHALE, BreathingPhase.HOLD_OUT -> 0.78f
    }

    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(
            durationMillis = 4000,
            easing = CubicBezierEasing(0.42f, 0.0f, 0.58f, 1.0f)
        ),
        label = "organic_breathing_scale"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cycle Counter Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = stringResource(R.string.urge_breath_cycle_count, completedCycles),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Main Box Breathing Frame with Canvas perimeter + Organic Core
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            // Box Border Canvas: Illuminates active side of the box (Inhale Top -> Hold Right -> Exhale Bottom -> Rest Left)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 3.dp.toPx()
                val cornerRadius = 28.dp.toPx()
                val pad = 10.dp.toPx()
                val boxWidth = size.width - (pad * 2)
                val boxHeight = size.height - (pad * 2)

                // Background track
                drawRoundRect(
                    color = surfaceVariantColor.copy(alpha = 0.6f),
                    topLeft = Offset(pad, pad),
                    size = Size(boxWidth, boxHeight),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Highlighted active edge
                val activeEdgeColor = primaryColor
                when (currentPhase) {
                    BreathingPhase.INHALE -> {
                        // Top side (Left to Right)
                        drawLine(
                            color = activeEdgeColor,
                            start = Offset(pad + cornerRadius, pad),
                            end = Offset(pad + boxWidth - cornerRadius, pad),
                            strokeWidth = strokeWidth * 2f,
                            cap = StrokeCap.Round
                        )
                    }
                    BreathingPhase.HOLD_IN -> {
                        // Right side (Top to Bottom)
                        drawLine(
                            color = activeEdgeColor,
                            start = Offset(pad + boxWidth, pad + cornerRadius),
                            end = Offset(pad + boxWidth, pad + boxHeight - cornerRadius),
                            strokeWidth = strokeWidth * 2f,
                            cap = StrokeCap.Round
                        )
                    }
                    BreathingPhase.EXHALE -> {
                        // Bottom side (Right to Left)
                        drawLine(
                            color = activeEdgeColor,
                            start = Offset(pad + boxWidth - cornerRadius, pad + boxHeight),
                            end = Offset(pad + cornerRadius, pad + boxHeight),
                            strokeWidth = strokeWidth * 2f,
                            cap = StrokeCap.Round
                        )
                    }
                    BreathingPhase.HOLD_OUT -> {
                        // Left side (Bottom to Top)
                        drawLine(
                            color = activeEdgeColor,
                            start = Offset(pad, pad + boxHeight - cornerRadius),
                            end = Offset(pad, pad + cornerRadius),
                            strokeWidth = strokeWidth * 2f,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            // Expanding & Contracting Inner Aura Sphere
            Box(
                modifier = Modifier
                    .size(175.dp)
                    .scale(animatedScale)
                    .clip(CircleShape)
                    .background(ColorVictory.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(135.dp)
                        .clip(CircleShape)
                        .background(ColorVictory.copy(alpha = 0.22f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Phase Name
                            Text(
                                text = when (currentPhase) {
                                    BreathingPhase.INHALE -> stringResource(R.string.urge_breath_inhale)
                                    BreathingPhase.HOLD_IN -> stringResource(R.string.urge_breath_hold_in)
                                    BreathingPhase.EXHALE -> stringResource(R.string.urge_breath_exhale)
                                    BreathingPhase.HOLD_OUT -> stringResource(R.string.urge_breath_hold_out)
                                },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )

                            // Live seconds countdown
                            Text(
                                text = stringResource(R.string.urge_breath_seconds_left, secondsLeft),
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Phase Guidance Subtext
        Text(
            text = when (currentPhase) {
                BreathingPhase.INHALE -> stringResource(R.string.urge_breath_inhale_hint)
                BreathingPhase.HOLD_IN -> stringResource(R.string.urge_breath_hold_in_hint)
                BreathingPhase.EXHALE -> stringResource(R.string.urge_breath_exhale_hint)
                BreathingPhase.HOLD_OUT -> stringResource(R.string.urge_breath_hold_out_hint)
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4-Phase Step Capsules
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            PhaseCapsule(
                label = stringResource(R.string.urge_breath_phase_1),
                isActive = currentPhase == BreathingPhase.INHALE,
                modifier = Modifier.weight(1f)
            )
            PhaseCapsule(
                label = stringResource(R.string.urge_breath_phase_2),
                isActive = currentPhase == BreathingPhase.HOLD_IN,
                modifier = Modifier.weight(1f)
            )
            PhaseCapsule(
                label = stringResource(R.string.urge_breath_phase_3),
                isActive = currentPhase == BreathingPhase.EXHALE,
                modifier = Modifier.weight(1f)
            )
            PhaseCapsule(
                label = stringResource(R.string.urge_breath_phase_4),
                isActive = currentPhase == BreathingPhase.HOLD_OUT,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Pause / Resume Button
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
    }
}

@Composable
private fun PhaseCapsule(
    label: String,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isActive) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium
            ),
            color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
