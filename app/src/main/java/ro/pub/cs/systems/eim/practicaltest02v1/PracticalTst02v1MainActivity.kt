package ro.pub.cs.systems.eim.practicaltest02v1

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class SuggestionResponse(
    val query: String,          // Maps the first element of the array
    val suggestions: List<String> // Maps the second element of the array
)

interface ApiService {
    @GET("complete/search")
    fun getSuggestions(
        @Query("client") client: String,
        @Query("q") query: String
    ): Call<SuggestionResponse>
}

object RetrofitInstance {
    private const val BASE_URL = "https://www.google.com/"

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(SuggestionResponse::class.java, SuggestionResponseDeserializer())
        .create()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}

class PracticalTst02v1MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practical_test02v1_main)

        val prefixEditText = findViewById<EditText>(R.id.prefix)
        val autocompleteBtn = findViewById<Button>(R.id.autocomplete)
        val suggestionsTextView = findViewById<TextView>(R.id.suggestions)

        autocompleteBtn.setOnClickListener {
            val prefix = prefixEditText.text.toString()
            Log.d("PracticalTest02v1", "Prefix: $prefix")

            RetrofitInstance.apiService.getSuggestions("chrome", prefix)
                .enqueue(object : Callback<SuggestionResponse> {
                    override fun onResponse(
                        call: Call<SuggestionResponse>,
                        response: Response<SuggestionResponse>
                    ) {
                        if (response.isSuccessful) {
                            val suggestions = response.body()?.suggestions ?: emptyList()
                            suggestionsTextView.text = suggestions.joinToString("\n")
                            // Get the 3rd suggestion and Log it
                            if (suggestions.size >= 3) {
                                Log.d("PracticalTest02v1", "3rd suggestion: ${suggestions[2]}")
                            }
                        } else {
                            suggestionsTextView.text = "Error: ${response.code()}"
                        }
                    }

                    override fun onFailure(call: Call<SuggestionResponse>, t: Throwable) {
                        Log.e("PracticalTest02v1", "Network request failed: ${t.message}")
                        suggestionsTextView.text = "Failed to fetch suggestions."
                    }
                })
        }
    }
}
