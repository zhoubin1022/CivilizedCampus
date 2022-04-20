package com.example.civilizedcampus

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.*
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class Login : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {

    var isRemember = false
    var isAuto = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCollector.addActivity(this)
        setContentView(R.layout.activity_login)
        //主页面自动登录
        val fill = intent.getBooleanExtra("data",false)
        if (fill){
            findViewById<EditText>(R.id.login_account).setText(intent.getStringExtra("username"))
            findViewById<EditText>(R.id.login_password).setText(intent.getStringExtra("password"))
            Toast.makeText(this,"已填充密码",Toast.LENGTH_SHORT).show()
        }
        //复选框设置监听事件
        val remember_box = findViewById<CheckBox>(R.id.remember)
        remember_box.setOnCheckedChangeListener(this)
        val auto_box = findViewById<CheckBox>(R.id.auto)
        auto_box.setOnCheckedChangeListener(this)

        //注册点击事件
        val register:TextView = findViewById(R.id.register)
        register.setOnClickListener{
            val intent = Intent(this, Register::class.java)
            startActivityForResult(intent, 1)

        }

        //登录点击事件
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
                        //保存密码
                        val editor = getSharedPreferences("remember", Context.MODE_PRIVATE).edit()
                        if(isRemember){
                            editor.putString("username", username)
                            editor.putString("password", password)
                            editor.putBoolean("remember", true)
                            if (isAuto){
                                editor.putBoolean("auto", true)
                            }
                        }
                        editor.apply()

                        val appData=application as LoginUser
                        appData.username=username

                        Log.d("login","success")
                        val intent = Intent(this@Login,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        //Toast.makeText(this@Login,result.message,Toast.LENGTH_SHORT).show()
                    }
                }
            })

        }
    }

    //注册页面返回的数据
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

    //两次返回才成功
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

    //复选框监听事件
    override fun onCheckedChanged(checkBox: CompoundButton?, checked: Boolean) {
        when(checkBox?.id){
            R.id.remember->{
                isRemember=true
            }
            R.id.auto->{
                isAuto=true
            }
        }
    }
}