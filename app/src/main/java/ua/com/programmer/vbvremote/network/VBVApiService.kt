package ua.com.programmer.vbvremote.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import ua.com.programmer.vbvremote.BuildConfig

const val STATUS_OK = "success"
const val STATUS_ERROR = "fail"

enum class Event {STATUS, START, STOP, PAUSE}

fun eventToString(event: Event): String {
    return when (event) {
        Event.STATUS -> "status"
        Event.START -> "start"
        Event.PAUSE -> "pause"
        Event.STOP -> "stop"
    }
}

class VBVApi(baseUrl: String) {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = if (BuildConfig.DEBUG) {
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    } else {
        OkHttpClient.Builder()
            .build()
    }

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(client)
        .baseUrl(baseUrl)
        .build()

    val retrofitService: VBVApiService by lazy {
        retrofit.create(VBVApiService::class.java)
    }

}

interface VBVApiService {
    @POST("getorder")
    suspend fun barcode(@Body body: BarcodeRequest): BarcodeResponse
    @POST("auth")
    suspend fun authenticate(@Body body: AuthRequest): AuthResponse
    @POST("cutwork")
    suspend fun documents(@Body body: DocumentRequest): DocumentResponse
}