import com.jakewharton.mosaic.ui.Color
import java.awt.image.BufferedImage

object ColorUtil {
    val outputColors = arrayOf(
        Color.Black,
        Color.Red,
        Color.Green,
        Color.Yellow,
        Color.Blue,
        Color.Magenta,
        Color.Cyan,
        Color.White,
        Color.BrightBlack,
        Color.BrightRed,
        Color.BrightBlue,
        Color.BrightMagenta,
        Color.BrightCyan,
        Color.BrightWhite
    )

    val bashColors = arrayOf(
        java.awt.Color(0, 0, 0),  // Black
        java.awt.Color(128, 128, 128),  // Dark Gray
        java.awt.Color(255, 0, 0),  // Red
        java.awt.Color(255, 128, 128),  // Light Red
        java.awt.Color(0, 255, 0),  // Green
        java.awt.Color(128, 255, 128),  // Light Green
        java.awt.Color(128, 128, 0),  // Brown/Orange
        java.awt.Color(255, 255, 0),  // Yellow
        java.awt.Color(0, 0, 255),  // Blue
        java.awt.Color(128, 128, 255),  // Light Blue
        java.awt.Color(255, 0, 255),  // Purple
        java.awt.Color(255, 128, 255),  // Light Purple
        java.awt.Color(0, 255, 255),  // Cyan
        java.awt.Color(128, 255, 255),  // Light Cyan
        java.awt.Color(192, 192, 192),  // Light Gray
        java.awt.Color(255, 255, 255) // White
    )

    fun getDistance(c1: java.awt.Color, c2: java.awt.Color): Double {
        val rDiff = (c1.red - c2.red).toDouble()
        val gDiff = (c1.green - c2.green).toDouble()
        val bDiff = (c1.blue - c2.blue).toDouble()
        return Math.sqrt(rDiff * rDiff + gDiff * gDiff + bDiff * bDiff)
    }

    fun getColorDistance(c1: java.awt.Color, c2: java.awt.Color): Double {
        val red1 = c1.red
        val red2 = c2.red
        val rmean = red1 + red2 shr 1
        val r = red1 - red2
        val g = c1.green - c2.green
        val b = c1.blue - c2.blue
        return Math.sqrt((((512 + rmean) * r * r shr 8) + 4 * g * g + ((767 - rmean) * b * b shr 8)).toDouble())
    }

    fun getDominantColor(inputImage: BufferedImage, blockX: Int, blockY: Int, blockWidth: Int, blockHeight: Int, blockStride: Int): java.awt.Color {
        var r = 0
        var g = 0
        var b = 0
        var pixelCount = 0

        for (y in blockY until blockY + blockHeight step blockStride) {
            for (x in blockX until blockX + blockWidth step blockStride) {
                val pixelColor = java.awt.Color(inputImage.getRGB(x, y))

                r += pixelColor.red
                g += pixelColor.green
                b += pixelColor.blue
                pixelCount++
            }
        }

        val avgR = r / pixelCount
        val avgG = g / pixelCount
        val avgB = b / pixelCount

        return java.awt.Color(avgR, avgG, avgB)
    }
}