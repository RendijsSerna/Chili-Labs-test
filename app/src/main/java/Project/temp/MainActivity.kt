package Project.temp

import Project.temp.repository.GiphyRepository
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import Project.temp.ui.GiphySearchUI
import Project.temp.ui.theme.ProjectTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectApp {
                GiphySearchScreen()
            }
        }
    }
}

@Composable
fun ProjectApp(content: @Composable () -> Unit) {
    ProjectTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}


@Composable
fun GiphySearchScreen() {
    val context = LocalContext.current
    val repository = remember { GiphyRepository() }


    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        GiphySearchUI(
            modifier = Modifier.padding(innerPadding),
            repository = repository,
            context = context
            )
        }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ProjectApp {
        GiphySearchScreen()
    }
}