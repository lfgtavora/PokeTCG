package com.lfgtavora.poketcg.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.lfgtavora.poketcg.core.ui.R

private const val CardAspectRatio = 2.5f / 3.5f

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PokecardCard(
    modifier: Modifier = Modifier,
    id: String,
    name: String,
    imageUrl: String? = null,
    onClick: (id: String) -> Unit,
    onLongClick: ((id: String) -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(CardAspectRatio)
            .combinedClickable(
                onClick = { onClick(id) },
                onLongClick = onLongClick?.let { { it(id) } },
            )
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = name,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.card_back),
            error = painterResource(R.drawable.card_back),
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview
@Composable
private fun PokecardCardPreview() {
    PokecardCard(
        id = "",
        name = "Card name",
        imageUrl = "",
        onClick = {}
    )
}
