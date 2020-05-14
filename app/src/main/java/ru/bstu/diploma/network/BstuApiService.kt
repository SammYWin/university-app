package ru.bstu.diploma.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://www.tu-bryansk.ru/education/schedule/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    //.addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface BstuApiService{
    @GET("schedule.ajax.php")
    fun getSchedule(@Query("namedata") namedata: String,
                    @Query("period") period: String,
                    @Query("group") group: String,
                    @Query("teacher") teacher: String): Deferred<Response<ResponseBody>>
}

object ScheduleApi{
    val retrofitService: BstuApiService by lazy {
        retrofit.create(BstuApiService::class.java)
    }
}