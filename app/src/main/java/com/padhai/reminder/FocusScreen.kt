package com.padhai.reminder

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// Same berry colours as the app theme (light + dark)
private class RingColors(
    val bg: Color, val track: Color, val primary: Color, val onPrimary: Color,
    val text: Color, val muted: Color
)
private val LightRing = RingColors(
    bg = Color(0xFFFFF6FA), track = Color(0xFFE3C9DB), primary = Color(0xFFD6006F),
    onPrimary = Color(0xFFFFFFFF), text = Color(0xFF2B1235), muted = Color(0xFF7A6285)
)
private val DarkRing = RingColors(
    bg = Color(0xFF120818), track = Color(0xFF3A2250), primary = Color(0xFFFF3D9A),
    onPrimary = Color(0xFF1A0010), text = Color(0xFFF7EAFF), muted = Color(0xFFB79CC9)
)

private fun formatTime(totalSeconds: Int): String {
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
}

@Composable
fun FocusScreen(onClose: () -> Unit) {
    val ctx = LocalContext.current
    val view = LocalView.current
    val c = if (isSystemInDarkTheme()) DarkRing else LightRing

    // endAt = 0 means idle; otherwise wall-clock end time (survives closing the screen)
    var endAt by remember { mutableLongStateOf(Prefs.focusUntil(ctx)) }
    var sessionMin by remember {
        mutableIntStateOf(if (Prefs.focusUntil(ctx) > 0L) Prefs.sessionMin(ctx) else Prefs.focusMin(ctx))
    }
    val totalMs = sessionMin * 60_000L
    var leftMs by remember {
        mutableLongStateOf(
            if (endAt > 0L) (endAt - System.currentTimeMillis()).coerceIn(0L, totalMs) else totalMs
        )
    }
    var finished by remember { mutableStateOf(false) }
    val running = endAt > 0L

    LaunchedEffect(endAt) {
        while (endAt > 0L) {
            val left = endAt - System.currentTimeMillis()
            if (left <= 0L) {
                leftMs = 0L
                Prefs.addMinutes(ctx, sessionMin)
                Prefs.setFocusUntil(ctx, 0L)
                Notifier.show(
                    ctx,
                    "Great job! $sessionMin-minute focus session done. Take a short break, then come back.",
                    "🎉 Focus complete"
                )
                finished = true
                endAt = 0L
                break
            }
            leftMs = left
            delay(50L)   // smooth ring shrink
        }
    }
    LaunchedEffect(running) { view.keepScreenOn = running }

    fun start() {
        val m = Prefs.focusMin(ctx)                  // always the latest length from the main screen
        sessionMin = m
        Prefs.setSessionMin(ctx, m)
        val end = System.currentTimeMillis() + m * 60_000L
        Prefs.setFocusUntil(ctx, end)
        finished = false
        leftMs = m * 60_000L
        endAt = end
    }
    fun stop() {
        Prefs.setFocusUntil(ctx, 0L)
        sessionMin = Prefs.focusMin(ctx)
        endAt = 0L
        leftMs = sessionMin * 60_000L
        finished = false
    }

    val fraction = if (finished) 0f else (leftMs.toFloat() / totalMs).coerceIn(0f, 1f)
    val secs = ((leftMs + 999L) / 1000L).toInt()
    val caption = when { finished -> "DONE"; running -> "REMAINING"; else -> "READY" }

    MaterialTheme {
        Box(Modifier.fillMaxSize().background(c.bg)) {

            // top bar
            Row(
                Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onClose) { Text("‹ Back", color = c.muted) }
            }

            // ring + time
            Box(Modifier.align(Alignment.Center), contentAlignment = Alignment.Center) {
                Canvas(Modifier.size(280.dp)) {
                    val stroke = 14.dp.toPx()
                    val d = size.minDimension - stroke
                    val topLeft = Offset(stroke / 2f, stroke / 2f)
                    val arcSize = Size(d, d)
                    drawArc(
                        color = c.track, startAngle = 0f, sweepAngle = 360f, useCenter = false,
                        topLeft = topLeft, size = arcSize, style = Stroke(width = stroke)
                    )
                    if (fraction > 0.002f) {
                        drawArc(
                            color = c.primary, startAngle = -90f, sweepAngle = 360f * fraction,
                            useCenter = false, topLeft = topLeft, size = arcSize,
                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        formatTime(secs), color = c.text, fontSize = 52.sp,
                        fontWeight = FontWeight.Light, letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(caption, color = c.muted, fontSize = 12.sp, letterSpacing = 3.sp)
                }
            }

            // start / stop
            Button(
                onClick = { if (running) stop() else start() },
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                    .navigationBarsPadding().padding(horizontal = 24.dp, vertical = 32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = c.primary, contentColor = c.onPrimary)
            ) { Text(if (running) "Stop" else "Start") }
        }
    }
}
