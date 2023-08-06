import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jakewharton.mosaic.layout.padding
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.runMosaicBlocking
import com.jakewharton.mosaic.ui.*
import kotlinx.coroutines.delay
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO


/**
 * resources
 * https://www.baeldung.com/kotlin/multidimentional-arrays
 * https://stackoverflow.com/questions/15777821/how-can-i-pixelate-a-jpg-with-java
 * https://stackoverflow.com/questions/60025890/how-to-pixelate-a-bufferedimage-so-it-looks-like-that
 * https://stackoverflow.com/questions/6334311/whats-the-best-way-to-round-a-color-object-to-the-nearest-color-constant
 * https://gist.github.com/JBlond/2fea43a3049b38287e5e9cefc87b2124
 */
fun main(args: Array<String>) = runMosaicBlocking {
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

    var outPutMatrix by mutableStateOf(PixelGrid(outputWidth, outputHeight))
    val outPutMatrixArray: MutableList<PixelGrid> by mutableStateOf(mutableListOf())
    val inputFile = File(inputImageUrl)

    if (inputFile.isFile) {
        if (inputFile.extension != "mp4" && inputFile.extension != "avi") {
            val inputImage = ImageIO.read(inputFile)
            checkIfNull(inputImage)
            outPutMatrix = pixelate(inputImage, outputWidth, outputHeight)
        } else {
            val outputDirectory = "./frames/"
            try {
                // Create the frames directory
                val mkdirProcessBuilder = ProcessBuilder("mkdir", "-p", "frames")
                val mkdirProcess = mkdirProcessBuilder.start()
                val mkdirExitCode = mkdirProcess.waitFor()
                if (mkdirExitCode == 0) {
                    val ffmpegProcessBuilder = ProcessBuilder(
                        "ffmpeg", "-i", inputImageUrl, outputDirectory + "output_%03d.png"
                    )
                    val ffmpegProcess = ffmpegProcessBuilder.start()
                    val ffmpegExitCode = ffmpegProcess.waitFor()
                    if (ffmpegExitCode == 0) {
                        val folder = File(outputDirectory)
                        // resource hog here :(
                        val files = folder.listFiles()
                        files?.sortBy { it.name }

                        // Null safety? probably would crash here if files is null
                        for (file in files!!) {
                            if (file.isFile && file.extension == "png") {
                                val inputFrame = ImageIO.read(file)
                                setContent {
                                    Text("Pixelating fame... ${file.name}")
                                }
                                checkIfNull(inputFrame)
                                outPutMatrixArray.add(pixelate(inputFrame, outputWidth, outputHeight))
                            }
                        }
                    } else {
                        System.err.println("Error executing ffmpeg command. Exit code: $ffmpegExitCode")
                    }
                } else {
                    System.err.println("Error creating directory. Exit code: $mkdirExitCode")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
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

    if (inputFile.extension != "mp4" && inputFile.extension != "avi") {
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
    } else {
        //start audio just about when frames start to render
        //see more at: https://askubuntu.com/questions/115369/how-to-play-mp3-files-from-the-command-line
        val playAudioCommand = "ffplay -loglevel panic -nodisp -autoexit -vn $inputImageUrl"

        val processBuilder = ProcessBuilder()
        processBuilder.command("bash", "-c", playAudioCommand)
        processBuilder.start()
        for (currentMatrix in outPutMatrixArray) {
            setContent {
                Column {
                    for (y in 0 until currentMatrix.height) {
                        Row {
                            for (x in 0 until currentMatrix.width) {
                                val color = currentMatrix.getPixel(x, y)
                                Text("  ", background = color)
                            }
                        }
                    }
                }
            }
            delay(5)
        }
    }
}

fun checkIfNull(inputBuffer: BufferedImage) = runMosaicBlocking {
    if (inputBuffer == null) {
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
}

fun pixelate(inputImage: BufferedImage, outputWidth: Int, outputHeight: Int): PixelGrid {
    val outPutMatrix = PixelGrid(outputWidth, outputHeight)
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

    return outPutMatrix
}