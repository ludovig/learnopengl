package learnopengl

import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.imageio.ImageIO

fun BufferedImage.toBuffer(): ByteBuffer {
    val data = (raster.dataBuffer as DataBufferByte).data
    val buffer = ByteBuffer.allocateDirect(data.size)
    buffer.order(ByteOrder.nativeOrder())
    buffer.put(data)
    buffer.flip()

    return buffer
}

class Image(
    imagePath: String,
) {
    private val file: File

    init {
        val url = ClassLoader.getSystemResource(imagePath)
        file = File(url.toURI())
    }

    fun readImage(): BufferedImage {
        return ImageIO.read(file)
    }
}
