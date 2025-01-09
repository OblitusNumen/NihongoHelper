package oblitusnumen.nihongohelper.implementation.data

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import oblitusnumen.nihongohelper.Config
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class DataManager(private val c: Context) {
    private val poolsDir: File
    var config: Config = Config.ofString(getSharedPrefs(c).getString(CONFIG_PREF_NAME, "")!!)
        set(config) {
            getSharedPrefs(c).edit().putString(CONFIG_PREF_NAME, config.toString()).apply()
            field = config
        }

    init {
        val poolsDirectory = File(c.filesDir, WORD_POOL_DIRECTORY)
        if (!poolsDirectory.exists() && !poolsDirectory.mkdirs()) throw IOException("Could not create directory")
        poolsDir = poolsDirectory
    }

    fun getWordPools(): List<WordPool> {
        val result: MutableList<WordPool> = ArrayList()
        for (poolFile in poolsDir.listFiles() ?: throw IOException("Could not list files from directory")) {
            result.add(WordPool(poolFile))
        }
        return result
    }

    fun getWordPool(fileName: String): WordPool {
        return WordPool(File(poolsDir, fileName))
    }

    fun copyPool(uri: Uri): String {
        val randomUUID = UUID.randomUUID().toString()
        c.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(File(poolsDir, randomUUID)).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return randomUUID
    }

    fun deletePool(wordPool: WordPool) {
        wordPool.file.delete()
    }

    companion object {
        private const val WORD_POOL_DIRECTORY: String = "word-pools"
        private const val SHARED_PREFERENCES_NAME: String = "nihongo_helper_preferences"
        private const val CONFIG_PREF_NAME: String = "config"

        fun getSharedPrefs(context: Context): SharedPreferences {
            return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        }
    }
}