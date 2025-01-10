package oblitusnumen.nihongohelper

class Config(string: String) {
    var repeatNotCorrect: Boolean = false
    var isTranslateToJp: Boolean = false
    var askHiragana: Boolean = false
    var fastMode: Boolean = false

    init {
        if (string.isNotEmpty()) {
            repeatNotCorrect = string[0] == '1'
            isTranslateToJp = string[1] == '1'
            askHiragana = string[2] == '1'
            fastMode = string[3] == '1'
        }
    }

    override fun toString(): String {
        return (if (repeatNotCorrect) "1" else "0") +
                (if (isTranslateToJp) "1" else "0") +
                (if (askHiragana) "1" else "0") +
                (if (fastMode) "1" else "0")
    }

    companion object {
        fun ofString(string: String): Config {
            return Config(string)
        }
    }
}