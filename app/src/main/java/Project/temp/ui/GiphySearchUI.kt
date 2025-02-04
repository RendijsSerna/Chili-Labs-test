package Project.temp.ui

import Project.temp.models.viewModels.GiphyViewModel
import Project.temp.models.viewModels.GiphyViewModelFactory
import Project.temp.models.dataclass.GiphyState
import Project.temp.repository.GiphyRepository
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.project.R
import kotlinx.coroutines.delay


@Composable
fun GiphySearchUI(
    modifier: Modifier = Modifier,
    repository: GiphyRepository,
    context: Context
) {
    val viewModel: GiphyViewModel = viewModel(factory = GiphyViewModelFactory(repository))
    var query by rememberSaveable { mutableStateOf("") }
    val giphyState by viewModel.giphyState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                if (visibleItems.isNotEmpty() && visibleItems.last().index >= listState.layoutInfo.totalItemsCount - 5) {
                    viewModel.loadMoreGifs()
                }
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = { viewModel.searchGifs(query) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = giphyState) {
            is GiphyState.Success -> {
                if (state.gifs.isEmpty()) {
                    Text(
                        text = "No GIFs found. Try searching for something else.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    // TODO quality is not working properly takes High Quality always
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.gifs) { gif ->
                            GifItem(
                                gifUrl = gif.lowQualityUrl,
                                onClick = {
                                    val intent = Intent(context, GifDetailActivity::class.java).apply {
                                        putExtra("GIF_URL", gif.highQualityUrl)
                                        putExtra("HIGH_QUALITY", true)
                                    }
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }

            is GiphyState.Error -> {
                Text(
                    text = "Error: ${state.message}",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            GiphyState.Loading -> {
                LoadingAnimation()
            }
        }
    }
}

@Composable
fun GifItem(
    gifUrl: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(8.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = gifUrl),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(query) {
        if (query.isNotBlank()) {
            delay(1500) // 1.5 seconds debounce
            onSearch()
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("Search GIFs") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onSearch,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search")
        }
    }
}

// TODO works but needs testing if UI shows it in correct place
@Composable
fun LoadingAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_animation))

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            modifier = Modifier.size(100.dp)
        )
    }
}

class GifDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val gifUrl = intent.getStringExtra("GIF_URL")
            val isHighQuality = intent.getBooleanExtra("HIGH_QUALITY", false)

            GifDetailUI(gifUrl = gifUrl, isHighQuality = isHighQuality, onBackClick = { finish() })
        }
    }
}

@Composable
fun GifDetailUI(gifUrl: String?, isHighQuality: Boolean, onBackClick: () -> Unit) {

    val finalGifUrl = gifUrl //

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (finalGifUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(finalGifUrl),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Log.e("GifDetailActivity", "GIF URL is null")
            Text("GIF URL is not available.")
        }

        Button(onClick = onBackClick) {
            Text("Back")
        }
    }
}