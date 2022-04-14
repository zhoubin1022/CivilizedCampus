package com.example.civilizedcampus

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat

class NewsAdapter(activity: Activity, private val resourceId: Int, data: List<News>) :ArrayAdapter<News>(activity,resourceId,data){
    @SuppressLint("ViewHolder", "SimpleDateFormat")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(resourceId, parent, false)
        val title:TextView = view.findViewById(R.id.title)
        val desc:TextView = view.findViewById(R.id.desc)
        val publishAccount:TextView = view.findViewById(R.id.publishAccount)
        val publishTime:TextView = view.findViewById(R.id.publishTime)
        val image:ImageView = view.findViewById(R.id.newsImage)
        val news = getItem(position)
        if (news!=null){
//            Log.d("img", news.imageUrl)
//            Thread {
//                val bitmap = ImageUtils.getBitmap(news.imageUrl)
//                if (bitmap == null) {
//                    Log.d("img", "图片加载错误")
//                }
//                (context as Activity).runOnUiThread {
//                    image.setImageBitmap(ImageUtils.compressImage(bitmap!!))
//                }
//
//            }.start()
            Glide.with(context).load(news.imageUrl).into(image)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            publishTime.text = sdf.format(news.publishTime)
            publishAccount.text = news.publishAccount
            desc.text = news.desc
            title.text = news.title

        }
        return view
    }

    private fun getHttpBitmap(url:String) : Bitmap? {
        return try {
            val imageUrl = URL(url)
//            val responseCode = imageUrl.openConnection().getHeaderField(0)
    //            if(responseCode.indexOf("200")<0){
    //                throw Exception("图片文件不存在或路径错误，错误代码：$responseCode")
    //            }
            val bitmap = BitmapFactory.decodeStream(imageUrl.openStream())
            val w = bitmap.width
            val h = bitmap.height
            Bitmap.createScaledBitmap(bitmap, (w*0.6).toInt(), (h*0.5).toInt(), true)
        }catch (e:Exception){
            e.printStackTrace()
            null
        }
    }
}