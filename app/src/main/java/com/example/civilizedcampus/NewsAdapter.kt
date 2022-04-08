package com.example.civilizedcampus

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class NewsAdapter(activity: Activity, val resourceId: Int, data: List<News>) :ArrayAdapter<News>(activity,resourceId,data){
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(resourceId, parent, false)
        val title:TextView = view.findViewById(R.id.title)
        val info:TextView = view.findViewById(R.id.info)
        val publisher:TextView = view.findViewById(R.id.publisher)
        val date:TextView = view.findViewById(R.id.date)
        val time:TextView = view.findViewById(R.id.time)
        val image:ImageView = view.findViewById(R.id.newsImage)
        val news = getItem(position)
        if (news!=null){
            image.setImageResource(news.imageId)
            time.text = news.time
            date.text = news.date
            publisher.text = news.publisher
            info.text = news.info
            title.text = news.title
        }
        return view
    }
}