package com.coherentsolutions.by.max.sir.androidtrainingtasks.network

import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities.PetResponse
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities.UserResponse
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
        "Platform: android"
    )
    @POST(value = "user")
    fun createUser(
        @Header(value = "/authorization")
        apiKey: String,
        @Body
        userResponse: UserResponse
    ): Call<ServerStatusResponse>


    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
        "Platform: android"
    )
    @DELETE(value = "user/{username}")
    fun deleteUser(
        @Header(value = "/authorization")
        apiKey: String,
        @Path(value = "username")
        username: String
    ): Call<ServerStatusResponse>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
        "Platform: android"
    )
    @GET(value = "user/{username}")
    fun getUser(
        @Header(value = "/authorization")
        apiKey: String,
        @Path(value = "username")
        username: String
    ): Call<UserResponse?>


    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
        "Platform: android"
    )
    @GET(value = "pet/findByStatus")
    fun getPetsByStatus(
        @Query(value = "status")
        status: String
    ): Deferred<List<PetResponse>>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
        "Platform: android"
    )
    @POST(value = "pet/{petId}/uploadImage")
    fun uploadImage(
        @Header(value = "/authorization")
        apiKey: String,
        @Path(value = "petId")
        petId: Long,
        @Body requestBody: RequestBody
    ): Deferred<ServerStatusResponse>


}