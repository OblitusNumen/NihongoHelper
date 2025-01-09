package oblitusnumen.nihongohelper.implementation.data

import java.io.File

class WordPool(var file: File) {
    val filename: String = file.name
    val name: String
    private val words: List<Word>

    fun countWords(): Int {
        return words.size
    }

    fun wordsScrambled(): List<Word> = words.shuffled()

    init {
        val lines = file.readLines()
        name = lines[0]
        val words: MutableList<Word> = mutableListOf()
        var i = 1
        while (i < lines.size) {
            if (lines[i].isNotEmpty())
                words.add(Word.ofLine(lines[i]))
            i++
        }
        this.words = words
    }
}