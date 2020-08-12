package alfianyusufabdullah.exp.asciistream

import android.graphics.Bitmap
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class AsciiImageGenerator(private val image: Bitmap) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Main

    fun run(results: (Spannable) -> Unit) {
        var singleCharsAscii: Char
        val resultSpannable = SpannableStringBuilder()
        val imageHeight = image.height
        val imageWidth = image.width

        repeat(imageHeight) { imageY ->
            repeat(imageWidth) { imageX ->

                val imagePixel = image.getPixel(imageX, imageY)
                val red = Color.red(imagePixel)
                val green = Color.green(imagePixel)
                val blue = Color.blue(imagePixel)

                val luminanceValue = 0.2126 * red + 0.7152 * green + 0.0722 * blue
                singleCharsAscii = generateAsciiSingleCharacterFromLuminance(luminanceValue)
                resultSpannable.append(singleCharsAscii)
            }

            resultSpannable.append("\n")
        }

        launch {
            results(resultSpannable)
        }
    }

    private fun generateAsciiSingleCharacterFromLuminance(luminance: Double): Char //takes the grayscale value as parameter
    {
        return when {
            luminance >= 230.0 -> ' '
            luminance >= 200.0 -> '.'
            luminance >= 130.0 -> ':'
            luminance >= 100.0 -> 'o'
            luminance >= 80.0 -> 'O'
            luminance >= 60.0 -> '&'
            luminance >= 70.0 -> '8'
            luminance >= 50.0 -> '#'
            else -> '@'
        }
    }
}