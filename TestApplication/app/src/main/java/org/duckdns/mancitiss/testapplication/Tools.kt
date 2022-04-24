package org.duckdns.mancitiss.testapplication

import android.os.Build
import androidx.annotation.RequiresApi
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.PushbackInputStream
import java.nio.charset.StandardCharsets
import java.util.Base64

class Tools {
    @Throws(IOException::class)
    fun receiveASCII(DIS: DataInputStream, byte_expected: Int): String {
        var byte_expected = byte_expected
        val buffer = ByteArray(byte_expected)
        var total_byte_received = 0
        var received_byte: Int
        do {
            received_byte = DIS.read(buffer, total_byte_received, byte_expected)
            if (received_byte > 0) {
                total_byte_received += received_byte
                byte_expected -= received_byte
            } else break
        } while (byte_expected > 0 && received_byte > 0)
        return if (byte_expected == 0) {
            String(buffer, StandardCharsets.US_ASCII)
        } else {
            //String s = new String(buffer, 0, total_byte_received, StandardCharsets.US_ASCII);
            ""
        }
    }

    // overload receive_ASCII with Pushbackinputstream
    @Throws(IOException::class)
    fun receiveASCII(DIS: PushbackInputStream, byte_expected: Int): String {
        var byte_expected = byte_expected
        val buffer = ByteArray(byte_expected)
        var total_byte_received = 0
        var received_byte: Int
        do {
            received_byte = DIS.read(buffer, total_byte_received, byte_expected)
            if (received_byte > 0) {
                total_byte_received += received_byte
                byte_expected -= received_byte
            } else break
        } while (byte_expected > 0 && received_byte > 0)
        return if (byte_expected == 0) {
            String(buffer, StandardCharsets.US_ASCII)
        } else {
            //String s = new String(buffer, 0, total_byte_received, StandardCharsets.US_ASCII);
            ""
        }
    }

    /**
     *
     * @param DIS
     * @param length
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun receive_unicode(DIS: DataInputStream, byte_expected: Int): String {
        var byte_expected = byte_expected
        val buffer = ByteArray(byte_expected)
        //read until get enough length bytes
        var received_byte: Int
        var total_byte_received = 0
        do {
            received_byte = DIS.read(buffer, total_byte_received, byte_expected)
            if (received_byte > 0) {
                total_byte_received += received_byte
                byte_expected -= received_byte
            }
        } while (byte_expected > 0 && received_byte > 0)
        return if (byte_expected == 0) {
            // return string from byte array as unicode
            String(buffer, StandardCharsets.UTF_16LE)
        } else {
            ""
        }
    }

    // overload receive_unicode with Pushbackinputstream
    @Throws(IOException::class)
    fun receive_unicode(DIS: PushbackInputStream, byte_expected: Int): String {
        var byte_expected = byte_expected
        val buffer = ByteArray(byte_expected)
        //read until get enough length bytes
        var received_byte: Int
        var total_byte_received = 0
        do {
            received_byte = DIS.read(buffer, total_byte_received, byte_expected)
            if (received_byte > 0) {
                total_byte_received += received_byte
                byte_expected -= received_byte
            }
        } while (byte_expected > 0 && received_byte > 0)
        return if (byte_expected == 0) {
            // return string from byte array as unicode
            String(buffer, StandardCharsets.UTF_16LE)
        } else {
            ""
        }
    }

    /**
     *
     * @param DIS
     * @return
     */
    fun receive_ASCII_Automatically(DIS: DataInputStream): String {
        var result: String = ""
        try {
            result = receiveASCII(DIS, 2)
            var bytesize = result.toInt(10)
            result = receiveASCII(DIS, bytesize)
            bytesize = result.toInt(10)
            result = receiveASCII(DIS, bytesize)
        } catch (e: Exception) {
            e.printStackTrace()
            result = ""
        }
        return result
    }

    // overload receive_ASCII_Automatically with Pushbackinputstream
    fun receive_ASCII_Automatically(DIS: PushbackInputStream): String {
        var result = ""
        try {
            result = receiveASCII(DIS, 2)
            var bytesize = result.toInt(10)
            result = receiveASCII(DIS, bytesize)
            bytesize = result.toInt(10)
            result = receiveASCII(DIS, bytesize)
        } catch (e: Exception) {
            e.printStackTrace()
            result = ""
        }
        return result
    }

