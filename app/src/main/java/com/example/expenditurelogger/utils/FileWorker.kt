package com.example.expenditurelogger.utils

import android.content.Context
import java.io.FileNotFoundException

class FileWorker(context: Context, fileName: String) {

    private val fileName = fileName
    private var fileList: MutableList<String> = readFileToList(context)

    fun getFileList(): MutableList<String> {
        return fileList
    }

    fun updateFileList(newList: MutableList<String>, context: Context) {
        fileList = newList
        writeFileList(context)
    }

    private fun readFileToList(context: Context): MutableList<String> {
        try {
            context.openFileInput(
                fileName
            ).bufferedReader().useLines { lines ->
                return lines.toMutableList()
            }
        } catch (e: FileNotFoundException) {
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                it.write("".toByteArray())
            }

            return emptyList<String>().toMutableList()
        }
    }

    private fun writeFileList(context: Context) {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            for (item in fileList) {
                it.write((item + "\n").toByteArray())
            }
        }
    }
}