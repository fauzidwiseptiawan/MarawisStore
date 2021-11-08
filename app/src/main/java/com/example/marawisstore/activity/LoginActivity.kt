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
import kotlinx.android.synthetic.main.activity_login.edt_email
import kotlinx.android.synthetic.main.activity_login.edt_password
import kotlinx.android.synthetic.main.activity_masuk.*
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    lateinit var s:SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        s = SharedPref(this)

        btn_login.setOnClickListener {
            login()
        }

        btn_back.setOnClickListener {
            onBackPressed()
        }
    }

    fun login(){
        if(edt_email.text.isEmpty()){
            edt_email.error = "Email Tidak Boleh Kosong"
            edt_email.requestFocus()
            return
        }else if(edt_password.text.isEmpty()){
            edt_password.error = "Password Tidak Boleh Kosong"
            edt_password.requestFocus()
            return
        }

        pb_login.visibility = View.VISIBLE

        ApiConfig.instanceRetrofit.login(edt_email.text.toString(), edt_password.text.toString()).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                //Handle ketika gagal
                pb_login.visibility = View.GONE
                Toast.makeText(this@LoginActivity,"Error:"+t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                pb_login.visibility = View.GONE
                val respon = response.body()!!

                //Handle ketika sukses
                if (respon.success == 1) {
                    s.setStatusLogin(true)
                    s.setUser(respon.user)
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                    Toast.makeText(this@LoginActivity, "Selamat Datang "+respon.user.nama, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@LoginActivity, respon.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}