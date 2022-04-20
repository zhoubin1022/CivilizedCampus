package com.example.civilizedcampus

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCollector.addActivity(this)
        setContentView(R.layout.activity_login)
        val register:TextView = findViewById(R.id.register)
        register.setOnClickListener{
            val intent = Intent(this, Register::class.java)
            startActivityForResult(intent, 1)

        }
        val login:Button = findViewById(R.id.login)
        login.setOnClickListener {
            Log.d("login", "login")
            val username = findViewById<EditText>(R.id.login_account).text.toString()
            val password = findViewById<EditText>(R.id.login_password).text.toString()
            val client = OkHttpClient()
            val request = Request.Builder().url("http://49.235.134.191:8080/user/login?account=$username&password=$password").build()
            client.newCall(request).enqueue(object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    Log.d("login","error")
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body?.string()
                    val gson = Gson()
                    val result = gson.fromJson(responseData, Result::class.java)
                    if (result.code == 200){
                        val editor = getSharedPreferences("remember", Context.MODE_PRIVATE).edit()
                        editor.putString("username", username)
                        editor.putString("password", password)
                        editor.putBoolean("logged", true)
                        editor.apply()

                        val appData=application as LoginUser
                        appData.username=username

                        Log.d("login","success")
                        val intent = Intent(this@Login,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(this@Login,result.message,Toast.LENGTH_SHORT).show()
                    }
                }

            })

        }
        val find:TextView = findViewById(R.id.find_password)
        find.setOnClickListener {
            Toast.makeText(this,"该功能暂未上架",Toast.LENGTH_LONG).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            1 -> if(resultCode == RESULT_OK){
                val username = data?.getStringExtra("username")
                val password = data?.getStringExtra("password")
                findViewById<EditText>(R.id.login_account).setText(username)
                findViewById<EditText>(R.id.login_password).setText(password)
                Toast.makeText(this,"已填充密码",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }

    private var exitTime:Long = 0
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
}