package oblitusnumen.nihongohelper

class Config(string: String) {
    var repeatNotCorrect: Boolean = false

    init {
        if (string.isNotEmpty()) {
            repeatNotCorrect = string[0] == '1'
        }
    }

    override fun toString(): String {
        return if (repeatNotCorrect) "1" else "0"
    }

    companion object {
        fun ofString(string: String): Config {
            return Config(string)
        }
    }
}