package homeway.com.network

import java.io.IOException
import java.io.InputStreamReader
import java.io.StringWriter

/**
 * This object class provides helper methods for opening files.
 */
object TestUtil {

    /**
     * This method will open a file and return the contents of a given file in a string format.
     *
     * @param path is the path that we will attempt to open a file at
     * @throws IOException is the exception that was thrown if the file was unable to be opened
     * @return the contents of the file that was opened
     */
    @Throws(IOException::class)
    fun getContentStringFromFile(path: String): String {
        var inputStream: InputStreamReader? = null
        val writer = StringWriter()
        val content: String

        try {
            inputStream = InputStreamReader(TestUtil::class.java.getResourceAsStream(path))
            val buffer = CharArray(1024)

            var n = inputStream.read(buffer)
            while (n != -1) {
                writer.write(buffer, 0, n)
                n = inputStream.read(buffer)
            }

            content = writer.toString()

        } catch (e: Exception) {
            throw e
        } finally {
            inputStream?.close()

            writer.close()
        }

        return content
    }

}