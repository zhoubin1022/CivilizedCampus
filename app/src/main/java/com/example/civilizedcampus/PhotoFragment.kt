package com.example.civilizedcampus

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class PhotoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as MainActivity).findViewById<View>(R.id.title_bar).visibility=View.GONE
        return inflater.inflate(R.layout.fragment_photo, container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imageView = activity?.findViewById<ImageView>(R.id.iv_take_pictures)
        imageView?.setOnClickListener {
            val intent = Intent(activity, SubmitActivity::class.java)
            startActivity(intent)
        }
    }
}