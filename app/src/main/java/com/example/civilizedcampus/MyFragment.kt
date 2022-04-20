package com.example.civilizedcampus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

class MyFragment(val text:String) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as MainActivity).findViewById<View>(R.id.title_bar).visibility=View.GONE
        val view = inflater.inflate(R.layout.fg_content, container, false)
        val text_content = view.findViewById<TextView>(R.id.txt_content)
        //Toast.makeText(context,text,Toast.LENGTH_SHORT).show()
        text_content.text = text
        return view
    }

}