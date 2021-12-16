package com.example.marawisstore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marawisstore.R
import com.example.marawisstore.adapter.AdapterAllProduk
import com.example.marawisstore.app.ApiConfig
import com.example.marawisstore.helper.Helper
import com.example.marawisstore.model.Produk
import com.example.marawisstore.model.ResponModel
import com.example.marawisstore.room.MyDatabase
import kotlinx.android.synthetic.main.activity_all_produk.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.toolbar_baru.*
import kotlinx.android.synthetic.main.toolbar_costume.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllProdukLainActivity : AppCompatActivity() {

    private var listProduk: ArrayList<Produk> = ArrayList()
    lateinit var myDb: MyDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_produk)
        //Set Toolbar
        Helper().setToolbarCostume(this, toolbar_costume, "Produk Lainnya")

        myDb = MyDatabase.getInstance(this)!! // call database keranjang

        displayProduk()
        getProdukLainnyaAll()
        checkKeranjang()
        mainButton()
    }

    private fun mainButton(){
        btn_toKeranjang.setOnClickListener {
            startActivity(Intent(this@AllProdukLainActivity,KeranjangProdukActivity::class.java))
        }
    }

    private fun displayProduk(){
        val layoutManager = GridLayoutManager(this,2)

        layoutManager.orientation = LinearLayoutManager.VERTICAL

        rv_all_produk_baru.adapter = AdapterAllProduk(this, listProduk)
        rv_all_produk_baru.layoutManager = layoutManager
    }

    fun getProdukLainnyaAll() {
        ApiConfig.instanceRetrofit.getProductLainnya().enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                //Handle ketika gagal

            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {

                if (response.isSuccessful) {
                    val res = response.body()!!
                    if (res.success == 1) {
                        listProduk = res.produk
                        displayProduk()
                    }
                }
            }

        })
    }

    private fun checkKeranjang(){
        val dataKeranjang = myDb.daoKeranjang().getAll()

        if(dataKeranjang.isNotEmpty()){
            div_angka.visibility = View.VISIBLE
            tv_angkaa.text = dataKeranjang.size.toString()
        } else{
            div_angka.visibility = View.GONE
        }
    }

    override fun onResume() {
        checkKeranjang()
        super.onResume()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}