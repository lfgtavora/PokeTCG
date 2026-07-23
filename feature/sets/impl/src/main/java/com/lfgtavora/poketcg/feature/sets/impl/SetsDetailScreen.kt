package com.lfgtavora.poketcg.feature.sets.impl

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil3.ColorImage
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import com.lfgtavora.poketcg.core.ui.R
import com.lfgtavora.poketcg.feature.sets.impl.preview.fakeCardPreview
import com.lfgtavora.poketcg.feature.sets.impl.preview.fakeCardPreviewList
import com.lfgtavora.poketcg.feature.sets.impl.preview.fakeSet
import com.lfgtavora.poketcg.model.data.CardPreview
import com.lfgtavora.poketcg.ui.PokecardCard
import kotlinx.coroutines.flow.flowOf

private val QuickLookShape = RoundedCornerShape(12.dp)
private const val CardAspectRatio = 2.5f / 3.5f
private val GridHorizontalPadding = 8.dp

@OptIn(ExperimentalCoilApi::class)
@Composable
internal fun SetsDetailsScreen(
    viewModel: SetsDetailsViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onItemClick: (id: String) -> Unit = {}
) {
    val setUiState = viewModel.setUiState.collectAsStateWithLifecycle()
    val cardsPagingItems = viewModel.cardsPagingData.collectAsLazyPagingItems()

    SetDetailScreen(
        setUiState = setUiState.value,
        cardsPagingItems = cardsPagingItems,
        onBack = onBack,
        onItemClick = onItemClick
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoilApi::class)
@Composable
internal fun SetDetailScreen(
    setUiState: SetUiState,
    cardsPagingItems: LazyPagingItems<CardPreview>,
    onBack: () -> Unit = {},
    onItemClick: (id: String) -> Unit = {},
    previewHandler: AsyncImagePreviewHandler? = null,
) {
    CompositionLocalProvider(
        LocalAsyncImagePreviewHandler provides (previewHandler
            ?: LocalAsyncImagePreviewHandler.current)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "back"
                            )
                        }
                    },
                    title = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(TopAppBarDefaults.MediumAppBarCollapsedHeight)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (setUiState is SetUiState.Success) {
                                AsyncImage(
                                    model = setUiState.set.logo,
                                    contentDescription = setUiState.set.name,
                                    modifier = Modifier.widthIn(max = 100.dp)
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "information"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            PokeCardList(
                cardsPagingItems = cardsPagingItems,
                onItemClick = onItemClick,
                contentPadding = PaddingValues(
                    start = GridHorizontalPadding,
                    top = innerPadding.calculateTopPadding(),
                    end = GridHorizontalPadding,
                    bottom = innerPadding.calculateBottomPadding(),
                ),
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PokeCardList(
    cardsPagingItems: LazyPagingItems<CardPreview>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(8.dp),
    onItemClick: (id: String) -> Unit
) {
    var selectedCard by remember { mutableStateOf<CardPreview?>(null) }
    val gridState = rememberLazyGridState()
    val isRefreshLoading = cardsPagingItems.loadState.refresh is LoadState.Loading

    val gridBlur by animateDpAsState(
        targetValue = if (selectedCard != null) 20.dp else 0.dp,
        label = "quickLookBlur",
    )

    BackHandler(enabled = selectedCard != null) {
        selectedCard = null
    }

    SharedTransitionLayout(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(4),
                contentPadding = contentPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                userScrollEnabled = !isRefreshLoading || cardsPagingItems.itemCount > 0,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("set_cards_grid")
                    .blur(
                        radius = gridBlur,
                        edgeTreatment = BlurredEdgeTreatment.Unbounded,
                    )
            ) {
                items(
                    key = cardsPagingItems.itemKey { it.id },
                    count = cardsPagingItems.itemCount,
                    contentType = cardsPagingItems.itemContentType { "pokecard_preview" }
                ) { index ->
                    val card = cardsPagingItems[index]
                    if (card == null) {
                        CardPlaceholder()
                        return@items
                    }

                    val isSelected = card.id == selectedCard?.id

                    Box(modifier = Modifier.alpha(if (isSelected) 0f else 1f)) {
                        PokecardCard(
                            id = card.id,
                            name = card.name,
                            imageUrl = card.image.small,
                            onClick = onItemClick,
                            onLongClick = { selectedCard = card },
                            modifier = Modifier.sharedElementWithCallerManagedVisibility(
                                sharedContentState = rememberSharedContentState(
                                    key = "card-${card.id}"
                                ),
                                visible = !isSelected,
                            ),
                        )
                    }
                }

                if (!isRefreshLoading) {
                    when (val state = cardsPagingItems.loadState.append) {
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
                                ErrorState(
                                    message = "Something went wrong",
                                    onRetry = { cardsPagingItems.retry() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                )
                            }
                        }

                        else -> {
                            if (state.endOfPaginationReached && cardsPagingItems.itemCount > 0) {

                            }
                        }
                    }
                }
            }

            if (isRefreshLoading && cardsPagingItems.itemCount == 0) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            if (cardsPagingItems.loadState.refresh is LoadState.Error && cardsPagingItems.itemCount == 0) {
                ErrorState(
                    message = "Something went wrong",
                    onRetry = { cardsPagingItems.retry() },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                )
            }

            CardQuickLookOverlay(
                card = selectedCard,
                onDismiss = { selectedCard = null },
            )
        }
    }
}

