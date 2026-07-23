package com.lfgtavora.poketcg.feature.card_detail.impl.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ColorImage
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import com.lfgtavora.poketcg.feature.card_detail.impl.preview.fakeAbility
import com.lfgtavora.poketcg.feature.card_detail.impl.preview.fakeAttack
import com.lfgtavora.poketcg.feature.card_detail.impl.preview.fakeAttackMinimal
import com.lfgtavora.poketcg.feature.card_detail.impl.preview.fakeAttackNoDamage
import com.lfgtavora.poketcg.feature.card_detail.impl.preview.fakeCard
import com.lfgtavora.poketcg.feature.card_detail.impl.preview.fakeInfoChips
import com.lfgtavora.poketcg.feature.card_detail.impl.preview.fakeInfoChipsWrapping
import com.lfgtavora.poketcg.model.data.Ability
import com.lfgtavora.poketcg.model.data.Attack
import com.lfgtavora.poketcg.model.data.Card

private const val CardAspectRatio = 2.5f / 3.5f

@Composable
fun CardDetailScreen(
    onBack: () -> Unit,
    viewModel: CardDetailViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    CardDetailScreen(
        uiState = uiState.value,
        onBack = onBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoilApi::class)
@Composable
fun CardDetailScreen(
    onBack: () -> Unit,
    uiState: CardDetailUiState,
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
                                contentDescription = "back",
                            )
                        }
                    },
                    title = {
                        Text(
                            text = (uiState as? CardDetailUiState.Success)?.card?.name.orEmpty()
                        )
                    },
                )
            },
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .testTag("card_detail"),
            ) {
                when (uiState) {
                    CardDetailUiState.Error -> {
                        Text(
                            text = "Failed to load card",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }

                    CardDetailUiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }

                    is CardDetailUiState.Success -> {
                        val card = uiState.card
                        if (card == null) {
                            Text(
                                text = "Card not found",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.align(Alignment.Center),
                            )
                        } else {
                            CardDetailContent(card = card)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CardDetailContent(card: Card) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AsyncImage(
            model = card.imageLarge ?: card.imageSmall,
            contentDescription = card.name,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .widthIn(max = 320.dp)
                .fillMaxWidth()
                .aspectRatio(CardAspectRatio),
        )

        Text(
            text = card.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            card.hp?.let {
                Text(
                    text = "HP $it",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            card.level?.let {
                Text(
                    text = "Lv. $it",
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }

        InfoChips(
            items = buildList {
                add(card.supertype)
                card.subtypes?.let(::addAll)
                card.types?.let(::addAll)
                card.rarity?.let(::add)
                card.regulationMark?.let { add("Reg $it") }
            }
        )

        MetaRow(label = "Number", value = "#${card.number}")
        card.evolvesFrom?.let { MetaRow(label = "Evolves from", value = it) }
        card.evolvesTo?.takeIf { it.isNotEmpty() }?.let {
            MetaRow(label = "Evolves to", value = it.joinToString())
        }
        card.artist?.let { MetaRow(label = "Artist", value = it) }
        card.nationalPokedexNumbers?.takeIf { it.isNotEmpty() }?.let {
            MetaRow(label = "Pokédex", value = it.joinToString { n -> "#$n" })
        }

        card.abilities?.forEach { ability ->
            SectionDivider(title = ability.type)
            AbilityBlock(ability)
        }

        card.attacks?.takeIf { it.isNotEmpty() }?.let { attacks ->
            SectionDivider(title = "Attacks")
            attacks.forEach { AttackBlock(it) }
        }

        val combatRows = buildList {
            card.weaknesses?.takeIf { it.isNotEmpty() }?.let {
                add("Weakness" to it.joinToString { w -> "${w.type} ${w.value}" })
            }
            card.resistances?.takeIf { it.isNotEmpty() }?.let {
                add("Resistance" to it.joinToString { r -> "${r.type} ${r.value}" })
            }
            card.convertedRetreatCost?.let { cost ->
                val energy = card.retreatCost?.joinToString(" ") ?: cost.toString()
                add("Retreat" to energy)
            }
        }
        if (combatRows.isNotEmpty()) {
            SectionDivider(title = "Combat")
            combatRows.forEach { (label, value) ->
                MetaRow(label = label, value = value)
            }
        }

        card.rules?.takeIf { it.isNotEmpty() }?.let { rules ->
            SectionDivider(title = "Rules")
            rules.forEach { rule ->
                Text(
                    text = rule,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        card.ancientTrait?.let { trait ->
            SectionDivider(title = "Ancient Trait")
            Text(
                text = trait.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = trait.text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        card.flavorText?.let { flavor ->
            SectionDivider(title = "Flavor")
            Text(
                text = "“$flavor”",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        card.legalities?.let { legalities ->
            val legalityChips = buildList {
                legalities.standard?.let { add("Standard: $it") }
                legalities.expanded?.let { add("Expanded: $it") }
                legalities.unlimited?.let { add("Unlimited: $it") }
            }
            if (legalityChips.isNotEmpty()) {
                SectionDivider(title = "Legalities")
                InfoChips(items = legalityChips)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SectionDivider(title: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        HorizontalDivider()
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun MetaRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun InfoChips(items: List<String>) {
    if (items.isEmpty()) return
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items.forEach { item ->
            AssistChip(
                onClick = {},
                enabled = false,
                label = { Text(item) },
            )
        }
    }
}

@Composable
private fun AbilityBlock(ability: Ability) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = ability.name,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = ability.text,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun AttackBlock(attack: Attack) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = attack.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                attack.cost?.takeIf { it.isNotEmpty() }?.let { cost ->
                    Text(
                        text = cost.joinToString(" · "),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            attack.damage?.takeIf { it.isNotEmpty() }?.let { damage ->
                Text(
                    text = damage,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        attack.text?.takeIf { it.isNotEmpty() }?.let { text ->
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
private fun previewImageHandler() = AsyncImagePreviewHandler {
    ColorImage(Color(0xFFE53935).toArgb())
}

@OptIn(ExperimentalCoilApi::class)
@Preview(showBackground = true, name = "Screen – Success")
@Composable
private fun CardDetailSuccessPreview() {
    CardDetailScreen(
        onBack = {},
        uiState = CardDetailUiState.Success(fakeCard),
        previewHandler = previewImageHandler(),
    )
}

@Preview(showBackground = true, name = "Screen – Loading")
@Composable
private fun CardDetailLoadingPreview() {
    CardDetailScreen(
        onBack = {},
        uiState = CardDetailUiState.Loading,
    )
}

@Preview(showBackground = true, name = "Screen – Error")
@Composable
private fun CardDetailErrorPreview() {
    CardDetailScreen(
        onBack = {},
        uiState = CardDetailUiState.Error,
    )
}

@Preview(showBackground = true, name = "Screen – Not found")
@Composable
private fun CardDetailNotFoundPreview() {
    CardDetailScreen(
        onBack = {},
        uiState = CardDetailUiState.Success(card = null),
    )
}

@Preview(showBackground = true, name = "Attack – full")
@Composable
private fun AttackBlockFullPreview() {
    AttackBlock(fakeAttack)
}

@Preview(showBackground = true, name = "Attack – no damage")
@Composable
private fun AttackBlockNoDamagePreview() {
    AttackBlock(fakeAttackNoDamage)
}

@Preview(showBackground = true, name = "Attack – minimal")
@Composable
private fun AttackBlockMinimalPreview() {
    AttackBlock(fakeAttackMinimal)
}

@Preview(showBackground = true, name = "Ability")
@Composable
private fun AbilityBlockPreview() {
    AbilityBlock(fakeAbility)
}

@Preview(showBackground = true, name = "InfoChips")
@Composable
private fun InfoChipsPreview() {
    InfoChips(items = fakeInfoChips)
}

@Preview(showBackground = true, name = "InfoChips – wrapping")
@Composable
private fun InfoChipsWrappingPreview() {
    InfoChips(items = fakeInfoChipsWrapping)
}
