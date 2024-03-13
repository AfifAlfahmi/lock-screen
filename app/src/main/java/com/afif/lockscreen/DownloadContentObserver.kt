package com.afif.lockscreen

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import java.io.*


class DownloadContentObserver(handler: Handler?, context: Context) : ContentObserver(handler) {
    private var context: Context? = context.applicationContext


    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        Log.d("dwonload_det2", "Download change detected: " + uri!!.path)
        //        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        val downloadId = uri.lastPathSegment!!.toLong()

        // Query the download details using the download ID
        val downloadManager =
            context!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query()
        query.setFilterById(downloadId)
        Log.d("dwonload_det2", "downloadId : $downloadId")
        val fileName = System.currentTimeMillis().toString() + "." + MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(
                context!!.contentResolver.getType(uri)
            )

        val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (downloadsFolder != null && downloadsFolder.exists()) {
            Log.d("dwonload_det2", "downloadsFolder  != null")

            val files = downloadsFolder.listFiles()
            for (file in files) {
                // Perform operations on individual files
                Log.d("dwonload_det2", "down fn : ${file.name}")

            }
        }


            //  var cfile = readTextFromUri(uri)
      //  if (cfile != null) {
      //      Log.d("dwonload_det2", "cfile != null"+ cfile)

//            if (cfile.exists()) {
//                Log.d("dwonload_det2", "file exist")
//
//            }
     //   }

        //val selectedFile = readTextFromUri(uri)
//        if (selectedFile.exists()) {
//            Log.d("dwonload_det2", "file exist")
//
//        }
//        selectedFile.bufferedReader().useLines {
//            Log.d("dwonload_det2", "file content line: $it")
//
//        }
        Log.d("dwonload_det2", "filename: $fileName")
        //Log.d("dwonload_det2", "filename path: " + selectedFile.absolutePath)
    }

    private fun readTextFromUri(uri: Uri): String {
        val stringBuilder = StringBuilder()
        context!!.contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = reader.readLine()
                } }
        }
        return stringBuilder.toString()
    }

    @Throws(IOException::class)
    fun from(context: Context, uri: Uri?): File? {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri!!)
        val fileName: String = getFileName(context, uri)
        val splitName: Array<String> = splitFileName(fileName)
        var tempFile = File.createTempFile(splitName[0], splitName[1])
        tempFile = rename(tempFile, fileName)
        tempFile.deleteOnExit()
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(tempFile)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        if (inputStream != null) {
            if (out != null) {
                copy(inputStream, out)
            }
            inputStream.close()
        }
        if (out != null) {
            out.close()
        }
        return tempFile
    }

    @SuppressLint("Range")
    private fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (cursor != null) {
                    cursor.close()
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf(File.separator)
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }
    private fun splitFileName(fileName: String): Array<String> {
        var name = fileName
        var extension = ""
        val i = fileName.lastIndexOf(".")
        if (i != -1) {
            name = fileName.substring(0, i)
            extension = fileName.substring(i)
        }
        return arrayOf(name, extension)
    }
    private fun rename(file: File, newName: String): File? {
        val newFile = File(file.parent, newName)
        if (newFile != file) {
            if (newFile.exists() && newFile.delete()) {
                Log.d("FileUtil", "Delete old $newName file")
            }
            if (file.renameTo(newFile)) {
                Log.d("FileUtil", "Rename file to $newName")
            }
        }
        return newFile
    }
    @Throws(IOException::class)
    private fun copy(input: InputStream, output: OutputStream): Long {
        var count: Long = 0
        var n: Int
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        while (-1 !== input.read(buffer).also { n = it }) {
            output.write(buffer, 0, n)
            count += n.toLong()
        }
        return count
    }

}