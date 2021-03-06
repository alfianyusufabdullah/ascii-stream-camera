package alfianyusufabdullah.exp.asciistream

import android.annotation.SuppressLint
import android.graphics.*
import android.media.Image
import android.text.Spannable
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream

class AsciiImageAnalyzer(private val listener: (Spannable) -> Unit) : ImageAnalysis.Analyzer {

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(image: ImageProxy) {
        val raw = image.image?.toBitmap()?.rotateAndFlipBitmap(-90f)
        val convert = Bitmap.createScaledBitmap(raw as Bitmap, 80, 80, true)
        AsciiImageGenerator(convert).run {
            listener(it)
        }

        image.close()
    }

    /**
     *  Converting image https://stackoverflow.com/a/56812799
     */
    private fun Image.toBitmap(): Bitmap {
        val yBuffer = planes[0].buffer // Y
        val uBuffer = planes[1].buffer // U
        val vBuffer = planes[2].buffer // V

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        //U and V are swapped
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    private fun Bitmap.rotateAndFlipBitmap(degrees: Float): Bitmap {
        val matrix = Matrix().apply {
            postRotate(degrees)
            postScale(-1f, 1f, width / 2f, height / 2f)
        }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

}