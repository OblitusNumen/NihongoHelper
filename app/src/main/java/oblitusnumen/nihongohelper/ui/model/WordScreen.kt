package oblitusnumen.nihongohelper.ui.model

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import oblitusnumen.nihongohelper.implementation.data.DataManager
import oblitusnumen.nihongohelper.implementation.data.Word
import oblitusnumen.nihongohelper.implementation.data.WordPool
import oblitusnumen.nihongohelper.implementation.equalsStripIgnoreCase
import oblitusnumen.nihongohelper.implementation.measureTextLine

class WordScreen(private val dataManager: DataManager, fileName: String) {
    private val wordPool: WordPool = dataManager.getWordPool(fileName)
    private val wordQueue: MutableList<Word> = wordPool.wordsScrambled().toMutableList()

    @Composable
    fun compose(modifier: Modifier = Modifier) {
        var correctNumber by remember { mutableStateOf(0) }
        var overallNumber by remember { mutableStateOf(0) }
        var isTranslationToJp by remember { mutableStateOf(true) }
        var isTranslationToJpNext by remember { mutableStateOf(isTranslationToJp) }
        var isCorrect by remember { mutableStateOf(false) }
        var hasAnswered by remember { mutableStateOf(false) }
        val word = wordQueue[0]
        val translation = remember { mutableStateOf("") }
        val jp = remember { mutableStateOf("") }
        val hiragana: MutableState<String> = remember { mutableStateOf("") }
        Column(modifier = modifier) {
            Text(
                "Answered: $overallNumber",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
            Text(
                "Correct: $correctNumber",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
            Text(
                "Correct percentage: ${if (overallNumber == 0) "N/A" else "${100f * correctNumber / overallNumber}%"}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
            Button(
                { isTranslationToJpNext = !isTranslationToJpNext },
                Modifier.fillMaxWidth().defaultMinSize(minHeight = 64.dp).padding(vertical = 12.dp, horizontal = 8.dp)
                    .border(2.dp, Color.Gray, shape = RoundedCornerShape(4.dp)).align(Alignment.CenterHorizontally)
            ) {
                Text(if (isTranslationToJpNext) "Russian → Japanese" else "Japanese → Russian")
            }
            Column {
                val modifier1 = Modifier//.weight(1f).align(Alignment.CenterVertically)
                if (isTranslationToJp) {
                    translation.value = word.translationWord
                    qField("Translation", translation.value, modifier1)//translation q
                    answerField("Japanese", hasAnswered, word.jpWord, jp, modifier1)//jp blank
                } else {
                    jp.value = word.jpWord
                    qField("Japanese", jp.value, modifier1)//jp q
                    answerField(
                        "Translation",
                        hasAnswered,
                        word.translationWord,
                        translation,
                        modifier1
                    )//translation blank
                }
                word.hiraganaEquivalent?.let { hiraganaE ->
                    answerField(
                        "Hiragana",
                        hasAnswered,
                        hiraganaE,
                        hiragana,
                        modifier1
                    )//hiragana equivalent
                }
            }
            Spacer(Modifier.weight(1f))
            val buttonModifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp).fillMaxWidth()
            if (!hasAnswered) {
                Button(onClick = {
                    isCorrect = (word.hiraganaEquivalent == null ||
                            word.hiraganaEquivalent.equalsStripIgnoreCase(hiragana.value)) &&
                            word.jpWord.equalsStripIgnoreCase(jp.value) &&
                            word.translationWord.equalsStripIgnoreCase(translation.value)
                    hasAnswered = true
                }, modifier = buttonModifier) {
                    Text("Submit")
                }
            } else {
                Button(onClick = {
                    translation.value = ""
                    jp.value = ""
                    hiragana.value = ""
                    overallNumber++
                    isTranslationToJp = isTranslationToJpNext
                    hasAnswered = false
                    translation.value = ""
                    while (wordQueue.size <= 10) {
                        wordQueue.addAll(wordPool.wordsScrambled())
                    }
                    if (isCorrect) {
                        wordQueue.removeAt(0)
                        correctNumber++
                    } else {
                        if (dataManager.config.repeatNotCorrect) {
                            if (wordQueue[4] != word) wordQueue.add(4, word)
                            if (wordQueue[10] != word) wordQueue.add(10, word)
                        } else
                            wordQueue.removeAt(0)
                    }
                    isCorrect = false
                }, modifier = buttonModifier) {
                    Text("Next")
                }
            }
        }
    }

    @Composable
    fun qField(label: String, value: String, modifier: Modifier = Modifier) {
        Column(modifier = modifier) {
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
            Spacer(modifier = Modifier.padding(8.dp).defaultMinSize(minHeight = 8.dp))
        }
    }

    @Composable
    fun answerField(
        label: String,
        lock: Boolean,
        correctOne: String,
        typedValue: MutableState<String>,
        modifier: Modifier = Modifier
    ) {
        val correct = remember(lock) { typedValue.value.equalsStripIgnoreCase(correctOne) }
        Column {
            OutlinedTextField(
                value = typedValue.value,
                onValueChange = {
                    typedValue.value = it
                },
                label = { Text(label) },
                readOnly = lock,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.padding(8.dp).align(Alignment.CenterHorizontally)
            )
            val spaceModifier = Modifier.padding(8.dp)
                .defaultMinSize(minHeight = measureTextLine(MaterialTheme.typography.bodySmall) * 1.2f)
            if (lock) {
                if (correct)
                    Text(
                        "Correct", color = Color.Green,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = spaceModifier.align(Alignment.CenterHorizontally)
                    )
                else
                    Text(
                        "Correct: $correctOne",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = spaceModifier.align(Alignment.CenterHorizontally)
                    )
            } else
                Spacer(modifier = spaceModifier)
        }
    }

    @Composable
    fun showSettingsDialog(onClose: () -> Unit) {
        val config = dataManager.config
        var repeatIncorrect by remember { mutableStateOf(config.repeatNotCorrect) }
        AlertDialog(
            onDismissRequest = onClose,
            dismissButton = {
                TextButton(onClick = onClose) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onClose()
                    config.repeatNotCorrect = repeatIncorrect
                    dataManager.config = config
                }) {
                    Text("OK")
                }
            },
            text = {
                Row(
                    Modifier.fillMaxWidth().defaultMinSize(minHeight = 80.dp).padding(8.dp).clickable {
                        repeatIncorrect = !repeatIncorrect
                    },
                    horizontalArrangement = Arrangement.Center
                ) {
                    Checkbox(
                        checked = repeatIncorrect,
                        onCheckedChange = { repeatIncorrect = it },
                        modifier = Modifier.padding(8.dp).align(Alignment.CenterVertically)
                    )
                    Text("Repeat incorrect", modifier = Modifier.padding(8.dp).align(Alignment.CenterVertically))
                }
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun topBar(backPress: () -> Unit) {
        var settingsDialogShown by remember { mutableStateOf(false) }
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = .9f),
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = { Text(wordPool.name, maxLines = 1) },
            navigationIcon = {
                IconButton(onClick = backPress) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Localized description"
                    )
                }
            },
            scrollBehavior = scrollBehavior,
            actions = {
                IconButton(onClick = { settingsDialogShown = true }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = null
                    )
                }
            }
        )
        if (settingsDialogShown)
            showSettingsDialog { settingsDialogShown = false }
    }
}
