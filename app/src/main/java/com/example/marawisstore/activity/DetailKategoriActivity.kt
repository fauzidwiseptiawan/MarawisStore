package com.example.marawisstore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marawisstore.R
import com.example.marawisstore.adapter.AdapterPromo
import com.example.marawisstore.app.ApiConfig
import com.example.marawisstore.helper.Helper
import com.example.marawisstore.model.Produk
import com.example.marawisstore.model.ResponModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_kategori_produk.*
import kotlinx.android.synthetic.main.toolbar_costume.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailKategoriActivity : AppCompatActivity() {

    private var listProduk: ArrayList<Produk> = ArrayList()
    lateinit var produk: Produk


//    lateinit var kategori: Promo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kategori_produk)
        //Set Toolbar
        Helper().setToolbarCostume(this, toolbar_costume, "Produk Terbaru")

        displayProduk()
        getInfo()
    }

    private fun displayProduk(){
        val layoutManager = GridLayoutManager(this,2)

        layoutManager.orientation = LinearLayoutManager.VERTICAL

        rv_AllProdukLain.adapter = AdapterPromo(this, listProduk)
        rv_AllProdukLain.layoutManager = layoutManager
    }

    fun getInfo(){
        val data = intent.getStringExtra("extra")
        produk = Gson().fromJson<Produk>(data, Produk::class.java)
        

    }


    private fun getKategori(id: Int) {
        ApiConfig.instanceRetrofit.getprodukkategori(id).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                //Handle ketika gagal

            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                val res = response.body()!!
                if (res.success == 1) {
                    listProduk = res.produk
                    displayProduk()
                }
            }

        })

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
    }
}