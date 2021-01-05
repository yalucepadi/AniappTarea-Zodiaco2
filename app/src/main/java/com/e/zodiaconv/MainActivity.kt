package com.e.zodiaconv

import APIService
import RestApiService
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.e.zodiaconv.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory
import com.squareup.picasso.RequestCreator
import timber.log.Timber
import java.io.*
import java.util.*
import javax.security.auth.login.LoginException
import javax.xml.datatype.DatatypeConstants.MONTHS
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.JsonReader
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.JsonParser.parseString

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.*
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    val dias: Int = 0
    val signos = arrayOf(
        R.drawable.capricornio,
        R.drawable.aquario,
        R.drawable.piscis,
        R.drawable.aries,
        R.drawable.tauro,
        R.drawable.geminis,
        R.drawable.cancer,
        R.drawable.leo,
        R.drawable.ophiuchus,
        R.drawable.libra,
        R.drawable.escorpio,
        R.drawable.virgo,
        R.drawable.sagitario
    )

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setUpView()

    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun setUpView() {


        binding.etPlannedDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


            val dpd = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, mYear: Int, mMonth: Int, mDay: Int ->
                    val nMonth1 = mMonth + 1
                    binding.etPlannedDate.setText("$mYear/$nMonth1/$mDay")
                    //Toast.makeText(this,"the month: $nMonth1 and day $mDay ",Toast.LENGTH_LONG).show()
                    calendario(nMonth1, mDay)
                },
                year,
                month,
                day
            )
            dpd.show()


        }
        binding.outlinedButtonRefrescar.setOnClickListener {

            binding.etPlannedDate.setText("")
            binding.tvSigno.setText("")
            binding.tvPrediccion.setText("")
            binding.imageViewZodiaco.setImageResource(0)



        }

        binding.imageViewFacebook.setOnClickListener {

            validar()


        }
        binding.imageViewGoogle.setOnClickListener {

            validar()


        }
        binding.imageViewInstagram.setOnClickListener {

            validar()

        }


    }

    private  fun inicializar(): Boolean{
        val r: Boolean


        val x:Boolean=true
       if (x==true){

           binding.imageViewZodiaco.setImageResource(R.drawable.transparente)
       }
             r=when(x){
               true->{true

               }

            else -> false}
            return r
    }
    private fun validar() {
val imz:Boolean=true
        if (binding.etPlannedDate.text.isEmpty() && imz==inicializar()  ) {
            Toast.makeText(this, "Seleccione su fecha de nacimiento", Toast.LENGTH_LONG).show()

        } else {
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {

                // En esta parte si deseas puedes mostrar una explicación al usuario porque le solicitas permisos a su carpeta imágenes
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this@MainActivity,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {

                    // Si no das una explicación en esta parte puedes pedir permisos sin problemas
                } else {


                }
            }
            compartirImagen()
            loadDataSign()

        }
    }

    private fun hallarSigno(): Bitmap {

        val r: Bitmap

        r = when (binding.tvSigno.text) {

            "capricorn" -> BitmapFactory.decodeResource(resources, signos[0])
            "aquarius" -> BitmapFactory.decodeResource(resources, signos[1])
            "pisces" -> BitmapFactory.decodeResource(resources, signos[2])
            "aries" -> BitmapFactory.decodeResource(resources, signos[3])
            "taurus" -> BitmapFactory.decodeResource(resources, signos[4])
            "gemini" -> BitmapFactory.decodeResource(resources, signos[5])
            "cancer" -> BitmapFactory.decodeResource(resources, signos[6])
            "leo" -> BitmapFactory.decodeResource(resources, signos[7])
            "virgo" -> BitmapFactory.decodeResource(resources, signos[11])
            "libra" -> BitmapFactory.decodeResource(resources, signos[9])
            "scorpio" -> BitmapFactory.decodeResource(resources, signos[10])
            "Ophiuchus" -> BitmapFactory.decodeResource(resources, signos[8])
            "sagittarius" -> BitmapFactory.decodeResource(resources, signos[12])

            else -> BitmapFactory.decodeResource(resources,R.drawable.transparente)
        }



        return r
    }

    fun compartirImagen() {


        val gf = hallarSigno()
        // con ACTION_SEND enviamos nuestra imagen a otras aplicaciones para ser compartida en ellas
        val compartirgf = Intent(Intent.ACTION_SEND)

        // Indicamos el formato de la imagen Gelatina de Fresa
        compartirgf.type = "image/jpeg"
        compartirgf.type = "text/plain"

        val texto = binding.tvSigno.getText()
        compartirgf.putExtra(Intent.EXTRA_TEXT, texto)
        compartirgf.putExtra(Intent.EXTRA_SUBJECT, "Enviado desde mi app")
        // Con la clase ContentValues() almacenamos los valores que nuestra imagen va compartir en
        // las aplicaciones, definimos un nombre, formato de la imagen
        val valoresgf = ContentValues()
        valoresgf.put(MediaStore.Images.Media.TITLE, "Imagen de signo")
        valoresgf.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")


        // Con la clase contentResolver() Proporcionamos el acceso de las aplicaciones a nuestra Galería de imágenes
        // a contenido externo con EXTERNAL_CONTENT_URI
        val urigf = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            valoresgf
        )


        val outstreamgf: OutputStream?
        try {
            outstreamgf = contentResolver.openOutputStream(urigf!!)

            // Optimizamos la imagen JPEG que será compartida, con calidad 100
            gf.compress(Bitmap.CompressFormat.JPEG, 100, outstreamgf)
            outstreamgf!!.close()
        } catch (e: Exception) {
            System.err.println(e.toString())
        }

        // Suministramos los datos de la imagen que compartiremos con EXTRA_STREAM
        compartirgf.putExtra(Intent.EXTRA_STREAM, urigf)

        // Iniciamos la Actividad (Activity) con la lógica de nuestro proyecto
        startActivity(Intent.createChooser(compartirgf, getString(R.string.compartir_gf_txt)))

        val chooseIntent = Intent.createChooser(compartirgf, "Elija una opcion")
        startActivity(chooseIntent)


    }



    private fun calendario(mes: Int, dia: Int) {

        if ((mes == 1 && dia >= 20 && dia < 32) || (mes == 2 && dia >= 1 && dia < 16)) {
            //Toast.makeText(this,"Capricornio",Toast.LENGTH_LONG).show()
            binding.tvSigno.setText("capricorn")
            binding.imageViewZodiaco.setImageResource(signos[0])


        }
        if ((mes == 2 && dia >= 16 && dia <= 28) || (mes == 3 && dia >= 1 && dia < 11)) {
            //Toast.makeText(this,"Aquario",Toast.LENGTH_LONG).show()
            binding.tvSigno.setText("aquarius")
            binding.imageViewZodiaco.setImageResource(signos[1])

        }
        if ((mes == 3 && dia >= 11 && dia < 32) || (mes == 4 && dia >= 1 && dia < 18)) {
            //Toast.makeText(this,"Piscis",Toast.LENGTH_LONG).show()
            binding.tvSigno.setText("pisces")
            binding.imageViewZodiaco.setImageResource(signos[2])

        }
        if ((mes == 4 && dia >= 18 && dia < 31) || (mes == 5 && dia >= 1 && dia < 13)) {
            //Toast.makeText(this,"Aries",Toast.LENGTH_LONG).show()
            binding.tvSigno.setText("aries")
            binding.imageViewZodiaco.setImageResource(signos[3])

        }
        if ((mes == 5 && dia >= 13 && dia < 32) || (mes == 6 && dia >= 1 && dia < 21)) {
            //Toast.makeText(this,"Tauro",Toast.LENGTH_LONG).show()
            binding.tvSigno.setText("taurus")
            binding.imageViewZodiaco.setImageResource(signos[4])

        }

        if ((mes == 6 && dia >= 21 && dia < 31) || (mes == 7 && dia >= 1 && dia < 20)) {
            //Toast.makeText(this,"Geminis",Toast.LENGTH_LONG).show()
            binding.tvSigno.setText("gemini")
            binding.imageViewZodiaco.setImageResource(signos[5])

        }

        if ((mes == 7 && dia >= 20 && dia < 31) || (mes == 8 && dia >= 1 && dia < 10)) {
            //Toast.makeText(this,"Cancer",Toast.LENGTH_LONG).show()
            binding.tvSigno.setText("cancer")
            binding.imageViewZodiaco.setImageResource(signos[6])


        }
        if ((mes == 8 && dia >= 10 && dia < 32) || (mes == 9 && dia >= 1 && dia < 16)) {
            //Toast.makeText(this,"Leo",Toast.LENGTH_LONG).show()
            binding.tvSigno.setText("leo")
            binding.imageViewZodiaco.setImageResource(signos[7])


        }
        if ((mes == 9 && dia >= 16 && dia < 31) || (mes == 10 && dia >= 1 && dia < 30)) {
            //Toast.makeText(this,"Virgo",Toast.LENGTH_LONG).show()
            binding.tvSigno.setText("virgo")
            binding.imageViewZodiaco.setImageResource(signos[11])

        }
        if ((mes == 10 && dia >= 30 && dia < 32) || (mes == 11 && dia >= 1 && dia < 23)) {
            //Toast.makeText(this,"Libra",Toast.LENGTH_LONG).show()
            binding.tvSigno.setText("libra")
            binding.imageViewZodiaco.setImageResource(signos[9])

        }
        if ((mes == 11 && dia >= 23 && dia < 29)) {
            binding.tvSigno.setText("scorpio")
            //Toast.makeText(this,"Escorpio",Toast.LENGTH_LONG).show()
            binding.imageViewZodiaco.setImageResource(signos[10])

        }

        if ((mes == 11 && dia > 29 && dia < 31) || (mes == 12 && dia >= 1 && dia < 17)) {
            //Toast.makeText(this,"Capricornio",Toast.LENGTH_LONG).show()
            binding.tvSigno.setText("Ophiuchus")
            binding.imageViewZodiaco.setImageResource(signos[8])

        }

        if ((mes == 12 && dia >= 17 && dia < 32) || (mes == 1 && dia >= 1 && dia < 20)) {
            binding.tvSigno.setText("sagittarius")
            //Toast.makeText(this,"Sagitario",Toast.LENGTH_LONG).show()
            binding.imageViewZodiaco.setImageResource(signos[12])

        }


    }




      fun loadDataSign(){
        val dataSingService:APIService =ServiceBuilder.buildService(APIService::class.java)
        val requestCall: Call<ResponseBody> = dataSingService.getDataAboutSign(binding.tvSigno.text.toString(),"today")

        requestCall.enqueue( object: Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
               if(response.isSuccessful){
                   val dataSing: ResponseBody? =response.body()!!



                     Log.d("dataSing",dataSing.toString())
                   if (dataSing != null) {
                       binding.tvPrediccion.setText(dataSing.string())
                   }


               }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }


        }

        )






    }
}




