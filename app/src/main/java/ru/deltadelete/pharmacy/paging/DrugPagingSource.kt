package ru.deltadelete.pharmacy.paging

import android.net.http.HttpException
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.Flow
import ru.deltadelete.pharmacy.api.ApiClient
import ru.deltadelete.pharmacy.api.DrugService
import ru.deltadelete.pharmacy.api.dto.Drug
import java.io.IOException

class DrugPagingSource(
    private val service: DrugService
) : PagingSource<Long, Drug>() {
    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Drug> {
        try {

        val currentKey = params.key ?: 0

        val response = service.getDrugList(params.loadSize.toLong(), currentKey).execute()
        val drugs = response.body()
        val nextKey = if (drugs.isNullOrEmpty()) {
            null
        } else {
            (currentKey + params.loadSize)
        }

        val prevKey = if (currentKey == 0L) null
        else currentKey - params.loadSize

        return LoadResult.Page<Long, Drug>(
            data = drugs.orEmpty(),
            prevKey = prevKey,
            nextKey = nextKey
        )
        } catch (e: Exception) {
            e.printStackTrace()
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Long, Drug>): Long? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}

class DrugRemoteDataSource(
    private val service: DrugService
) {
    fun getDrugs(): Flow<PagingData<Drug>> {
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                DrugPagingSource(service)
            }
        ).flow
    }
}