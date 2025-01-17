package oblitusnumen.nihongohelper.ui.model

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import oblitusnumen.nihongohelper.implementation.data.DataManager
import oblitusnumen.nihongohelper.implementation.data.WordPool

class MainScreen(private val dataManager: DataManager) {
    private var wordPools: List<WordPool> by mutableStateOf(dataManager.getWordPools())

    @Composable
    fun compose(modifier: Modifier = Modifier, openPool: (String) -> Unit) {
        LazyColumn(modifier = modifier) {
            items(wordPools) {
                drawWordPool(it) { openPool(it.filename) }
            }
            item {
                Button(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 8.dp),
                    onClick = {
                        val context = dataManager.context
                        val intent =
                            Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/OblitusNumen/NihongoHelper"))
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Log.e("BrowserIntent", "Error starting activity", e)
                            Toast.makeText(context, "Failed to open browser", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                    Text(
                        modifier = Modifier.weight(1.0f).padding(start = 8.dp, end = 8.dp)
                            .align(Alignment.CenterVertically),
                        text = "Visit site",
                        style = MaterialTheme.typography.headlineSmall,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun drawWordPool(wordPool: WordPool, openPool: () -> Unit) {
        var deleteDialogShown by remember { mutableStateOf(false) }
        Row(
            Modifier.defaultMinSize(minHeight = 100.dp).fillMaxWidth().padding(vertical = 4.dp, horizontal = 8.dp)
                .combinedClickable(onLongClick = { deleteDialogShown = true }, onClick = { openPool() })
                .border(2.dp, Color.Gray, shape = RoundedCornerShape(4.dp))
        ) {
            Text(
                modifier = Modifier.weight(1.0f).padding(start = 8.dp, end = 8.dp).align(Alignment.CenterVertically),
                text = wordPool.name,
                style = MaterialTheme.typography.headlineSmall,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
            Text(
                modifier = Modifier.align(Alignment.CenterVertically).padding(start = 8.dp, end = 8.dp),
                text = "${wordPool.countWords()} words",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        if (deleteDialogShown)
            deleteDialog(wordPool) { deleteDialogShown = false }
    }

    @Composable
    fun deleteDialog(wordPool: WordPool, onClose: () -> Unit) {
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
                    wordPool.delete()
                    wordPools = dataManager.getWordPools()
                }) {
                    Text("OK")
                }
            },
            text = {
                Column {
                    Text("Delete ${wordPool.name}?")
                }
            }
        )
    }

    @Composable
    fun functionButton() {
        val filePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) {
            if (it != null) {
                val fileName = dataManager.copyPool(it)
                if (fileName != null)
                    wordPools += dataManager.getWordPool(fileName)
                else
                    Toast.makeText(dataManager.context, "File is invalid", Toast.LENGTH_SHORT).show()
            }
        }
        FloatingActionButton(onClick = { filePickerLauncher.launch("*/*") }) {
            Icon(Icons.Filled.Add, "Add word pool")
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun topBar() {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = .9f),
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = { Text("日本語 Helper", maxLines = 1) },
            scrollBehavior = scrollBehavior,
        )
    }
}