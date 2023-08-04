import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.jakewharton.mosaic.layout.padding
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.runMosaicBlocking
import com.jakewharton.mosaic.ui.*
import java.io.File
import javax.imageio.ImageIO

/**
 * resources
 * https://www.baeldung.com/kotlin/multidimentional-arrays
 * https://stackoverflow.com/questions/15777821/how-can-i-pixelate-a-jpg-with-java
 * https://stackoverflow.com/questions/60025890/how-to-pixelate-a-bufferedimage-so-it-looks-like-that
 * https://stackoverflow.com/questions/6334311/whats-the-best-way-to-round-a-color-object-to-the-nearest-color-constant
 * https://gist.github.com/JBlond/2fea43a3049b38287e5e9cefc87b2124
 */
fun main(args: Array<String>) = runMosaicBlocking{
    if (args.isEmpty()) {
        setContent {
            Column {
                Text(
                    "(◡︵◡)",
                    color = Color.Yellow,
                    style = TextStyle.Bold,
                    modifier = Modifier.padding(horizontal = 2, vertical = 1)
                )
                Text(
                    "Oops! you need to supply image path",
                    color = Color.Red,
                    style = TextStyle.Bold
                )
                Text(
                    "Basic usage:\n --url=[Image Url] --width=[Width here] --height=[Height here]",
                    color = Color.Green,
                    style = TextStyle.Bold
                )
            }
        }
        return@runMosaicBlocking
    }

    var outputWidth = 100
    var outputHeight = 100
    var inputImageUrl = ""

    for (arg in args) {
        when {
            arg.startsWith("--url=") -> inputImageUrl = arg.substringAfter("=")
            arg.startsWith("--width=") -> outputWidth = arg.substringAfter("=").toIntOrNull() ?: 100
            arg.startsWith("--height=") -> outputHeight = arg.substringAfter("=").toIntOrNull() ?: 100
        }
    }

    val outPutMatrix by mutableStateOf(PixelGrid(outputWidth, outputHeight))
    val inputFile = File(inputImageUrl)

    if (inputFile.isFile) {
        val inputImage = ImageIO.read(inputFile)
        if (inputImage == null) {
            setContent {
                Column {
                    Text(
                        "(◡︵◡)",
                        color = Color.Yellow,
                        style = TextStyle.Bold,
                        modifier = Modifier.padding(horizontal = 2, vertical = 1)
                    )
                    Text(
                        "Oops! couldn't read the image",
                        color = Color.Red,
                        style = TextStyle.Bold
                    )
                }
            }
            return@runMosaicBlocking
        }

        /**
         * finding the block size in each axis by first dividing the input image width and height
         * and setting Math.min() on it to always make sure that it doesn't go below 1
         */
        val blockSizeX = (inputImage.width / outputWidth).coerceAtLeast(1)
        val blockSizeY = (inputImage.height / outputHeight).coerceAtLeast(1)

        /**
         * loop through each pixel in the image and approximate the current pixel color to the ColorUtil.outputColors
         */
        for (x in 0 until outputWidth) {
            for (y in 0 until outputHeight) {
                val blockX = x * blockSizeX
                val blockY = y * blockSizeY
                val blockWidth = blockSizeX.coerceAtMost(inputImage.width - blockX)
                val blockHeight = blockSizeY.coerceAtMost(inputImage.height - blockY)

                // only work on a rectangle of blockX, blockY pixels on the image.
                val blockColor = ColorUtil.getDominantColor(inputImage, blockX, blockY, blockWidth, blockHeight, 5)
                var closestColorIndex = 0
                var closestColorDistance = Double.MAX_VALUE

                for (i in ColorUtil.outputColors.indices) {
                    // using the corresponding bashColors because there is no rgb values for Mosaic's colors
                    val distance = ColorUtil.getColorDistance(ColorUtil.bashColors[i], blockColor)
                    if (distance < closestColorDistance) {
                        closestColorIndex = i
                        closestColorDistance = distance
                    }
                }
                // set the final approximated color on the given pixel
                outPutMatrix.setPixel(x, y, ColorUtil.outputColors[closestColorIndex])
            }
        }
    }

    val art = "\n" +
            " .----------------.  .----------------.  .----------------.  .----------------.  .----------------.  .----------------.  .----------------.  .----------------. \n" +
            "| .--------------. || .--------------. || .--------------. || .--------------. || .--------------. || .--------------. || .--------------. || .--------------. |\n" +
            "| |   ______     | || |     _____    | || |  ___  ____   | || |    _______   | || |   _____      | || |      __      | || |  _________   | || |  _________   | |\n" +
            "| |  |_   __ \\   | || |    |_   _|   | || | |_  ||_  _|  | || |   /  ___  |  | || |  |_   _|     | || |     /  \\     | || | |  _   _  |  | || | |_   ___  |  | |\n" +
            "| |    | |__) |  | || |      | |     | || |   | |_/ /    | || |  |  (__ \\_|  | || |    | |       | || |    / /\\ \\    | || | |_/ | | \\_|  | || |   | |_  \\_|  | |\n" +
            "| |    |  ___/   | || |      | |     | || |   |  __'.    | || |   '.___`-.   | || |    | |   _   | || |   / ____ \\   | || |     | |      | || |   |  _|  _   | |\n" +
            "| |   _| |_      | || |     _| |_    | || |  _| |  \\ \\_  | || |  |`\\____) |  | || |   _| |__/ |  | || | _/ /    \\ \\_ | || |    _| |_     | || |  _| |___/ |  | |\n" +
            "| |  |_____|     | || |    |_____|   | || | |____||____| | || |  |_______.'  | || |  |________|  | || ||____|  |____|| || |   |_____|    | || | |_________|  | |\n" +
            "| |              | || |              | || |              | || |              | || |              | || |              | || |              | || |              | |\n" +
            "| '--------------' || '--------------' || '--------------' || '--------------' || '--------------' || '--------------' || '--------------' || '--------------' |\n" +
            " '----------------'  '----------------'  '----------------'  '----------------'  '----------------'  '----------------'  '----------------'  '----------------' \n"

    setContent {
        Column {
            Text(art, style = TextStyle.Bold, color = ColorUtil.outputColors.random())
            for (y in 0 until outPutMatrix.height) {
                Row {
                    for (x in 0 until outPutMatrix.width) {
                        val color = outPutMatrix.getPixel(x, y)
                        Text("  ", background = color)
                    }
                }
            }
        }
    }
}