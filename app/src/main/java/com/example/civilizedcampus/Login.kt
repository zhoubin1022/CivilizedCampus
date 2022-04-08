package com.example.civilizedcampus

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCollector.addActivity(this)
        setContentView(R.layout.activity_login)
        val register:TextView = findViewById(R.id.register)
        register.setOnClickListener{
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
        val login:Button = findViewById(R.id.login)
        login.setOnClickListener {
            val username = findViewById<EditText>(R.id.login_account).text.toString()
            val password = findViewById<EditText>(R.id.login_password).text.toString()
            if (isCorrect(username=username,password=password)){
                val editor = getSharedPreferences("remember", Context.MODE_PRIVATE).edit()
                editor.putString("username", username)
                editor.putString("password", password)
                editor.putBoolean("logged", true)
                editor.apply()
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this,"密码错误",Toast.LENGTH_SHORT).show()
            }
        }
        val find:TextView = findViewById(R.id.find_password)
        find.setOnClickListener {
            Toast.makeText(this,"该功能暂未上架",Toast.LENGTH_LONG).show()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }
    private var exitTime:Long = 0
//    override fun onBackPressed() {
//        if (System.currentTimeMillis()-exitTime>2000){
//            Toast.makeText(this,"再按一次退出程序",Toast.LENGTH_SHORT).show()
//            exitTime = System.currentTimeMillis()
//        }else{
//            ActivityCollector.finishAll()
//        }
//        super.onBackPressed()
//    }



    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event?.action == KeyEvent.ACTION_DOWN){
            if (System.currentTimeMillis()-exitTime>2000){
                Toast.makeText(this,"再按一次退出程序",Toast.LENGTH_SHORT).show()
                exitTime = System.currentTimeMillis()
            }else{
                ActivityCollector.finishAll()
            }
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    private fun isCorrect(username:String, password:String):Boolean{
        if (username=="admin"&&password=="123456") return true
        return false
    }
}