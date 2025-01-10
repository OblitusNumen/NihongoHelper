package oblitusnumen.nihongohelper.implementation.data

import java.io.File
import java.io.InputStream

class WordPool {
    private var file: File? = null
    val filename: String
    val name: String
    private val words: List<Word>

    constructor(file: File) : this(file.readLines(), file.name) {
        this.file = file
    }

    constructor(inputStream: InputStream) : this(inputStream.bufferedReader().use { it.readLines() }, "")

    private constructor(lines: List<String>, filename: String) {
        this.filename = filename
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

    fun countWords(): Int {
        return words.size
    }

    fun wordsScrambled(): List<Word> = words.shuffled()

    fun delete() {
        file?.delete()
    }
}