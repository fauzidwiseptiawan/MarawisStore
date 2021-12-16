package com.example.marawisstore.activity

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marawisstore.R
import com.example.marawisstore.adapter.AdapterProduk
import com.example.marawisstore.app.ApiConfig
import com.example.marawisstore.helper.Helper
import com.example.marawisstore.model.Produk
import com.example.marawisstore.model.ResponModel
import com.example.marawisstore.room.MyDatabase
import com.example.marawisstore.room.MyDatabaseFavorit
import com.example.marawisstore.util.Config
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_detail_produk.*
import kotlinx.android.synthetic.main.activity_detail_produk.btn_favorit
import kotlinx.android.synthetic.main.activity_detail_produk.tv_nama
import kotlinx.android.synthetic.main.toolbar_costume.*
import kotlinx.android.synthetic.main.toolbar_costume.btn_toKeranjang
import kotlinx.android.synthetic.main.toolbar_costume.div_angka
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailProdukActivity : AppCompatActivity() {

    lateinit var myDb: MyDatabase
    lateinit var myDbFav: MyDatabaseFavorit
    lateinit var produk: Produk

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_produk)

        myDb = MyDatabase.getInstance(this)!! // call database keranjang
        myDbFav = MyDatabaseFavorit.getInstance(this)!! // call database favorit

        getInfo()
        mainButton()
        getProdukTerkait()
        checkKeranjang()
        refreshApp()
    }

    private fun refreshApp() {
        swipeToRefresh.setOnRefreshListener {
            getInfo()
            getProdukTerkait()
            swipeToRefresh.isRefreshing = false
        }
    }

    var totalHarga = 0
    var totalBerat = 0
    private fun hitungTotal(){
        val listProduk = myDb.daoKeranjang().getProduk(produk.kode_produk)

            if(listProduk == null){
                val berat = Integer.valueOf(produk.berat)
                totalBerat += (berat * produk.jumlah)
                val harga = Integer.valueOf(produk.harga)
                totalHarga += (harga * produk.jumlah)
            }
    }

    private fun mainButton(){
        btn_keranjang.setOnClickListener {
            val data = myDb.daoKeranjang().getProduk(produk.kode_produk)
            if(data == null){
                insert()
            } else {
                data.jumlah += 1
                update(data)
            }
        }
        btn_favorit.setOnClickListener {
            val favorit = myDbFav.daoFavorit().getProdukFav(produk.kode_produk)
            if(favorit == null){
                insertFav()
            }else{
                favorit.jumlahfav  += 1
                updateFav(favorit)
            }
        }
        btn_beli.setOnClickListener {
            val intent = Intent(this,PengirimanBeliActivity::class.java)
            intent.putExtra("berat","" + totalBerat)
            intent.putExtra("harga","" + totalHarga)
            startActivity(intent)
        }

        btn_toKeranjang.setOnClickListener {
            startActivity(Intent(this,KeranjangProdukActivity::class.java))
        }
    }

    //    Insert Data Ke Keranjang
    private fun insert(){
        CompositeDisposable().add(Observable.fromCallable { myDb.daoKeranjang().insert(produk) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                checkKeranjang()
                Log.d("respons", "data inserted")
                Toast.makeText(this,"Yay! Barang berhasil ditambahkan ke keranjang",Toast.LENGTH_SHORT).show()
            })
    }

    private fun update(data: Produk){
        CompositeDisposable().add(Observable.fromCallable { myDb.daoKeranjang().update(data) }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    checkKeranjang()
                    Log.d("respons", "data inserted")
                    Toast.makeText(this,"Yay! Barang berhasil diupdate ke keranjang",Toast.LENGTH_SHORT).show()
                })
    }

    //    Insert Data Ke Favorit
    private fun insertFav(){
        CompositeDisposable().add(Observable.fromCallable { myDbFav.daoFavorit().insertfav(produk) }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d("respons", "data inserted")
                    Toast.makeText(this,"Yay! Barang berhasil ditambahkan ke wishlist",Toast.LENGTH_SHORT).show()
                })
    }

    private fun updateFav(data: Produk){
        CompositeDisposable().add(Observable.fromCallable { myDbFav.daoFavorit().updatefav(data) }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d("respons", "data inserted")
                    Toast.makeText(this,"Yay! Barang berhasil ditambahkan ke wishlist",Toast.LENGTH_SHORT).show()
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

    private fun getInfo(){
        val data = intent.getStringExtra("extra")
        produk = Gson().fromJson<Produk>(data, Produk::class.java)

        val diskon = Integer.valueOf(produk.diskon)
        val harga = Integer.valueOf(produk.harga)
        val hargaCoret = (harga - ((produk.diskon.toDouble() / 100) * harga).toInt())

        if (produk.diskon != 0){
            ly_promo.visibility = View.GONE
            ly_diskon.visibility = View.VISIBLE
            tv_diskon.text = Helper().gantiRupiah(harga)
            tv_diskon.paintFlags = tv_diskon.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }

        //Set Value
        tv_persen.text = "${diskon}%"
        tv_nama.text = produk.nama_produk
        tv_harga_produk.text = Helper().gantiRupiah(hargaCoret)
        tv_sku.text = produk.kode_produk
        tv_stok.text = produk.stok+" Set"
        tv_berat.text = produk.berat+" Gram"
        tv_deskripsi.text = produk.keterangan

        var img = Config.produkUrl + produk.image

        Picasso.get()
                .load(img)
                .placeholder(R.drawable.blank)
                .error(R.drawable.blank)
                .resize(500,500)
                .centerCrop()
                .into(image)

        //Set Toolbar
        Helper().setToolbarCostume(this, toolbar_costume, produk.nama_produk)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }


    private fun displayProduk(){
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL

        rv_produkTerkait.adapter = AdapterProduk(this, listProduk)
        rv_produkTerkait.layoutManager = layoutManager
    }

    private var listProduk: ArrayList<Produk> = ArrayList()

    private fun getProdukTerkait() {
        ApiConfig.instanceRetrofit.getProductTerkait().enqueue(object : Callback<ResponModel> {
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

    override fun onResume() {
        checkKeranjang()
        hitungTotal()
        getInfo()
        super.onResume()
    }

}