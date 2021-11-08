package com.example.marawisstore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marawisstore.R
import com.example.marawisstore.adapter.AdapterFavorit
import com.example.marawisstore.helper.Helper
import com.example.marawisstore.model.Produk
import com.example.marawisstore.room.MyDatabase
import com.example.marawisstore.room.MyDatabaseFavorit
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_favorit_produk.*
import kotlinx.android.synthetic.main.item_favorit.*
import kotlinx.android.synthetic.main.toolbar_baru.*
import kotlinx.android.synthetic.main.toolbar_costume.*

class FavoritProdukActivity : AppCompatActivity() {

    lateinit var myDb: MyDatabase
    lateinit var myDbFav: MyDatabaseFavorit
    lateinit var produk: Produk
    lateinit var adapter : AdapterFavorit
    var listProdukFavorit = ArrayList<Produk>()

    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorit_produk)
        //Set Toolbar
        Helper().setToolbarCostume(this, toolbar_costume, "Wishlist")

        myDb = MyDatabase.getInstance(this)!! // call database favorit
        myDbFav = MyDatabaseFavorit.getInstance(this)!! // call database favorit

        displayProdukFav()
        mainButton()
        checkKeranjang()
    }

    private fun checkKeranjang(){
        val dataKeranjang = myDb.daoKeranjang().getAll()

        if(dataKeranjang.isNotEmpty()){
            div_angka.visibility = View.VISIBLE
            tv_angkaa.text = dataKeranjang.size.toString()
        } else{
            rv_ProdukFav.visibility = View.VISIBLE
            div_angka.visibility = View.GONE
        }
    }

    private fun emptyFavorit(){
        val listProduks = myDb.daoKeranjang().getAll() as ArrayList

        if (listProduks.isEmpty()){
            empty_favorit.visibility = View.GONE
        } else {
            empty_favorit.visibility = View.VISIBLE
        }
    }


    private fun mainButton(){
        btn_toKeranjang.setOnClickListener {
            startActivity(Intent(this@FavoritProdukActivity, KeranjangProdukActivity::class.java))
        }
    }

    private fun displayProdukFav(){
        listProdukFavorit = myDbFav.daoFavorit().getAllFav() as ArrayList
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        adapter = AdapterFavorit(this, listProdukFavorit, object : AdapterFavorit.Listeners{
            override fun onInsert(position: Int) {
                adapter.notifyDataSetChanged()
            }
            override fun onUpdate() {

            }

            override fun onDelete(position: Int) {
                listProdukFavorit.removeAt(position)
                adapter.notifyDataSetChanged()
            }

            override fun onItemClick(produk: Produk) {
                val myDb: MyDatabase = MyDatabase.getInstance(applicationContext)!! // call database
                val data = myDb.daoKeranjang().getProduk(produk.id_produk) // get produk
                if (data == null){
                    insert(produk)
                }else{
                    data.jumlah += 1
                    update(data)
                }
//                listener.onInsert(position)
                Toast.makeText(applicationContext,"Yay! Barang berhasil ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
            }
        })

        rv_ProdukFav.adapter = adapter
        rv_ProdukFav.layoutManager = layoutManager
    }

    private fun insert(data: Produk){
        val myDb = MyDatabase.getInstance(this)
        CompositeDisposable().add(Observable.fromCallable { myDb!!.daoKeranjang()!!.insert(data) }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    checkKeranjang()
                })
    }

    private fun update(data: Produk){
        val myDb = MyDatabase.getInstance(this)
        CompositeDisposable().add(Observable.fromCallable { myDb!!.daoKeranjang().update(data) }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    checkKeranjang()
                    Log.d("respons", "data inserted")
                })
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onResume() {
        checkKeranjang()
        emptyFavorit()
        super.onResume()
    }

}