    /**
     *
     * @param DIS
     * @return
     */
    fun receive_Unicode_Automatically(DIS: DataInputStream): String {
        var result = ""
        try {
            result = receive_unicode(DIS, 4)
            var bytesize = result.toInt(10) * 2
            result = receive_unicode(DIS, bytesize)
            bytesize = result.toInt(10)
            result = receive_unicode(DIS, bytesize)
        } catch (e: Exception) {
            e.printStackTrace()
            result = ""
        }
        return result
    }

    // overload receive_Unicode_Automatically with Pushbackinputstream
    fun receive_Unicode_Automatically(DIS: PushbackInputStream): String {
        var result = ""
        try {
            result = receive_unicode(DIS, 4)
            var bytesize = result.toInt(10) * 2
            result = receive_unicode(DIS, bytesize)
            bytesize = result.toInt(10)
            result = receive_unicode(DIS, bytesize)
        } catch (e: Exception) {
            e.printStackTrace()
            result = ""
        }
        return result
    }

    /**
     *
     * @param s
     * @param n
     * @param c
     * @return
     */
    fun padleft(s: String, n: Int, c: Char): String {
        var s = s
        while (s.length < n) {
            s = c.toString() + s
        }
        return s
    }

    /**
     *
     * @param data
     * @return
     */
    fun data_with_unicode_byte(data: String): String? {
        if (!data.isEmpty()) {
            val databyte = Integer.toString(data.length * 2, 10)
            return padleft(Integer.toString(databyte.length, 10), 2, '0') + databyte + data
        }
        return ""
    }

    /**
     *
     * @param data
     * @return
     */
    fun data_with_ASCII_byte(data: String): String? {
        if (!data.isEmpty()) {
            val databyte = Integer.toString(data.length, 10)
            return padleft(Integer.toString(databyte.length, 10), 2, '0') + databyte + data
        }
        return ""
    }
    //combine n byte arrays

    //combine n byte arrays
    /**
     *
     * @param arrays
     * @return
     */
    fun combine(vararg arrays: ByteArray): ByteArray? {
        var length = 0
        for (array in arrays) {
            length += array.size
        }
        val result = ByteArray(length)
        var offset = 0
        for (array in arrays) {
            System.arraycopy(array, 0, result, offset, array.size)
            offset += array.size
        }
        return result
    }

    /**
     *
     * @param ID1
     * @param ID2
     * @return a sorted array of two IDs from low to high
     */
    fun compareIDs(ID1: String, ID2: String): Array<String>? {
        return if (ID1.compareTo(ID2) <= 0) {
            arrayOf(ID1, ID2)
        } else {
            arrayOf(ID2, ID1)
        }
    }

    /**
     *
     * @param string
     * @return string
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun ImageToBASE64(string: String?): String? {
        var result = ""
        val imagefile = File(string)
        // check if file exists
        if (imagefile.exists()) {
            try {
                // read file
                val imagebyte = ByteArray(imagefile.length() as Int)
                DataInputStream(FileInputStream(imagefile)).use { DIS -> DIS.readFully(imagebyte) }
                // convert to base64
                result = Base64.getEncoder().encodeToString(imagebyte)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }

    /**
     *
     * @param DataInputStream, int
     * @return byte array
     * @throws IOException
     */
    @Throws(IOException::class)
    fun receive_byte_array(DIS: DataInputStream, byte_expected: Int): ByteArray? {
        var byte_expected = byte_expected
        val buffer = ByteArray(byte_expected)
        var total_byte_received = 0
        var received_byte: Int
        do {
            received_byte = DIS.read(buffer, total_byte_received, byte_expected)
            if (received_byte > 0) {
                total_byte_received += received_byte
                byte_expected -= received_byte
            } else break
        } while (byte_expected > 0 && received_byte > 0)
        return if (byte_expected == 0) {
            buffer
        } else {
            ByteArray(0)
        }
    }

    // overload receive_byte_array with Pushbackinputstream
    @Throws(IOException::class)
    fun receive_byte_array(DIS: PushbackInputStream, byte_expected: Int): ByteArray? {
        var byte_expected = byte_expected
        val buffer = ByteArray(byte_expected)
        var total_byte_received = 0
        var received_byte: Int
        do {
            received_byte = DIS.read(buffer, total_byte_received, byte_expected)
            if (received_byte > 0) {
                total_byte_received += received_byte
                byte_expected -= received_byte
            } else break
        } while (byte_expected > 0 && received_byte > 0)
        return if (byte_expected == 0) {
            buffer
        } else {
            ByteArray(0)
        }
    }
}