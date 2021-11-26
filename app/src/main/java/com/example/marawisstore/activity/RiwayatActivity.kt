package com.example.marawisstore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marawisstore.R
import com.example.marawisstore.adapter.AdapterRiwayat
import com.example.marawisstore.app.ApiConfig
import com.example.marawisstore.helper.Helper
import com.example.marawisstore.helper.SharedPref
import com.example.marawisstore.model.ResponModel
import com.example.marawisstore.model.Transaksi
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_riwayat.*
import kotlinx.android.synthetic.main.toolbar_baru.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat)
        Helper().setToolbar(this, toolbar_baru, "Riwayat")

        getRiwayat()
    }

    fun getRiwayat() {
        val id = SharedPref(this).getUser()!!.id_pelanggan
        ApiConfig.instanceRetrofit.getRiwayat(id).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                //Handle ketika gagal
            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                if (response.isSuccessful) {
                    val res = response.body()!!
                    if (res.success == 1) {
                        displayRiwayat(res.transaksis)
                    }
                }
            }

        })

    }

    fun displayRiwayat(transaksis: ArrayList<Transaksi>) {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        rv_riwayat.adapter = AdapterRiwayat(transaksis, object :AdapterRiwayat.Listeners{
            override fun onClicked(data: Transaksi) {
                val json = Gson().toJson(data, Transaksi::class.java)
                val intent = Intent(this@RiwayatActivity, DetailTransaksiActivity::class.java)
                intent.putExtra("transaksi", json)
                startActivity(intent)
            }

        })
        rv_riwayat.layoutManager = layoutManager
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
    }
}