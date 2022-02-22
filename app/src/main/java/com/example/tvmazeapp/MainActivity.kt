package com.example.tvmazeapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

/**
 * Created by Geethu T H on 19-01-2022.
 */
class MainActivity : AppCompatActivity() {
    var url: String = "https://api.tvmaze.com/search/shows?q=value"
    var stringInput = ""
    val nameList = ArrayList<String>()
    lateinit var recyclerview: RecyclerView//lateinit variables values initialized after the declaration
    lateinit var editText: EditText
    lateinit var noListField: TextView
    lateinit var adapter: CustomAdapter
    lateinit var activity: Activity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activity = this
        // getting the recyclerview by its id
        recyclerview = findViewById(R.id.namelist)
        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)
        // This will pass the ArrayList to our Adapter
        adapter = CustomAdapter(nameList)
        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter

        // the item search field
        editText = findViewById(R.id.searchString)
        noListField = findViewById(R.id.noList)


        // search the item typed
        val search = findViewById<LinearLayout>(R.id.search)
        search.setOnClickListener()
        {
            println("abc: " + editText.text.toString())
            if (editText.text != null && !editText.text.toString().isEmpty()) {

                searchItem()
            } else {

                editText.error = "Please type something"
            }
        }


    }

    @SuppressLint("ServiceCast")
    private fun searchItem() {
        stringInput = editText.text.toString()
        url = url.replace("value", stringInput)

        if (isNetworkAvailable(activity)) {// checking network connection availability
            tvMazeApiCall(url)
        } else {
            Toast.makeText(activity, "Please check your network connectivity.", Toast.LENGTH_SHORT)
                .show()
        }

        url = "https://api.tvmaze.com/search/shows?q=value"
    }

    private fun tvMazeApiCall(url: String) {
        nameList.clear()

        val queue = Volley.newRequestQueue(this)

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                println("Response is: $response")
                if (response.equals("[]")) {
                    noListField.isVisible = true
                } else {
                    noListField.isVisible = false
                    val jsonArr = JSONArray(response)
                    for (i in 0 until jsonArr.length()) {
                        val book = jsonArr.getJSONObject(i)
                        nameList.add(book.getJSONObject("show")["name"].toString())
                    }

                    // pass the ArrayList to Adapter
                    adapter = CustomAdapter(nameList)
                    if (recyclerview != null) {
                        recyclerview.layoutManager = LinearLayoutManager(this)
                        recyclerview.adapter = adapter
                    }
                }
            },
            {
                println("That didn't work!")
                noListField.isVisible = true
            })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    // method to check network connection availability
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }
}