package com.lfgtavora.poketcg.feature.card_detail.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage

@Composable
fun CardDetailScreen(
    onBack: () -> Unit,
    viewModel: CardDetailViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    CardDetailScreen(
        uiState = uiState.value,
        onBack = onBack
    )
}

@Composable
fun CardDetailScreen(
    onBack: () -> Unit,
    uiState: CardDetailUiState,
) {
    Scaffold() { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("card_detail")
        ) {
            when (uiState) {
                CardDetailUiState.Error -> {
                    Text(text = "Error")
                }

                CardDetailUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is CardDetailUiState.Success -> {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.width(width = 400.dp)
                        ) {
                            AsyncImage(
                                model = uiState.card?.imageLarge,
                                contentDescription = uiState.card?.name
                            )
                        }
                    }
                    Text(text = uiState.card?.name.orEmpty())
                }
            }
        }
    }
}

