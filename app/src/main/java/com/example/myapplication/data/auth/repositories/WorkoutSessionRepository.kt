package com.example.myapplication.data.auth.repositories

import android.content.Context
import com.example.myapplication.data.auth.AuthApi
import com.example.myapplication.data.auth.requests.CreateWorkoutSessionRequest
import com.example.myapplication.data.auth.requests.FinishSessionRequest
import com.example.myapplication.data.auth.requests.SessionExerciseRequest
import com.example.myapplication.data.auth.requests.SetRequest
import com.example.myapplication.data.auth.responses.CreateWorkoutSessionResponse
import com.example.myapplication.data.auth.responses.SessionExerciseResponse
import com.example.myapplication.data.auth.responses.SetResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WorkoutSessionRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val api: AuthApi
){
    suspend fun startWorkoutSession(
        createWorkoutSessionRequest: CreateWorkoutSessionRequest
    ): Response<CreateWorkoutSessionResponse> {
        return api.createWorkoutSession(createWorkoutSessionRequest)
    }

    suspend fun addSetToSession(setRequest: SetRequest): Response<SetResponse>
    {
        return api.addSetToSession(setRequest)
    }
    suspend fun finishWorkoutSession(durationMinutes: Int, sessionId: Long): Response<Unit>
    {
        val request = FinishSessionRequest(durationMinutes)
        return api.finishWorkoutSession(sessionId, request)
    }

    suspend fun addSessionExercise(
        requestForSessionExercise: SessionExerciseRequest
    ):Response<SessionExerciseResponse> {
        return api.addExerciseToWorkoutSession(requestForSessionExercise)
    }

}