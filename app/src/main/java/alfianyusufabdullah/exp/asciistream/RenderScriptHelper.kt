package alfianyusufabdullah.exp.asciistream

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.*


class RenderScriptHelper {

    fun c(context: Context, data: ByteArray, w: Int, h: Int): Bitmap {
        val rs = RenderScript.create(context)
        val yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs))

        val yuvType = Type.Builder(rs, Element.U8(rs)).setX(data.size)
        val inData = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT)

        val rgbaType = Type.Builder(rs, Element.RGBA_8888(rs)).setX(w).setY(h)
        val outData = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT)

        inData.copyFrom(data)

        yuvToRgbIntrinsic.setInput(inData)
        yuvToRgbIntrinsic.forEach(outData)

        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        outData.copyTo(bitmap)
        return bitmap
    }

    fun YUV_toRGB(context: Context, yuv: ByteArray?, W: Int, H: Int): Bitmap {
        val rs = RenderScript.create(context)
        val yuvBlder: Type.Builder = Type.Builder(rs, Element.U8(rs))
            .setX(W).setY(H * 3 / 2)
        val allocIn =
            Allocation.createTyped(rs, yuvBlder.create(), Allocation.USAGE_SCRIPT)
        val rgbType: Type = Type.createXY(rs, Element.RGBA_8888(rs), W, H)
        val allocOut = Allocation.createTyped(rs, rgbType, Allocation.USAGE_SCRIPT)
        val scriptC_yuv2rgb = ScriptC_yuv2rbg(rs)
        allocIn.copyFrom(yuv)
        scriptC_yuv2rgb.set_gW(W.toLong())
        scriptC_yuv2rgb.set_gH(H.toLong())
        scriptC_yuv2rgb.set_gYUV(allocIn)
        scriptC_yuv2rgb.forEach_YUV2RGB(allocOut)
        val bmp = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888)
        allocOut.copyTo(bmp)
        allocIn.destroy()
        scriptC_yuv2rgb.destroy()
        return bmp
    }
}