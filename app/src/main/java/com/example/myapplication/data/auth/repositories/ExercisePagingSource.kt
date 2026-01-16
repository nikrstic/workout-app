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
        return withContext(Dispatchers.IO) {
            try {
                val allExercises = repository.getAllExercises()

                val filteredExercise = allExercises!!.filter { exercise ->
                    val matchesQuery = exercise.name.contains(query, ignoreCase = true)
                    val matchesBodyPart = bodyPart == null || exercise.bodyParts.contains(bodyPart)
                    val matchesEquipment = equipment == null || exercise.equipments.contains(equipment)

                    matchesQuery && matchesBodyPart && matchesEquipment
                }

                val pageSize =params.loadSize
                val start = position * pageSize
                val end = minOf(start+pageSize, filteredExercise.size)

                val data = if(start< filteredExercise.size ){
                    filteredExercise.subList(start, end)
                }else{
                    emptyList()
                }
                LoadResult.Page(
                    data = data,
                    prevKey = if(position == 0) null else position -1,
                    nextKey = if(end >= allExercises.size) null else position + 1
                )
            }catch (e: Exception){
                Log.d("API_DEBUG", "GRESKA $e")
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