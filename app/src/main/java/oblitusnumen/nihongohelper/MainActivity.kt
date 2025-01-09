package oblitusnumen.nihongohelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import oblitusnumen.nihongohelper.implementation.data.DataManager
import oblitusnumen.nihongohelper.ui.model.MainScreen
import oblitusnumen.nihongohelper.ui.model.WordScreen
import oblitusnumen.nihongohelper.ui.theme.NihongoHelperTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NihongoHelperTheme {
                val dataManager = remember { DataManager(this) }
                val mainScreen = remember { MainScreen(dataManager) }
                var wordScreen: WordScreen? by remember { mutableStateOf(null) }
                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (wordScreen == null)
                            mainScreen.topBar()
                        else
                            wordScreen!!.topBar({ wordScreen = null })
                    },
                    floatingActionButton = {
                        if (wordScreen == null)
                            mainScreen.functionButton()
                    }) { innerPadding ->
                    if (wordScreen == null)
                        mainScreen.compose(Modifier.padding(innerPadding)) { wordScreen = WordScreen(dataManager, it) }
                    else {
                        BackHandler {
                            wordScreen = null
                        }
                        wordScreen!!.compose(Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}
