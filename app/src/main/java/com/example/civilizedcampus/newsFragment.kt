package com.example.civilizedcampus

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException


class newsFragment : Fragment() {
    private val newsList = ArrayList<News>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as MainActivity).findViewById<View>(R.id.title_bar).visibility=View.VISIBLE
        val view = inflater.inflate(R.layout.fragment_news, container, false)
        if(newsList.size==0) {
            initNews()
        }
        val adapter = this.context?.let { this.activity?.let { it1 -> NewsAdapter(it1, R.layout.news_item, newsList) } }
        view.findViewById<ListView>(R.id.newsList).adapter = adapter
        return view
    }

    private fun initNews() {
        val client = OkHttpClient()
        val request = Request.Builder().url("http://49.235.134.191:8080/news/get").build()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                //Toast.makeText(context,"网络错误", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call, response: Response) {

                val responseData = response.body?.string()
                val gson = Gson()
                val result = gson.fromJson(responseData, Result::class.java)
                if (result.code == 200){
                    val typeOf = object : TypeToken<List<News>>(){}.type
                    val news = gson.fromJson<List<News>>(gson.toJson(result.data).toString(), typeOf)
                    newsList.addAll(news)
                }else{
                    Toast.makeText(context,result.message, Toast.LENGTH_SHORT).show()
                }
            }

        })
    }
}