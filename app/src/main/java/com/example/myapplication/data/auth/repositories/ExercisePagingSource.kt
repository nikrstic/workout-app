package com.example.myapplication.data.auth.repositories

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.myapplication.data.model.Exercise

class ExercisePagingSource (
    private val allExercises: List<Exercise>?
): PagingSource<Int, Exercise>(){

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Exercise>{
        val position = params.key ?: 0
        return try {
            val pageSize =params.loadSize
            val start = position * pageSize
            val end = minOf(start+pageSize, allExercises!!.size)

            val data = if(start< allExercises.size ){
                allExercises.subList(start, end)
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

    override fun getRefreshKey(state: PagingState<Int, Exercise>): Int? {
        return state.anchorPosition?.let { anchorPosition->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)

        }
    }
}