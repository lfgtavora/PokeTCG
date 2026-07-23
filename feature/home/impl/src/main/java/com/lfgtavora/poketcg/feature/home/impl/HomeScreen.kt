package com.lfgtavora.poketcg.feature.home.impl

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import androidx.palette.graphics.Palette
import coil3.BitmapImage
import coil3.ColorImage
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import com.lfgtavora.poketcg.model.data.SetPreview
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalCoilApi::class)
@Composable
fun HomeScreen(
    onSetClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val lazyPagingItems = viewModel.setsPagingData.collectAsLazyPagingItems()

    HomeScreen(
        lazyPagingItems = lazyPagingItems,
        modifier = modifier,
        onSetClick = onSetClick,
        previewHandler = LocalAsyncImagePreviewHandler.current
    )
}

@OptIn(ExperimentalCoilApi::class)
@Composable
internal fun HomeScreen(
    lazyPagingItems: LazyPagingItems<SetPreview>,
    modifier: Modifier = Modifier,
    onSetClick: (String) -> Unit,
    previewHandler: AsyncImagePreviewHandler? = null
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .testTag("home_sets_grid")
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {

                Text(
                    text = "Sets",
                    style = MaterialTheme.typography.displaySmall,
                )
            }
            items(
                key = lazyPagingItems.itemKey { it.id },
                count = lazyPagingItems.itemCount,
                contentType = { lazyPagingItems.itemContentType { "SetPreview" } }
            ) { index ->
                lazyPagingItems[index]?.let { setData ->
                    SetCardView(
                        id = setData.id,
                        name = setData.name,
                        imageUrl = setData.logo,
                        totalCards = setData.totalCards,
                        onClick = { onSetClick(setData.id) },
                        previewHandler = previewHandler
                    )
                }
            }

            when (val state = lazyPagingItems.loadState.append) {
                is LoadState.Loading -> {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is LoadState.Error -> {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Button(
                            onClick = { lazyPagingItems.retry() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Erro ao carregar mais itens. Tentar novamente")
                        }
                    }
                }

                else -> {
                    if (state.endOfPaginationReached && lazyPagingItems.itemCount > 0) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                text = "Você chegou ao fim da lista",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        if (lazyPagingItems.loadState.refresh is LoadState.Loading && lazyPagingItems.itemCount == 0) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        if (lazyPagingItems.loadState.refresh is LoadState.Error && lazyPagingItems.itemCount == 0) {
            Text(
                text = "Erro ao carregar os dados",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun SetCardView(
    modifier: Modifier = Modifier,
    id: String,
    name: String,
    imageUrl: String?,
    totalCards: Int? = null,
    previewHandler: AsyncImagePreviewHandler? = null,
    onClick: () -> Unit
) {
    val paletteState = remember { mutableStateOf<Palette?>(null) }
    val palette = paletteState.value

    val startColor =
        palette?.let { Color(it.getDominantColor(android.graphics.Color.WHITE)) } ?: Color.White
    val endColor =
        palette?.let { Color(it.getLightVibrantColor(android.graphics.Color.WHITE)) } ?: Color.White

    Card(
        modifier = modifier
            .height(200.dp)
            .testTag("set_card_$id"),
        onClick = onClick,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(startColor, endColor),
                        start = Offset(0f, Float.POSITIVE_INFINITY),
                        end = Offset(Float.POSITIVE_INFINITY, 0f)
                    )
                )
                .background(
                    color = Color.Black.copy(alpha = .3f)
                )
        ) {
            previewHandler?.let {
                CompositionLocalProvider(LocalAsyncImagePreviewHandler provides it) {

                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "$name set logo",
                        modifier = Modifier
                            .size(120.dp),
                        onSuccess = {
                            (it.result.image as? BitmapImage)?.bitmap?.let { bitmap ->
                                val softwareBitmap =
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && bitmap.config == Bitmap.Config.HARDWARE) {
                                        bitmap.copy(Bitmap.Config.ARGB_8888, false)
                                    } else {
                                        bitmap
                                    }

                                softwareBitmap?.let {
                                    paletteState.value = Palette.from(it).generate()
                                }
                            }
                        }
                    )
                }
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (totalCards != null) {
                    Text(
                        text = "$totalCards cards",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                    )
                }
            }


        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
@Preview
private fun SetCardViewPreview() {
    val previewHandler = AsyncImagePreviewHandler {
        ColorImage(Color.Red.toArgb())
    }
    SetCardView(
        id = fakeSetWithVeryLongName.id,
        name = fakeSetWithVeryLongName.name,
        imageUrl = fakeSetWithVeryLongName.logo,
        totalCards = fakeSetWithVeryLongName.totalCards,
        previewHandler = previewHandler,
        onClick = { }
    )
}


@OptIn(ExperimentalCoilApi::class)
@Composable
@Preview(showBackground = true)
private fun HomeScreenSuccessPreview() {
    val pagingData = PagingData.from(fakeSetList)
    val flowPagingData = flowOf(pagingData)
    val lazyPagingItems = flowPagingData.collectAsLazyPagingItems()

    HomeScreen(
        lazyPagingItems = lazyPagingItems,
        onSetClick = { },
        previewHandler = AsyncImagePreviewHandler {
            ColorImage(Color.Red.toArgb())
        }
    )
}

@OptIn(ExperimentalCoilApi::class)
@Composable
@Preview(showBackground = true)
private fun HomeScreenErrorPreview() {

    val pagingData = PagingData.from(
        data = fakeSetList,
        sourceLoadStates = LoadStates(
            refresh = LoadState.Error(Exception("Error")),
            prepend = LoadState.NotLoading(false),
            append = LoadState.NotLoading(false)
        )
    )

    val flowPagingData = flowOf(pagingData)
    val lazyPagingItems = flowPagingData.collectAsLazyPagingItems()

    HomeScreen(
        lazyPagingItems = lazyPagingItems,
        onSetClick = { },
        previewHandler = AsyncImagePreviewHandler {
            ColorImage(Color.Red.toArgb())
        }
    )
}



