package com.example.marawisstore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marawisstore.R
import com.example.marawisstore.adapter.AdapterKeranjang
import com.example.marawisstore.helper.Helper
import com.example.marawisstore.helper.SharedPref
import com.example.marawisstore.model.Produk
import com.example.marawisstore.room.MyDatabase
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_keranjang.*
import kotlinx.android.synthetic.main.item_harga.*
import kotlinx.android.synthetic.main.toolbar.*

class KeranjangActivity : AppCompatActivity() {

    lateinit var produk: Produk
    lateinit var adapter : AdapterKeranjang
    lateinit var myDb: MyDatabase
    lateinit var s: SharedPref

    var listProduk = ArrayList<Produk>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keranjang)
        //Set Toolbar
        Helper().setToolbarBaru(this, toolbar, "Keranjang")

        myDb = MyDatabase.getInstance(this)!! // call database favorit

        mainButton()
        refreshApp()
    }

    private fun refreshApp() {
        swipeToRefresh.setOnRefreshListener {
            displayProduk()
            selectProduct()
            hitungTotal()
            swipeToRefresh.isRefreshing = false
        }
    }

    private fun displayProduk(){
        listProduk = myDb.daoKeranjang().getAll() as ArrayList
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        adapter = AdapterKeranjang(this, listProduk, object : AdapterKeranjang.Listeners{
            override fun onUpdate() {
                hitungTotal()
                selectProduct()
            }

            override fun onDelete(position: Int) {
                listProduk.removeAt(position)
                adapter.notifyDataSetChanged()
                hitungTotal()
                selectProduct()
            }
        })
        rv_produk_keranjang.adapter = adapter
        rv_produk_keranjang.layoutManager = layoutManager
    }

    var hargaAsli = 0
    var totalHarga = 0
    var totalDiskon = 0
    var totalBerat = 0

    private fun hitungTotal(){
        val listProduk =  myDb.daoKeranjang().getAll() as ArrayList
        totalHarga = 0
        totalDiskon = 0
        hargaAsli = 0
        var isSelectedAll = true
        if (listProduk.isEmpty()){
            empty_keranjang.visibility = View.VISIBLE
            div_header.visibility = View.GONE
            div_footer.visibility = View.GONE
        }else{
            for(produk in listProduk){
                if(produk.selected){
                    val berat = Integer.valueOf(produk.berat)
                    totalBerat += (berat * produk.jumlah)
                    val harga = Integer.valueOf(produk.harga)
                    val hasilHarga = (harga * produk.jumlah)
                    val diskon = Integer.valueOf(produk.diskon)
                    val hasilDiskon = (harga * diskon / 100)
                    hargaAsli += (harga * produk.jumlah)
                    totalHarga += (hasilHarga - hasilDiskon)
                    totalDiskon += (hasilHarga / 100 * (-diskon))
                }else{
                    isSelectedAll = false
                }
            }
            cb_allKeranjang.isChecked = isSelectedAll
            tv_total.text = Helper().gantiRupiah(totalHarga)
        }
    }

    private fun selectProduct(){
        var selectProduct = false

        for (p in listProduk){
            if (p.selected) selectProduct = true
        }
        if (selectProduct){
            ly_bayar.visibility = View.VISIBLE
            ly_total.visibility = View.VISIBLE
            btm_harga.visibility = View.VISIBLE
            tv_delete.visibility = View.VISIBLE
        } else {
            ly_bayar.visibility = View.GONE
            ly_total.visibility = View.GONE
            btm_harga.visibility = View.GONE
            tv_delete.visibility = View.GONE
        }
    }

    private fun mainButton(){
        btn_toFavorit.setOnClickListener {
            startActivity(Intent(this, FavoritProdukActivity::class.java))
        }

        btn_bayar.setOnClickListener {
            if (s.getStatusLogin()){
                var isThereProduk = false

                for (p in listProduk){
                    if (p.selected) isThereProduk = true
                }

                if (isThereProduk){
                    val intent = Intent(this,PengirimanActivity::class.java)
                    intent.putExtra("totalBerat","" + totalBerat)
                    intent.putExtra("totalHarga","" + totalHarga)
                    intent.putExtra("totalDiskon","" + totalDiskon)
                    startActivity(intent)
                } else {
                    Toast.makeText(this,"Tidak ada produk yang terpilih", Toast.LENGTH_SHORT).show()
                }
            } else {
                startActivity(Intent(this, MasukActivity::class.java))
            }
        }

        tv_delete.setOnClickListener {
            var isSelectedAll = true
            val listDelete = ArrayList<Produk>()
            for (p in listProduk){
                if (p.selected){
                    listDelete.add(p)
                    isSelectedAll = false
                }
            }
            cb_allKeranjang.isChecked = isSelectedAll
            delete(listDelete)
        }

        cb_allKeranjang.setOnClickListener {
            for (i in listProduk.indices){
                val produk = listProduk[i]
                produk.selected = cb_allKeranjang.isChecked
                listProduk[i] = produk
            }
            adapter.notifyDataSetChanged()
        }

        btm_harga.setOnClickListener {
            var isThereProduk = false
            val bottomDialog = BottomSheetDialog(
                    this, R.style.BottomSheetDialogtheme
            )
            val bottomSheetView = LayoutInflater.from(this.applicationContext).inflate(
                    R.layout.item_harga,
                    bottom_harga
            )
            for (p in listProduk){
                if (p.selected) isThereProduk = true
            }
            if(isThereProduk){
                bottomSheetView.findViewById<TextView>(R.id.tv_harga).text = Helper().gantiRupiah(hargaAsli)
                if (totalDiskon != 0){
                    bottomSheetView.findViewById<TextView>(R.id.tv_diskon).text = Helper().gantiRupiah(totalDiskon)
                } else {
                    bottomSheetView.findViewById<LinearLayout>(R.id.ly_diskon).visibility = View.GONE
                }
                bottomSheetView.findViewById<TextView>(R.id.tv_bayar).text = Helper().gantiRupiah(totalHarga)
                bottomDialog.setContentView(bottomSheetView)
                bottomDialog.show()
            }
        }
    }

    private fun delete(data: ArrayList<Produk>){
        CompositeDisposable().add(Observable.fromCallable { myDb.daoKeranjang().delete(data) }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    listProduk.clear()
                    listProduk.addAll(myDb.daoKeranjang().getAll() as ArrayList)
                    adapter.notifyDataSetChanged()
                })
    }

    override fun onResume() {
        selectProduct()
        hitungTotal()
        displayProduk()
        super.onResume()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}