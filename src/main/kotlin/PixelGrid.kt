import com.jakewharton.mosaic.ui.Color

/**
 * a simple data class that holds coordinate and color values for the alleged image.
 */
class PixelGrid(val width: Int, val height: Int) {
    private val pixels: Array<Array<Color>> = Array(height) { Array(width) { Color.Black } }

    fun setPixel(x: Int, y: Int, color: Color) {
        pixels[y][x] = color
    }

    fun getPixel(x: Int, y: Int): Color {
        return pixels[y][x]
    }

    fun getImage(): Array<Array<Color>> {
        return pixels
    }

    fun printArray() {
        for (y in pixels.indices) {
            for (x in pixels.indices) {
                val color = pixels[x][y]
                print(color)
            }
            println(",")
        }
    }
}