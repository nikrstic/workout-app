package com.example.myapplication.data.auth.repositories

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.myapplication.data.model.Exercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExercisePagingSource (
    private val repository: ExerciseRepository,
    private val query: String,
    private val bodyPart: String?,
    private val equipment: String?
): PagingSource<Int, Exercise>(){

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Exercise>{
        val position = params.key ?: 0
        return withContext(Dispatchers.Default) {
            try {

                val filteredExercise = repository.getFilteredExercises(query,bodyPart,equipment)

                val end = minOf(position + params.loadSize, filteredExercise.size)

                val data = if (position < filteredExercise.size) {
                    filteredExercise.subList(position, end)
                } else {
                    emptyList()
                }

                LoadResult.Page(
                    data = data,
                    prevKey = if (position == 0) null else position - params.loadSize,
                    nextKey = if (end >= filteredExercise.size) null else end
                )
            } catch (e: Exception) {
                Log.e("API_DEBUG", "GRESKA U PAGINGU: ${e.message}")
                LoadResult.Error(e)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Exercise>): Int? {
        return state.anchorPosition?.let { anchorPosition->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)

        }
    }
}