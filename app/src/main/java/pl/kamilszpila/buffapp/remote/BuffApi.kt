package pl.kamilszpila.buffapp.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface BuffApi {

    @GET("buffs/{id}")
    fun getBuff(@Path("id") id: Int): Call<BuffResponse>

}