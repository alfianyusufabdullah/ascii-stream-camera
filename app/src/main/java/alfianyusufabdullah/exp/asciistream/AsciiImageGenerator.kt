package alfianyusufabdullah.exp.asciistream

import android.graphics.Bitmap
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.math.ceil
import kotlin.math.round

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
                resultSpannable.setSpan(
                    ForegroundColorSpan(Color.rgb(red, green, blue)),
                    resultSpannable.lastIndex,
                    resultSpannable.lastIndex + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            resultSpannable.append("\n")
        }

        launch {
            results(resultSpannable)
        }
    }

    private fun generateAsciiSingleCharacterFromLuminance(luminance: Double): Char //takes the grayscale value as parameter
    {
        val ascii = "08@".toCharArray()
//        val ascii = " .,:;i1tfLCG08@".toCharArray()
        ascii.reverse()
        val asciiPosition = round(luminance / (255 / ascii.lastIndex))



        return ascii[asciiPosition.toInt()]
    }
}