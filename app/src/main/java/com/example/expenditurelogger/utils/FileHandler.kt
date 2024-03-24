package com.example.expenditurelogger.utils

import android.content.Context
import java.io.FileNotFoundException

object FileHandler {

    private const val merchantFileName = "listOfMerchants.txt"
    private var listOfMerchants: MutableList<String> = emptyList<String>().toMutableList()


    fun init(context: Context) {
        listOfMerchants = readListOfMerchantsToFile(context)
    }

    fun getListOfMerchants(): MutableList<String> {
        return listOfMerchants
    }

    fun updateListOfMerchants(newList: MutableList<String>, context: Context) {
        listOfMerchants = newList
        writeListOfMerchantsToFile(context)
    }

    private fun readListOfMerchantsToFile(context: Context): MutableList<String> {
        try {
            context.openFileInput(
                merchantFileName
            ).bufferedReader().useLines { lines ->
                return lines.toMutableList()
            }
        } catch (e: FileNotFoundException) {
            context.openFileOutput(merchantFileName, Context.MODE_PRIVATE).use {
                it.write("".toByteArray())
            }

            return emptyList<String>().toMutableList()
        }
    }

    private fun writeListOfMerchantsToFile(context: Context) {
        context.openFileOutput(merchantFileName, Context.MODE_PRIVATE).use {
            for (merchantName in listOfMerchants) {
                it.write((merchantName + "\n").toByteArray())
            }
        }
    }
}