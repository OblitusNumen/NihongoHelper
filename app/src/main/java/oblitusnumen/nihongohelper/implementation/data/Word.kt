package oblitusnumen.nihongohelper.implementation.data

class Word internal constructor(val translationWord: String, val jpWord: String, val hiraganaEquivalent: String?) {
    companion object {
        fun ofLine(word: String): Word {
            val strings = word.split(",", "|", "\t")
            return Word(strings[0], strings[1], if (strings.size == 2) null else strings[2])
        }
    }
}
