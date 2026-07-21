package com.lfgtavora.poketcg.search.impl

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ColorImage
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import com.lfgtavora.poketcg.core.ui.R
import com.lfgtavora.poketcg.model.SearchResultItem

@OptIn(ExperimentalCoilApi::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onCardClick: (String) -> Unit
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    val query = uiState.value.query
    val isLoading = uiState.value.isLoading
    val error = uiState.value.error
    val items = uiState.value.items

    SearchScreen(
        onCardClick = onCardClick,
        onSearchValueChange = { viewModel.onQueryChange(it) },
        query = query,
        isLoading = isLoading,
        error = error,
        items = items,
        previewHandler = LocalAsyncImagePreviewHandler.current,
    )
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun SearchScreen(
    onCardClick: (String) -> Unit,
    onSearchValueChange: (String) -> Unit,
    query: String,
    isLoading: Boolean,
    error: String?,
    items: List<SearchResultItem>, //TODO add immutableList lib
    previewHandler: AsyncImagePreviewHandler? = null,
) {
    CompositionLocalProvider(
        LocalAsyncImagePreviewHandler provides (previewHandler
            ?: LocalAsyncImagePreviewHandler.current)
    ) {
        Scaffold(
            topBar = {
                SearchInput(
                    onValueChange = onSearchValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    value = query,
                    isLoading = isLoading,
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                if (error != null) {
                    Text(text = error)
                    return@Column
                }
                if (items.isNotEmpty()) {
                    LazyColumn{
                        items(
                            items = items,
                            key = { item ->
                                when (item) {
                                    is SearchResultItem.Card -> "card-${item.id}"
                                    is SearchResultItem.Set -> "set-${item.id}"
                                }
                            }
                        ) { item ->
                            when (item) {
                                is SearchResultItem.Card -> CardItem(
                                    name = item.name,
                                    image = item.image,
                                    setName = item.set?.name,
                                    onClick = { onCardClick(item.id) }
                                )

                                is SearchResultItem.Set -> SetItem(
                                    name = item.name,
                                    image = item.logo,
                                    onClick = { onCardClick(item.id) }
                                )
                            }
                        }
                    }
                }

                if(items.isEmpty() && !isLoading && query.isNotEmpty()){
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No results found")
                    }
                }
            }
        }
    }
}

@Composable
fun SetItem(
    modifier: Modifier = Modifier,
    name: String,
    image: String?,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
    ) {
        AsyncImage(
            model = image,
            contentDescription = null,
            modifier = Modifier
                .height(64.dp)
                .aspectRatio(5f / 7)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = name)
    }
}

@Composable
fun CardItem(
    modifier: Modifier = Modifier,
    name: String,
    image: String?,
    setName: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
    ) {
        AsyncImage(
            model = image,
            contentDescription = null,
            placeholder = painterResource(R.drawable.card_back),
            error = painterResource(R.drawable.card_back),
            modifier = Modifier
                .height(64.dp)
                .aspectRatio(5f / 7)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
            )

            if (setName != null) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = setName,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

        }
    }

}

@Composable
fun SearchInput(
    modifier: Modifier = Modifier,
    value: String = "",
    isLoading: Boolean = false,
    placeholder: String = "Type pokemon name...",
    onValueChange: (String) -> Unit,
) {
    TextField(
        value = value,
        placeholder = { Text(text = placeholder) },
        onValueChange = onValueChange,
        trailingIcon = {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp)
                )
            } else if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search"
                    )
                }
            }
        },
        modifier = modifier.testTag("search_field")
    )
}

private val fakeSearchResults = listOf(
    SearchResultItem.Card(
        id = "swsh1-25",
        name = "Pikachu",
        image = "https://assets.tcgdex.net/en/swsh/swsh1/25/low.webp",
        set = SearchResultItem.Set(
            id = "swsh1",
            name = "Sword & Shield",
            logo = "https://assets.tcgdex.net/en/swsh/swsh1/logo.webp",
            series = "Sword & Shield",
        ),
    ),
    SearchResultItem.Set(
        id = "swsh1",
        name = "Sword & Shield",
        logo = "https://assets.tcgdex.net/en/swsh/swsh1/logo.webp",
        series = "Sword & Shield",
    ),
    SearchResultItem.Card(
        id = "swsh3-20",
        name = "Charizard",
        image = "https://assets.tcgdex.net/en/swsh/swsh3/20/low.webp",
        set = SearchResultItem.Set(
            id = "swsh3",
            name = "Darkness Ablaze",
            logo = "https://assets.tcgdex.net/en/swsh/swsh3/logo.webp",
            series = "Sword & Shield",
        ),
    ),
)

@OptIn(ExperimentalCoilApi::class)
private fun previewImageHandler() = AsyncImagePreviewHandler {
    ColorImage(Color.Red.toArgb())
}

@OptIn(ExperimentalCoilApi::class)
@Preview(showBackground = true)
@Composable
private fun SearchScreenEmptyPreview() {
    SearchScreen(
        onCardClick = {},
        onSearchValueChange = {},
        query = "",
        isLoading = false,
        error = null,
        items = emptyList(),
        previewHandler = previewImageHandler(),
    )
}

@OptIn(ExperimentalCoilApi::class)
@Preview(showBackground = true)
@Composable
private fun SearchScreenLoadingPreview() {
    SearchScreen(
        onCardClick = {},
        onSearchValueChange = {},
        query = "Pikachu",
        isLoading = true,
        error = null,
        items = emptyList(),
        previewHandler = previewImageHandler(),
    )
}

@OptIn(ExperimentalCoilApi::class)
@Preview(showBackground = true)
@Composable
private fun SearchScreenSuccessPreview() {
    SearchScreen(
        onCardClick = {},
        onSearchValueChange = {},
        query = "Pikachu",
        isLoading = false,
        error = null,
        items = fakeSearchResults,
        previewHandler = previewImageHandler(),
    )
}

@OptIn(ExperimentalCoilApi::class)
@Preview(showBackground = true)
@Composable
private fun SearchScreenErrorPreview() {
    SearchScreen(
        onCardClick = {},
        onSearchValueChange = {},
        query = "Pikachu",
        isLoading = false,
        error = "Something went wrong",
        items = emptyList(),
        previewHandler = previewImageHandler(),
    )
}
