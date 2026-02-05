package com.lfgtavora.poketcg.feature.sets.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.lfgtavora.poketcg.model.CardPreview
import com.lfgtavora.poketcg.ui.PokecardCard

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SetDetailScreen(
    setUiState: SetUiState,
    cardsPagingItems: LazyPagingItems<CardPreview>,
    onBack: () -> Unit = {},
    onItemClick: (id: String) -> Unit = {}
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
                    if (setUiState is SetUiState.Success)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(TopAppBarDefaults.MediumAppBarCollapsedHeight)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = setUiState.set.logo,
                                contentDescription = setUiState.set.name,
                                modifier = Modifier.widthIn(max = 160.dp)
                            )
                        }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Text(text = "back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            PokeCardList(
                cardsPagingItems = cardsPagingItems,
                onItemClick = onItemClick
            )
        }
    }
}

@Composable
fun PokeCardList(
    cardsPagingItems: LazyPagingItems<CardPreview>,
    onItemClick: (id: String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                key = cardsPagingItems.itemKey { it.id },
                count = cardsPagingItems.itemCount,
                contentType = { cardsPagingItems.itemContentType { "pokecard_preview" } }
            ) { index ->
                cardsPagingItems[index]?.let { card ->
                    PokecardCard(
                        id = card.id,
                        name = card.name,
                        imageUrl = card.image,
                        onClick = onItemClick
                    )
                }
            }

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
                        Button(
                            onClick = { cardsPagingItems.retry() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Try again")
                        }
                    }
                }

                else -> {
                    if (state.endOfPaginationReached && cardsPagingItems.itemCount > 0) {
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

        if (cardsPagingItems.loadState.refresh is LoadState.Loading && cardsPagingItems.itemCount == 0) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        if (cardsPagingItems.loadState.refresh is LoadState.Error && cardsPagingItems.itemCount == 0) {
            Text(text = "Try again")
            Button(
                onClick = { cardsPagingItems.retry() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Try again")
            }
        }
    }
}


@Preview
@Composable
private fun SetDetailUiScreenPreview() {


}

