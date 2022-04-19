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
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
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
    var mLocationClient:AMapLocationClient? = null
    private val mapLoactionListener = AMapLocationListener { location ->
        if(location!=null){
            if(location.errorCode==0){
                val text = location.address
                val position = findViewById<TextView>(R.id.tv_position)
                position.text=text
            }else{
                Log.d(tag,"ErrorCode:${location.errorCode},ErrorInfo:${location.errorInfo},detail:${location.locationDetail}")
            }
        }else{
            val position = findViewById<TextView>(R.id.tv_position)
            position.text="定位失败"
        }
    }

    private val permissions = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.ACCESS_FINE_LOCATION)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit)
        RequestPermissions()

        AMapLocationClient.updatePrivacyShow(applicationContext,true,true)
        AMapLocationClient.updatePrivacyAgree(applicationContext,true)
        mLocationClient= AMapLocationClient(applicationContext)
        mLocationClient?.setLocationListener(mapLoactionListener)

        val mLocationOption = AMapLocationClientOption()
        mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        mLocationOption.locationPurpose = AMapLocationClientOption.AMapLocationPurpose.SignIn
        mLocationOption.isOnceLocation = true
        mLocationClient?.setLocationOption(mLocationOption)
        mLocationClient?.startLocation()


        val imageView = this.findViewById<ImageView>(R.id.iv_choose_pictures)
        imageView.setOnClickListener {
            dialog = MyDialog(this)
            dialog!!.show()
            dialog!!.window?.findViewById<TextView>(R.id.camera)?.setOnClickListener(this)
            dialog!!.window?.findViewById<TextView>(R.id.photo)?.setOnClickListener(this)
            dialog!!.window?.findViewById<TextView>(R.id.cancel)?.setOnClickListener(this)
        }
    }

    private fun RequestPermissions(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            var toRequest = false
            for (p in permissions){
                if(ContextCompat.checkSelfPermission(this, p)!=PackageManager.PERMISSION_GRANTED){
                    toRequest=true

                }
            }
            if(toRequest){
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
                if(grantResults.isNotEmpty()){
                    for (i in 0 until grantResults.size-1){
                        if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                            Log.d(tag, "成功申请到${permissions[i]}")
                        }else{
                            Toast.makeText(this, "${permissions[i]}申请失败", Toast.LENGTH_SHORT).show()
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
            R.id.bt_release -> {

            }
            R.id.iv_back ->{
                finish()
            }
        }
    }

    private fun checkIsNull():Boolean{
        val title = this.findViewById<EditText>(R.id.et_input_title)
        val desc = this.findViewById<EditText>(R.id.et_input_depiction)
        return true
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== RESULT_OK){
            when(requestCode){
                CHOOSE_PICTURE ->{
                    if (data!=null){
                        handleImageONKitKat(data)
                        val image=this.findViewById<ImageView>(R.id.iv_choose_pictures)
                        if(imageUrl!="")
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
                        if(imageUrl!="")
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
                Log.d(tag,"auth=${uri.authority}")
                Log.d(tag,"scheme=${uri.scheme}")
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
            Log.d(tag,"imagePath=$imagePath")
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
            //Log.d(tag,cursor.toString())
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path
    }

    private fun saveFile(imagePath:String):File{
        val bitmap = BitmapFactory.decodeFile(imagePath)
        return saveFile(bitmap)
    }

    private fun saveFile(bitmap: Bitmap):File{
        val filepath = applicationContext.filesDir.absolutePath
        val fileParent = File(filepath)
        if(!fileParent.exists()){
            fileParent.mkdirs()
        }
        Log.d(tag,filepath)
        val file = File("$filepath/image.jpg")
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
        Log.d(tag,"filename=${file.absolutePath}")
        return file
    }

    private fun uploadImage(file: File):String{
        val client = OkHttpClient()
        var result=""
        val url = "http://49.235.134.191:8080/file/image/upload"
        val image = file.asRequestBody("image/*".toMediaTypeOrNull())
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
                Log.d(tag,e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.body!=null){
                    Log.d(tag,"upload success")
                    val jsonObject = JSONObject(response.body!!.string())
                    result = jsonObject.optString("data")
                }
            }

        })
        Thread.sleep(3000)
        return result
    }

    override fun onDestroy() {
        super.onDestroy()
        mLocationClient?.stopLocation()
    }
}