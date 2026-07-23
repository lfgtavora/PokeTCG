package com.lfgtavora.poketcg.feature.sets.impl.domain

import androidx.paging.PagingData
import com.lfgtavora.poketcg.data.repository.CardRepository
import com.lfgtavora.poketcg.model.data.CardPreview
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCardsPreviewFromSetUseCase @Inject constructor(
    private val cardRepository: CardRepository
) {
    operator fun invoke(
        setId: String,
        pageSize: Int = DEFAULT_PAGE_SIZE
    ): Flow<PagingData<CardPreview>> {

        val query = "set.id:$setId"
        val select = "id,name,images,number,supertype,set"

        return cardRepository.getCardsFromSet(
            setId = setId,
            pageSize = pageSize,
            query = query,
            select = select,
            orderBy = "number"
        )
    }


    companion object Companion {
        private const val DEFAULT_PAGE_SIZE = 32
    }
}

