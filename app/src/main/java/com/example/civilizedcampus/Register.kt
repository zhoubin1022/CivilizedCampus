package com.example.civilizedcampus

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        ActivityCollector.addActivity(this)

        val register:Button = findViewById(R.id.confirm_register)
        register.setOnClickListener {
            val username = findViewById<EditText>(R.id.register_account).text.toString()
            val password = findViewById<EditText>(R.id.register_password).text.toString()
            val client = OkHttpClient()
            val request = Request.Builder().url("http://49.235.134.191:8080/user/save?account=$username&password=$password").build()
            client.newCall(request).enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    //Toast.makeText(this@Register,"网络错误", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body?.string()
                    val gson = Gson()
                    val result = gson.fromJson(responseData, Result::class.java)
                    if (result.code == 200){
                        val intent = Intent()
                        intent.putExtra("username",username)
                        intent.putExtra("password", password)
                        setResult(RESULT_OK, intent)
                        finish()
                    }else{
                        Toast.makeText(this@Register,result.message,Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }
}