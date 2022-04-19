package com.example.civilizedcampus

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SubmitActivity : AppCompatActivity() , View.OnClickListener{
    private var dialog:MyDialog?=null
    private val TAKE_PICTURE=0
    private val CHOOSE_PICTURE=1
    var imageUrl = ""
    var imageUri:Uri?=null
    val tag = "Tag"
    private val permissions = arrayOf(
        "Manifest.permission.CAMERA",
        "Manifest.permission.READ_EXTERNAL_STORAGE",
        "Manifest.permission.WRITE_EXTERNAL_STORAGE",
        "Manifest.permission.MANAGE_EXTERNAL_STORAGE")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit)
        val imageView = this.findViewById<ImageView>(R.id.iv_choose_pictures)
        imageView.setOnClickListener {
            RequestPermissions()
            dialog = MyDialog(this)
            dialog!!.show()
            dialog!!.window?.findViewById<TextView>(R.id.camera)?.setOnClickListener(this)
            dialog!!.window?.findViewById<TextView>(R.id.photo)?.setOnClickListener(this)
            dialog!!.window?.findViewById<TextView>(R.id.cancel)?.setOnClickListener(this)

        }
    }

    private fun RequestPermissions(){
        for (p in permissions){
            if(ContextCompat.checkSelfPermission(this, p)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, permissions,2)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            2->{
                for(p in grantResults){
                    if(grantResults.isNotEmpty()){
                        if (p==PackageManager.PERMISSION_GRANTED){
                            Log.d(tag, "onRequestPermissionsResult: 成功申请到$p")
                        }else{
                            //Toast.makeText(this, "${p}申请失败", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }
        }
    }

    override fun onClick(v: View?) {
        if(v == null) {
            Log.d("dialog", "null")
            return
        }
        when(v.id){
            R.id.camera-> {
                Log.d("dialog", "camera")
                val outImage = File(externalCacheDir, "outPut_image.jpg")
                try {
                    if(outImage.exists()){
                        outImage.delete()
                    }
                    outImage.createNewFile()
                }catch (e:Exception){
                    e.printStackTrace()
                }
                if(Build.VERSION.SDK_INT>=24){
                    imageUri = FileProvider.getUriForFile(this,"com.example.civilizedcampus.fileProvider", outImage)
                }else{
                    imageUri = Uri.fromFile(outImage)
                }
                val openCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(openCameraIntent, TAKE_PICTURE)
                dialog?.dismiss()
            }

            R.id.photo -> {
                Log.d("dialog", "photo")
                val openAlbumIntent = Intent(Intent.ACTION_GET_CONTENT)
                openAlbumIntent.type="image/*"
                startActivityForResult(openAlbumIntent, CHOOSE_PICTURE)
                dialog?.dismiss()
            }
            R.id.cancel -> {
                Log.d("dialog", "cancel")
                dialog?.dismiss()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== RESULT_OK){
            when(requestCode){
                CHOOSE_PICTURE ->{
                    if (data!=null){
                        if (Build.VERSION.SDK_INT>=19){
                            handleImageONKitKat(data)
                        }else{
                            handleImageBeforeKitKat(data)
                        }
                        val image=this.findViewById<ImageView>(R.id.iv_choose_pictures)
                        Glide.with(this).load(imageUrl).into(image)
                    }
                }

                TAKE_PICTURE ->{
                    try {
                        val bitmap = BitmapFactory.decodeStream(imageUri?.let {
                            contentResolver.openInputStream(
                                it
                            )
                        })
                        Log.d(tag,"take success")
                        imageUrl = uploadImage(saveFile(bitmap))
                        val image=this.findViewById<ImageView>(R.id.iv_choose_pictures)
                        Glide.with(this).load(imageUrl).into(image)
                        Log.d(tag,"imageUrl=$imageUrl")
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun handleImageONKitKat(data:Intent){
        val uri = data.data
        var imagePath:String? = null
        Log.d(tag,"uri=$uri")
        if (uri==null){
            Log.d(tag,"uri wrong")
        }else{
            if(DocumentsContract.isDocumentUri(this,uri)){
                val docId = DocumentsContract.getDocumentId(uri)
                Log.d(tag,"docId=$docId")
                if("com.android.providers.media.documents" == uri.authority){
                    val id = docId.split(":")[1]
                    Log.d(tag,"id=$id")
                    val selection = "${MediaStore.Images.Media._ID}=$id"
                    Log.d(tag,"selection=$selection")
                    imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection)
                    Log.d(tag,"imagePath=$imagePath")
                }else if("com.android.providers.downloads.documents"==uri.authority){
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        docId.toLong(),
                    )
                    Log.d(tag,"contentUri=$contentUri")
                    imagePath=getImagePath(contentUri,null)
                }
            }else if ("content".equals(uri.scheme,true)){
                imagePath = getImagePath(uri,null)
            }else if ("file".equals(uri.scheme,true)){
                imagePath = uri.path
            }
            val file = imagePath?.let { saveFile(it) }
            if (file!=null){
                imageUrl=uploadImage(file)
                Log.d(tag,"imageUrl=$imageUrl")
            }
        }

    }

    fun handleImageBeforeKitKat(data:Intent){
        val uri = data.data
        val file = uri?.let { getImagePath(it,null)?.let { saveFile(it) } }
        if (file!=null){
            imageUrl=uploadImage(file)
            Log.d(tag,"imageUrl=$imageUrl")
        }
    }

    @SuppressLint("Range")
    private fun getImagePath(uri: Uri, Selection:String?):String?{
        var path:String? = null
        val cursor = contentResolver.query(uri, null, Selection, null,null)
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path
    }

    private fun saveFile(imagePath:String):File?{
        if(imagePath!=null){
            val bitmap = BitmapFactory.decodeFile(imagePath)
            val file = File("${Environment.getExternalStorageDirectory().absolutePath}/image.jpg")
            if (file.exists()){
                file.delete()
            }
            try {
                val bos = BufferedOutputStream(FileOutputStream(file))
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                bos.flush()
                bos.close()
            }catch (e:IOException){
                e.printStackTrace()
            }
            return file
        }else{
            return null
        }
    }

    private fun saveFile(bitmap: Bitmap):File{
        val file = File("${Environment.getExternalStorageDirectory().absolutePath}/image.jpg")
        if (file.exists()){
            file.delete()
        }
        try {
            val bos = BufferedOutputStream(FileOutputStream(file))
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            bos.flush()
            bos.close()
        }catch (e:IOException){
            e.printStackTrace()
        }
        return file
    }

    private fun uploadImage(file: File):String{
        val client = OkHttpClient()
        var result=""
        val url = "http://49.235.134.191:8080/file/image/upload"
        val image = file.asRequestBody("image/jpg".toMediaTypeOrNull())
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, image)
            .build()
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.d(tag,"网络错误")
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.body!=null){
                    Log.d(tag,"upload success")
                    val jsonObject = JSONObject(response.body!!.string())
                    result = jsonObject.optString("data")
                }
            }

        })
        Thread.sleep(1000)
        return result
    }
}