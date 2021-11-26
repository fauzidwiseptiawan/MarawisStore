package com.example.marawisstore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marawisstore.R
import com.example.marawisstore.adapter.AdapterKeranjang
import com.example.marawisstore.helper.Helper
import com.example.marawisstore.model.Produk
import com.example.marawisstore.room.MyDatabase
import kotlinx.android.synthetic.main.activity_keranjang_produk.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar_baru.*

class KeranjangProdukActivity : AppCompatActivity() {

    lateinit var produk: Produk
    lateinit var adapter : AdapterKeranjang
    lateinit var myDb: MyDatabase
    var listProdukKeranjang = ArrayList<Produk>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keranjang_produk)
        //Set Toolbar
        Helper().setToolbarBaru(this, toolbar, "Keranjang")

        myDb = MyDatabase.getInstance(this)!! // call database favorit

        displayProdukKeranjang()
        mainButton()
        showFooter()
        EmptyKeranjang()
        refreshApp()

    }

    private fun refreshApp() {
        swipeToRefresh.setOnRefreshListener {
            //Set Toolbar
            Helper().setToolbar(this, toolbar_baru, "Keranjang")
            displayProdukKeranjang()
            swipeToRefresh.isRefreshing = false
        }
    }

    private fun displayProdukKeranjang(){
        listProdukKeranjang = myDb.daoKeranjang().getAll() as ArrayList
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        adapter = AdapterKeranjang(this, listProdukKeranjang, object : AdapterKeranjang.Listeners{
            override fun onUpdate() {
                hitungTotal()
            }

            override fun onDelete(position: Int) {
                listProdukKeranjang.removeAt(position)
                adapter.notifyDataSetChanged()
            }
        })
        rv_produk_keranjang.adapter = adapter
        rv_produk_keranjang.layoutManager = layoutManager
    }

    var totalHarga = 0
    var totalBerat = 0
    private fun hitungTotal(){
        val listProdukKeranjang =  myDb.daoKeranjang().getAll() as ArrayList

        totalHarga = 0
        var isSelectedAll = true
        for(produk in listProdukKeranjang){
            if(produk.selected){
                val berat = Integer.valueOf(produk.berat)
                totalBerat += (berat * produk.jumlah)
                val harga = Integer.valueOf(produk.harga)
                totalHarga += (harga * produk.jumlah)
            }else{
                isSelectedAll = false
            }
        }
        cb_allKeranjang.isChecked = isSelectedAll
        tv_total.text = Helper().gantiRupiah(totalHarga)
    }

    private fun mainButton(){
        btn_toFavorit.setOnClickListener {
            startActivity(Intent(this, FavoritProdukActivity::class.java))
        }

        btn_bayar.setOnClickListener {
            val intent = Intent(this,PengirimanActivity::class.java)
            intent.putExtra("extraa","" + totalBerat)
            intent.putExtra("extra","" + totalHarga)
            startActivity(intent)
        }

        cb_allKeranjang.setOnClickListener {
            for (i in listProdukKeranjang.indices){
                val produk = listProdukKeranjang[i]
                produk.selected = cb_allKeranjang.isChecked
                listProdukKeranjang[i] = produk
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun showFooter(){
        val listProduks = myDb.daoKeranjang().getAll() as ArrayList

        for (produk in listProduks)
            if (produk.selected){
                rv_produk_keranjang.visibility  = View.VISIBLE
                div_footer.visibility = View.VISIBLE
                div_header.visibility = View.VISIBLE
            }else{
                div_header.visibility = View.GONE
                div_footer.visibility = View.GONE
            }
    }

    private fun EmptyKeranjang(){
        val listProduks = myDb.daoKeranjang().getAll() as ArrayList

        if (listProduks.isEmpty()){
            empty_keranjang.visibility = View.VISIBLE
        } else {
            empty_keranjang.visibility = View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}