@Composable
private fun CardPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(CardAspectRatio)
            .clip(QuickLookShape)
            .background(Color.LightGray.copy(alpha = 0.35f))
    )
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Button(onClick = onRetry) {
            Text(text = "Try again")
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.CardQuickLookOverlay(
    card: CardPreview?,
    onDismiss: () -> Unit,
) {
    AnimatedContent(
        targetState = card,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "CardQuickLook",
        modifier = Modifier.fillMaxSize(),
    ) { targetCard ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            if (targetCard != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onDismiss,
                        )
                )

                Box(
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .fillMaxWidth(0.85f)
                        .testTag("card_quick_look")
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(
                                key = "card-${targetCard.id}-bounds"
                            ),
                            animatedVisibilityScope = this@AnimatedContent,
                            clipInOverlayDuringTransition = OverlayClip(QuickLookShape),
                        )
                        .clip(QuickLookShape)
                ) {
                    AsyncImage(
                        model = targetCard.image.large,
                        contentDescription = targetCard.name,
                        contentScale = ContentScale.Fit,
                        placeholder = painterResource(R.drawable.card_back),
                        error = painterResource(R.drawable.card_back),
                        modifier = Modifier
                            .sharedElement(
                                sharedContentState = rememberSharedContentState(
                                    key = "card-${targetCard.id}"
                                ),
                                animatedVisibilityScope = this@AnimatedContent,
                            )
                            .fillMaxWidth()
                            .aspectRatio(CardAspectRatio)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { },
                            ),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
private fun previewImageHandler() = AsyncImagePreviewHandler {
    ColorImage(Color(0xFFE53935).toArgb())
}

@Composable
private fun rememberPreviewPagingItems(
    data: List<CardPreview> = emptyList(),
    refresh: LoadState = LoadState.NotLoading(endOfPaginationReached = false),
    append: LoadState = LoadState.NotLoading(endOfPaginationReached = true),
): LazyPagingItems<CardPreview> {
    val flow = remember(data, refresh, append) {
        flowOf(
            PagingData.from(
                data = data,
                sourceLoadStates = LoadStates(
                    refresh = refresh,
                    prepend = LoadState.NotLoading(endOfPaginationReached = false),
                    append = append,
                ),
            )
        )
    }
    return flow.collectAsLazyPagingItems()
}

@OptIn(ExperimentalCoilApi::class)
@Preview(showBackground = true, name = "Screen – Success")
@Composable
private fun SetDetailSuccessPreview() {
    SetDetailScreen(
        setUiState = SetUiState.Success(fakeSet),
        cardsPagingItems = rememberPreviewPagingItems(data = fakeCardPreviewList),
        previewHandler = previewImageHandler(),
    )
}

@OptIn(ExperimentalCoilApi::class)
@Preview(showBackground = true, name = "Screen – Loading")
@Composable
private fun SetDetailLoadingPreview() {
    SetDetailScreen(
        setUiState = SetUiState.Loading,
        cardsPagingItems = rememberPreviewPagingItems(
            refresh = LoadState.Loading,
        ),
        previewHandler = previewImageHandler(),
    )
}

@OptIn(ExperimentalCoilApi::class)
@Preview(showBackground = true, name = "Screen – Error")
@Composable
private fun SetDetailErrorPreview() {
    SetDetailScreen(
        setUiState = SetUiState.Error,
        cardsPagingItems = rememberPreviewPagingItems(
            refresh = LoadState.Error(Exception("Failed to load cards")),
        ),
        previewHandler = previewImageHandler(),
    )
}

@OptIn(ExperimentalCoilApi::class)
@Preview(showBackground = true, name = "Screen – Append loading")
@Composable
private fun SetDetailAppendLoadingPreview() {
    SetDetailScreen(
        setUiState = SetUiState.Success(fakeSet),
        cardsPagingItems = rememberPreviewPagingItems(
            data = fakeCardPreviewList,
            append = LoadState.Loading,
        ),
        previewHandler = previewImageHandler(),
    )
}

@Preview(showBackground = true, name = "CardPlaceholder")
@Composable
private fun CardPlaceholderPreview() {
    CardPlaceholder()
}

@Preview(showBackground = true, name = "ErrorState")
@Composable
private fun ErrorStatePreview() {
    ErrorState(
        message = "Something went wrong",
        onRetry = {},
        modifier = Modifier.padding(24.dp),
    )
}

@OptIn(ExperimentalCoilApi::class, ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true, name = "QuickLook")
@Composable
private fun CardQuickLookOverlayPreview() {
    CompositionLocalProvider(
        LocalAsyncImagePreviewHandler provides previewImageHandler()
    ) {
        SharedTransitionLayout {
            CardQuickLookOverlay(
                card = fakeCardPreview,
                onDismiss = {},
            )
        }
    }
}
