package com.example.marawisstore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.marawisstore.R
import com.example.marawisstore.helper.SharedPref
import kotlinx.android.synthetic.main.activity_masuk.*

class MasukActivity : AppCompatActivity() {

    lateinit var s:SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_masuk)

        s = SharedPref(this)

        btn_prosesLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btn_back_masuk.setOnClickListener {
            onBackPressed()
        }

        btn_register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))

        }

    }
}