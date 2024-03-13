package com.example.expenditurelogger.ocr

import com.example.expenditurelogger.shared.Transaction
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.Text.TextBlock
import kotlin.math.ceil
import kotlin.math.min

class TextParser {

    companion object {

        fun parseTotal(recognizedText: Text): Transaction {
            val verticalSortedTextBlocks = recognizedText.textBlocks.sortedBy { it.boundingBox?.top }

            val totalBlock = parseTotalByString(verticalSortedTextBlocks)
            val totalAmount = if (totalBlock != null)
                searchSameRowTotal(verticalSortedTextBlocks, totalBlock) else null

            val merchant = parseMerchant(verticalSortedTextBlocks)
            val date = parseDate(verticalSortedTextBlocks)

            return Transaction(merchant ?: "", date ?: "", totalAmount ?: 0f)
        }

        fun parseMerchant(textBlocks: List<TextBlock>): String? {
            val merchantStringTemplates = listOf(
                "makia",
                "s-market.*",
                "k-(city|super|)market.*",
                "alko"
            )

            for (textBlock in textBlocks) {
                for (merchantString in merchantStringTemplates) {
                    val merchantRegex = merchantString.toRegex(RegexOption.IGNORE_CASE)

                    val match = merchantRegex.find(textBlock.text)

                    if (match != null) {
                        return match.value
                    }
                }
            }

            return null
        }

        fun parseDate(textBlocks: List<TextBlock>): String? {
            val suitableTextBlocks = textBlocks
                .filter { Regex("\\d").containsMatchIn(it.text) }

            val dateStringTemplates = listOf(
                Regex("\\b\\d.[./]\\d.[./](\\d\\d\\D|\\d\\d\\d\\d)")
            )

            for (textBlock in suitableTextBlocks) {
                for (dateString in dateStringTemplates) {
                    val match = dateString.find(textBlock.text)

                    if (match != null) {
                        return match.value
                    }
                }
            }

            return null
        }

        private fun parseTotalByString(textBlocks: List<TextBlock>, similarityRate: Float = 0.8f): TextBlock? {
            val totalStringTemplates = listOf("yhteensä", "summa")

            for (totalString in totalStringTemplates) {
                val suitableTextBlocks = textBlocks
                    .filter { it.text.length in 4..8 }
                    .filter {
                        calculateSameCharacters(
                            it.text,
                            totalString
                        ) > (totalString.length * similarityRate).toInt()
                    }

                if (suitableTextBlocks.isEmpty()) {
                    continue
                }

                var bestBlock = suitableTextBlocks[0]
                var bestDistance = calculateLevenshteinDistance(suitableTextBlocks[0].text.lowercase(), totalString)

                for (textBlock in suitableTextBlocks) {
                    val currentDistance = calculateLevenshteinDistance(
                        textBlock.text.lowercase(),
                        totalString
                    )

                    if (currentDistance <= bestDistance) {
                        bestBlock = textBlock
                        bestDistance = currentDistance
                    }
                }

                if (bestDistance > ceil(totalString.length * similarityRate)) {
                    continue
                }

                return bestBlock
            }

            return null
        }

        private fun searchSameRowTotal(recognizedText: List<TextBlock>, totalTextBlock: TextBlock): Float? {
            val index = recognizedText.indexOf(totalTextBlock)

            val upperTopLimit = totalTextBlock.boundingBox!!.top - totalTextBlock.boundingBox!!.height()
            val lowerTopLimit = totalTextBlock.boundingBox!!.top + totalTextBlock.boundingBox!!.height()

            val upperBlock = if
                    (recognizedText[index-1].boundingBox!!.top > upperTopLimit) recognizedText[index-1] else null
            val lowerBlock = if (recognizedText[index+1].boundingBox!!.top < lowerTopLimit) recognizedText[index+1] else null

            val totalRegexString = "\\d*[,.]\\d\\d".toRegex()

            val upperText = if (upperBlock != null)
                totalRegexString.find(upperBlock.text)?.value?.replace(",", ".")?.toFloatOrNull()
                else null
            val lowerText = if (lowerBlock != null)
                totalRegexString.find(lowerBlock.text)?.value?.replace(",", ".")?.toFloatOrNull()
                else null

            return upperText ?: lowerText
        }

        private fun calculateSameCharacters(recognizedString: String, targetString: String): Int {
            var count = 0

            for (targetCharacter in targetString) {
                if (recognizedString.contains(targetCharacter, ignoreCase = true)) {
                    count++
                }
            }
            return count
        }

        private fun calculateLevenshteinDistance(recognizedString: String, targetString: String): Int {
            val recognizedStringLength = recognizedString.length
            val targetStringLength = targetString.length

            if (targetStringLength == 0) {
                return recognizedStringLength
            }

            else if (recognizedStringLength == 0) {
                return targetStringLength
            }

            else if (recognizedString[0] == targetString[0]) {
                return calculateLevenshteinDistance(recognizedString.substring(1), targetString.substring(1))
            }

            else {
                return 1 + min(
                    calculateLevenshteinDistance(recognizedString.substring(1), targetString),
                    min(
                        calculateLevenshteinDistance(recognizedString, targetString.substring(1)),
                        calculateLevenshteinDistance(recognizedString.substring(1), targetString.substring(1))
                    )
                )
            }
        }
    }
}