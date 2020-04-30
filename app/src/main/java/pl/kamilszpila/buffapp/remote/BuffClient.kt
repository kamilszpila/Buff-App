package pl.kamilszpila.buffapp.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log
import androidx.lifecycle.MutableLiveData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.kamilszpila.buff.model.Buff

class BuffClient {

    companion object {
        const val TAG = "BuffClient"
        const val BASE_URL = "https://buffup.proxy.beeceptor.com/"
    }

    private val buffApi by lazy { createApiService() }
    val buffData = MutableLiveData<Buff>()

    private fun createApiService(): BuffApi {

        val interceptor = HttpLoggingInterceptor()
            .apply { level = HttpLoggingInterceptor.Level.BODY }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        return retrofit.create(BuffApi::class.java)
    }

    fun fetchBuffData(id: Int) {
        buffApi.getBuff(id).enqueue(object : Callback<BuffResponse> {
            override fun onFailure(call: Call<BuffResponse>, t: Throwable) {
                Log.d(TAG, "onFailure: ")
                t.printStackTrace()
            }

            override fun onResponse(call: Call<BuffResponse>, response: Response<BuffResponse>) {
                if (!response.isSuccessful) {
                    Log.d(TAG, "onResponse: not success")
                    return
                }

                val buff = response.body()?.result
                buff?.let {
                    Log.d(TAG, "onResponse: buff not null")
                    buffData.postValue(it)
                    return
                }
            }

        })
    }

}