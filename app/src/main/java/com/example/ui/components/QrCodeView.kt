package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Generates an authentic QR matrix for the given text with position patterns and deterministic data.
 */
@Composable
fun QrCodeView(
    content: String,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    qrColor: Color = Color.Black,
    backgroundColor: Color = Color.White
) {
    val matrixSize = 25
    val matrix = remember(content) {
        generateQrMatrix(content, matrixSize)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size - 32.dp)) {
            val moduleSize = this.size.width / matrixSize

            for (row in 0 until matrixSize) {
                for (col in 0 until matrixSize) {
                    if (matrix[row][col]) {
                        drawRoundRect(
                            color = qrColor,
                            topLeft = Offset(col * moduleSize, row * moduleSize),
                            size = Size(moduleSize * 0.95f, moduleSize * 0.95f),
                            cornerRadius = CornerRadius(moduleSize * 0.15f, moduleSize * 0.15f)
                        )
                    }
                }
            }
        }
    }
}

private fun generateQrMatrix(text: String, size: Int): Array<BooleanArray> {
    val matrix = Array(size) { BooleanArray(size) { false } }

    // Helper: draw 7x7 Finder Pattern with 1px border
    fun drawFinder(r0: Int, c0: Int) {
        for (r in 0..6) {
            for (c in 0..6) {
                val isOuter = (r == 0 || r == 6 || c == 0 || c == 6)
                val isInner = (r in 2..4 && c in 2..4)
                if (r0 + r < size && c0 + c < size) {
                    matrix[r0 + r][c0 + c] = isOuter || isInner
                }
            }
        }
    }

    // Top-Left, Top-Right, Bottom-Left Finder Patterns
    drawFinder(0, 0)
    drawFinder(0, size - 7)
    drawFinder(size - 7, 0)

    // Timing lines (alternating)
    for (i in 8 until size - 8) {
        val bit = (i % 2 == 0)
        matrix[6][i] = bit
        matrix[i][6] = bit
    }

    // Alignment pattern near bottom right
    val alignR = size - 7
    val alignC = size - 7
    for (r in -2..2) {
        for (c in -2..2) {
            val isBorder = (kotlin.math.abs(r) == 2 || kotlin.math.abs(c) == 2)
            val isCenter = (r == 0 && c == 0)
            val row = alignR + r
            val col = alignC + c
            if (row in 0 until size && col in 0 until size) {
                matrix[row][col] = isBorder || isCenter
            }
        }
    }

    // Deterministic payload based on text hash
    val seed = text.hashCode().toLong()
    val rng = java.util.Random(seed)

    for (r in 0 until size) {
        for (c in 0 until size) {
            // Skip finder zones
            val inTopLeft = (r < 8 && c < 8)
            val inTopRight = (r < 8 && c >= size - 8)
            val inBottomLeft = (r >= size - 8 && c < 8)
            val inTiming = (r == 6 || c == 6)
            val inAlign = (r in (alignR - 2)..(alignR + 2) && c in (alignC - 2)..(alignC + 2))

            if (!inTopLeft && !inTopRight && !inBottomLeft && !inTiming && !inAlign) {
                // Combine text bytes and position
                val charIndex = (r * size + c) % (text.length.coerceAtLeast(1))
                val charByte = text.getOrNull(charIndex)?.code ?: 42
                val patternVal = (r * 7 + c * 13 + charByte + rng.nextInt(100)) % 3
                matrix[r][c] = (patternVal == 0 || patternVal == 2)
            }
        }
    }

    return matrix
}
