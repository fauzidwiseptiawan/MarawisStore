package com.example.marawisstore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.marawisstore.MainActivity
import com.example.marawisstore.R
import com.example.marawisstore.app.ApiConfig
import com.example.marawisstore.helper.SharedPref
import com.example.marawisstore.model.ResponModel
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.edt_email
import kotlinx.android.synthetic.main.activity_register.edt_password
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    lateinit var s:SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        s = SharedPref(this)

        btn_registerInput.setOnClickListener {
            register()
        }

        btn_back_regis.setOnClickListener {
            onBackPressed()
        }

        btn_google.setOnClickListener {
            dataDummy()
        }
    }

        fun dataDummy(){
            edt_nama.setText("fauzi")
            edt_email.setText("fauzidwiseptiawan123@gmail.com")
            edt_telpon.setText("089637831822")
            edt_password.setText("kepoanda123")
        }

    fun register(){
        if(edt_nama.text.isEmpty()){
            edt_nama.error = "Nama Tidak Boleh Kosong"
            edt_nama.requestFocus()
            return
        } else if(edt_email.text.isEmpty()){
            edt_email.error = "Email Tidak Boleh Kosong"
            edt_email.requestFocus()
            return
        } else if(edt_telpon.text.isEmpty()){
            edt_telpon.error = "Nomor Telepon Tidak Boleh Kosong"
            edt_telpon.requestFocus()
            return
        } else if(edt_password.text.isEmpty()){
            edt_password.error = "Password Tidak Boleh Kosong"
            edt_password.requestFocus()
            return
        }

        pb_register.visibility = View.VISIBLE

        ApiConfig.instanceRetrofit.register(edt_nama.text.toString(), edt_email.text.toString(), edt_telpon.text.toString(), edt_password.text.toString()).enqueue(object : Callback<ResponModel>{

            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                //Handle ketika
                pb_register.visibility = View.GONE
                Toast.makeText(this@RegisterActivity,"Error:"+t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                pb_register.visibility = View.GONE
                val respon = response.body()!!

                //Handle ketika sukses
                if (respon.success == 1) {
                    s.setStatusLogin(false)
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                    Toast.makeText(this@RegisterActivity, respon.message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@RegisterActivity, respon.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}