package com.example.civilizedcampus

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView

class MyDialog(context: Context) :Dialog(context){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = View.inflate(context, R.layout.bottom_dialog,null)
//        val camera = view.findViewById<TextView>(R.id.camera)
//        val photo = view.findViewById<TextView>(R.id.photo)
//        val cancel = view.findViewById<TextView>(R.id.cancel)
//        camera.setOnClickListener(this)
//        photo.setOnClickListener(this)
//        cancel.setOnClickListener(this)
        this.setContentView(view)
        val dialogWindow = this.window
        dialogWindow?.setGravity(Gravity.BOTTOM)
        dialogWindow?.setBackgroundDrawableResource(R.drawable.bottom_picture_bg)
        val lp = dialogWindow?.attributes
        lp?.width=WindowManager.LayoutParams.MATCH_PARENT
        lp?.y = 40
        dialogWindow?.attributes=lp
        //this.show()
    }


}