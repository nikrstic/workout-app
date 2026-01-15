package com.example.myapplication.data.auth.repositories

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.myapplication.data.model.Exercise
import com.example.myapplication.data.network.ExerciseApi

class ExercisePagingSource (
    private val api: ExerciseApi
): PagingSource<String, Exercise>(){

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Exercise>{
        return try {
            if (params.key != null) {
                kotlinx.coroutines.delay(1000)
            }
            val nextUrl = params.key?.replace("http://", "https://")
            val response = if (params.key == null){
                api.allExercises()
            }
            else{
                Log.d("API_DEBUG", "Pozivam URL: $nextUrl")
                api.allExerciseByUrl(nextUrl!!)

            }
            if (response.isSuccessful) {
                val body = response.body()
                val data = body?.data ?: emptyList()
                Log.d("API_DEBUG", "Status: ${response.code()}")
                Log.d("API_DEBUG", "Body data size: ${data.size}")

                LoadResult.Page(
                    data = data,
                    prevKey = body?.metadata?.previousPage,
                    nextKey = body?.metadata?.nextPage
                )
            } else {
                val errorMsg = response.errorBody()?.string()
                Log.e("API_DEBUG", "Greska! Kod: ${response.code()}, Error Body: $errorMsg")
                LoadResult.Error(Exception("API Error: ${response.code()}"))
            }

        }catch (e: Exception){
            Log.d("API_DEBUG", "GRESKA $e")
            LoadResult.Error(e)

        }
    }

    override fun getRefreshKey(state: PagingState<String, Exercise>): String? {
        return null
    }
}