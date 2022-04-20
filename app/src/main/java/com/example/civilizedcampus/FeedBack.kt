package com.example.civilizedcampus


import android.annotation.SuppressLint
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class FeedBack(val id: Long=0, private val imageUrl: String, private val title: String,private val desc: String,
               private val account: String, private val address: String, private val category: String,
               private val degree:Int,  private val time: Date,private var process: String="已提交") {

     @SuppressLint("SimpleDateFormat")
     fun toJson():JSONObject{
         val jsonObject = JSONObject()
         jsonObject.put("imageUrl",imageUrl)
         jsonObject.put("title",title)
         jsonObject.put("desc",desc)
         jsonObject.put("account",account)
         jsonObject.put("address",address)
         jsonObject.put("category",category)
         jsonObject.put("degree",degree)
         //jsonObject.put("time",time.toString())
         val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
         val str = dateFormat.format(time)
         jsonObject.put("time",str)
         jsonObject.put("process",process)
         return jsonObject
    }
}