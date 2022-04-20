package com.example.civilizedcampus

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() , View.OnClickListener{
    private var fg1: newsFragment? = null
    private var fg2: PhotoFragment? = null
    private var fg3: MyFragment? = null

    private var news: TextView? = null
    private var takephotos: TextView? = null
    private var homepage: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCollector.addActivity(this)
        fg1 = newsFragment()
        fg1?.let { replaceFragment(it) }
        setContentView(R.layout.activity_main)
        bindViews()

        val prefs = getSharedPreferences("remember", Context.MODE_PRIVATE)
        val logged = prefs.getBoolean("logged", false)
        if (logged){
            (application as LoginUser).username= prefs.getString("username","").toString()
            Toast.makeText(this,"已自动登录",Toast.LENGTH_LONG).show()
        }else{
            val intent = Intent(this,Login::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }

    private fun bindViews() {
       news = findViewById<TextView>(R.id.txt_news)
       takephotos = findViewById<TextView>(R.id.txt_takephotos)
       homepage = findViewById<TextView>(R.id.txt_homepage)

       news?.setOnClickListener(this)
       takephotos?.setOnClickListener(this)
       homepage?.setOnClickListener(this)
    }

    private fun setSelected(){
        news?.isSelected = false
        takephotos?.isSelected = false
        homepage?.isSelected = false
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.content, fragment)
        transaction.commit()
    }

    override fun onClick(v: View?) {
        if(v == null) return
        when(v.id){
            R.id.txt_news -> {
                setSelected()
                news?.isSelected =true
                if (fg1 == null){
                    fg1 = newsFragment()
                }
                fg1?.let { replaceFragment(it) }
            }
            R.id.txt_takephotos -> {
                setSelected()
                takephotos?.isSelected=true
                if (fg2 == null){
                    fg2 = PhotoFragment()
                }
                fg2?.let { replaceFragment(it) }
            }
            R.id.txt_homepage -> {
                setSelected()
                homepage?.isSelected=true
                if (fg3 == null){
                    fg3 = MyFragment("The three Fragment")
                }
                fg3?.let { replaceFragment(it) }
            }
        }
    }
}