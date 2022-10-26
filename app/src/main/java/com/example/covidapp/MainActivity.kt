package com.example.covidapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.covidapp.data.CovidData
import com.example.covidapp.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface CovidApiService {
    @GET("statistics?country=Italy")
    suspend fun covidDetails(): CovidData
}
class CovidResult : Any()

const val API_AUTHORIZATION_HEADER = "x-rapidapi-key"

class AuthorizationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val newRequest = request.newBuilder().addHeader(
            API_AUTHORIZATION_HEADER,
            "0c44e04580mshb128ce6f02906b1p182840jsn1187ec84e6e8"
        ).build()

        val val1 = chain.proceed(newRequest)
        Log.v("MainActivity", "The new request is: ${val1}")
        return val1
    }

}

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    val logging = HttpLoggingInterceptor()
    val authorization = AuthorizationInterceptor()
    val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(authorization)
        .build()

    val retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl("https://covid-193.p.rapidapi.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val covidApiService = retrofit.create(CovidApiService::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        logging.level = HttpLoggingInterceptor.Level.BODY
        retrieveCovid()




    }

    fun retrieveCovid() {

        lifecycleScope.launch {
            try {
                val details = covidApiService.covidDetails()
                showCovidDetails(details)
            } catch (e: Exception) {
                Log.e("MainActivity", "error retrieving countries: $e")
                Snackbar.make(
                    findViewById(R.id.main_view),
                    "Error retrieving countries",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("Retry") { retrieveCovid() }.show()
            }
        }
    }

    fun showCovidDetails(covidResult: CovidData) {
        Log.d("MainActivity", "list of details received: ${covidResult.response.get(0).cases.active}")
        Toast.makeText(this, "downloaded details", Toast.LENGTH_LONG).show()
        binding.tvNewCases.text = getString(R.string.tv_new_cases, covidResult.response.get(0).cases.new)
        binding.tvActiveCases.text = getString(R.string.tv_active_cases, covidResult.response.get(0).cases.active)
        binding.tvCriticalCases.text = getString(R.string.tv_critical_cases, covidResult.response.get(0).cases.critical)

    }
}