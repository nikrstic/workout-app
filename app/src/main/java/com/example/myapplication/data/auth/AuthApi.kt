package com.example.myapplication.data.auth


import com.example.myapplication.data.auth.requests.AddExerciseToPlanRequest
import com.example.myapplication.data.auth.requests.LoginRequest
import com.example.myapplication.data.auth.requests.RegisterRequest
import com.example.myapplication.data.auth.requests.WorkoutPlanRequest
import com.example.myapplication.data.auth.requests.CreateExerciseRequest
import com.example.myapplication.data.auth.requests.CreateWorkoutSessionRequest
import com.example.myapplication.data.auth.requests.FinishSessionRequest
import com.example.myapplication.data.auth.requests.SessionExerciseRequest
import com.example.myapplication.data.auth.requests.SetRequest
import com.example.myapplication.data.auth.responses.AuthResponse
import com.example.myapplication.data.auth.responses.CreateExerciseResponse
import com.example.myapplication.data.auth.responses.CreateWorkoutSessionResponse
import com.example.myapplication.data.auth.responses.ExerciseResponseById
import com.example.myapplication.data.auth.responses.PlanExercisesResponse
import com.example.myapplication.data.auth.responses.SessionExerciseResponse
import com.example.myapplication.data.auth.responses.SessionsResponse
import com.example.myapplication.data.auth.responses.SetResponse
import com.example.myapplication.data.auth.responses.WorkoutPlanResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AuthApi {

    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("api/v1/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ):Response<AuthResponse>

    //get all plans for current user
    @GET("api/plans/user")
    suspend fun  getPlans(
    ): Response<List<WorkoutPlanResponse>>

    @POST("api/plans/create")
    suspend fun createPlan(
        @Body request: WorkoutPlanRequest
    ): Response<WorkoutPlanResponse>

    @DELETE("api/plans/{planId}")
    suspend fun deletePlan(
        @Path("planId") planId: Long
    ): Response<Unit>

    @GET("api/exercises/{exerciseId}")
    suspend fun getExerciseById(
        @Path("exerciseId") exerciseId: Long
    ): Response<ExerciseResponseById>
    @POST("api/exercises/create")
    suspend fun createExercise(
        @Body request: CreateExerciseRequest
    ):Response<CreateExerciseResponse>
    @POST("api/plans/{planId}/exercises")
    suspend fun addExerciseToPlan(
        @Path("planId") planId: Long,
        @Body request: AddExerciseToPlanRequest
    ): Response<Unit>

    @GET("api/plan-exercises/plan/{planId}")
    suspend fun  getPlanExercises(
        @Path("planId") planId: Long?
    ): Response<List<PlanExercisesResponse>>

    @DELETE("api/plan-exercises/{id}")
    suspend fun deletePlanExercise(
        @Path("id") id: Long
    ): Response<Unit>

    @POST("api/sessions")
    suspend fun createWorkoutSession(
        @Body request: CreateWorkoutSessionRequest
    ): Response<CreateWorkoutSessionResponse>

    @PUT("api/sessions/finish/{sessionId}")
    suspend fun finishWorkoutSession(
        @Body request: FinishSessionRequest,
        @Path("sessionId") sessionId: Long
    ): Response<Unit>

    @POST("api/sets")
    suspend fun addSetToSession(
        @Body request: SetRequest
    ): Response<SetResponse>

    @POST("api/session-exercises")
    suspend fun addExerciseToWorkoutSession(
        @Body request: SessionExerciseRequest
    ): Response<SessionExerciseResponse>

    @GET("api/sessions/user")
    suspend fun listSessions(): Response<List<SessionsResponse>>
}