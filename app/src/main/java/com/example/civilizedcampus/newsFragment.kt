package com.example.civilizedcampus

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView


class newsFragment : Fragment() {
    private val newsList = ArrayList<News>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_news, container, false)
        if(newsList.size==0) {
            initNews()
        }
        val adapter = this.context?.let { this.activity?.let { it1 -> NewsAdapter(it1, R.layout.news_item, newsList) } }
        view.findViewById<ListView>(R.id.newsList).adapter = adapter
        return view
    }

    private fun initNews() {
        newsList.add(News(
                "东楼的花",
                "美丽的风景藏匿于知识的海洋，最漂亮的花配上最勤奋的人",
                "lm",
                "2022-3-20",
                "12:00",R.drawable.img_1))
        newsList.add(News(
                "福大下雪了！！",
                "震惊！在福大校园里竟发生这样的事...",
                "dy",
                "2022-3-19",
                "12:00",R.drawable.img_2))
        newsList.add(News(
                "国王排名",
                "来看看大家对国王排名的评价吧",
                "zb",
                "2022-3-18",
                "12:00",R.drawable.img_3))
        newsList.add(News(
                "正确的",
                "我们应该保持一个良好的心态",
                "bc",
                "2022-3-17",
                "12:00",R.drawable.img_4))
    }
}