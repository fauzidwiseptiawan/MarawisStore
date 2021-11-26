package com.example.marawisstore.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marawisstore.MainActivity
import com.example.marawisstore.R
import com.example.marawisstore.adapter.AdapterKeranjang
import com.example.marawisstore.adapter.AdapterKurir
import com.example.marawisstore.adapter.AdapterPengiriman
import com.example.marawisstore.app.ApiConfig
import com.example.marawisstore.app.ApiConfigAlamat
import com.example.marawisstore.helper.Helper
import com.example.marawisstore.helper.SharedPref
import com.example.marawisstore.model.Checkout
import com.example.marawisstore.model.Produk
import com.example.marawisstore.model.ResponModel
import com.example.marawisstore.model.rajaongkir.Costs
import com.example.marawisstore.model.rajaongkir.ResponOngkir
import com.example.marawisstore.room.MyDatabase
import com.example.marawisstore.util.ApiKey
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_pengiriman.*
import kotlinx.android.synthetic.main.activity_pengiriman.div_kosong
import kotlinx.android.synthetic.main.toolbar_baru.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PengirimanActivity : AppCompatActivity() {

    lateinit var myDb: MyDatabase
    var totalHarga = 0
    var totalBerat = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengiriman)
        //Set Toolbar
        Helper().setToolbar(this, toolbar_baru, "Pengiriman")
        myDb = MyDatabase.getInstance(this)!!

        totalBerat = Integer.valueOf(intent.getStringExtra("extraa")!!)
        totalHarga = Integer.valueOf(intent.getStringExtra("extra")!!)
        tv_totalBelanja.text = Helper().gantiRupiah(totalHarga)

        mainButton()
        setSpinner()
    }

    private fun setSpinner() {
        val arrayString = ArrayList<String>()
        arrayString.add("Pilih Kurir")
        arrayString.add("JNE")
        arrayString.add("POS")
        arrayString.add("TIKI")

        val adapter = ArrayAdapter<Any>(this, R.layout.item_dropdown, arrayString.toTypedArray())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spn_kurir.adapter = adapter
        spn_kurir.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    getOngkir(spn_kurir.selectedItem.toString())
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkAlamat(){
        if (myDb.daoAlamat().getByStatus(true) != null){
            div_alamat.visibility = View.VISIBLE
            div_kosong.visibility = View.GONE
            div_metodePengiriman.visibility = View.VISIBLE

            val a = myDb.daoAlamat().getByStatus(true)!!
            tv_nama.text = a.nama
            tv_telpon.text = a.telpon
            tv_alamat.text = a.alamat + ", " + a.kota + ", " + a.kodepos + ", (" + a.type + ")"
            button.text = "Ubah Alamat"

            getOngkir("Pilih Kurir")
        } else {
            div_alamat.visibility = View.GONE
            div_kosong.visibility = View.VISIBLE
            button.text = "Tambah Alamat"
        }
    }

    private fun mainButton(){
        btn_tambah_alamat.setOnClickListener {
            startActivity(Intent(this, ListAlamatActivity::class.java))
        }

        btn_bayar.setOnClickListener {
            bayar()
        }
    }

    private fun bayar(){
        var user = SharedPref(this).getUser()!!
        val a = myDb.daoAlamat().getByStatus(true)!!

        val listProduk = myDb.daoKeranjang().getAll() as ArrayList
        var totalItem = 0
        var totalHarga = 0
        var totalBerat = 0
        val produks = ArrayList<Checkout.Item>()
        for (p in listProduk){
            if (p.selected){
                totalItem += p.jumlah
                totalHarga += (p.jumlah * Integer.valueOf(p.harga))
                totalBerat += (p.jumlah * Integer.valueOf(p.berat))

                val produk =  Checkout.Item()
                produk.kode_produk = p.kode_produk
                produk.qty = ""+ p.jumlah
                produk.harga = "" + (p.jumlah * Integer.valueOf(p.harga))
                produk.berat = "" + (p.jumlah * Integer.valueOf(p.berat))

                produks.add(produk)
            }
        }

        val checkout = Checkout()
        checkout.costumer_id = "" + user.id_pelanggan
        checkout.total_qty = "" + totalItem
        checkout.total_harga = "" + totalHarga
        checkout.total_berat = "" + totalBerat
        checkout.nama = a.nama
        checkout.telpon = a.telpon
        checkout.catatan = "Catatan baru"
        checkout.layanan = layanan
        checkout.kurir = kurir
        checkout.ongkir = ongkir
        checkout.jumlah_bayar = "" + (totalHarga + Integer.valueOf(ongkir))
        checkout.alamat = tv_alamat.text.toString()
        checkout.produks = produks

        val json = Gson().toJson(checkout, Checkout::class.java)
        Log.d("Responce:","json:" + json)
        val intent = Intent(this, PembayaranActivity::class.java)
        intent.putExtra("extra", json)
        startActivity(intent)
    }

    private fun getOngkir(kurir: String){

        val alamat = myDb.daoAlamat().getByStatus(true)

        val origin = "78"
        val destination = "" + alamat!!.id_kota.toString()
        val berat = totalBerat

        ApiConfigAlamat.instanceRetrofit.ongkir(ApiKey.key, origin, destination, berat, kurir.toLowerCase()).enqueue(object : Callback<ResponOngkir> {
            override fun onResponse(call: Call<ResponOngkir>, response: Response<ResponOngkir>) {
                if (response.isSuccessful) {
                    div_metode.visibility = View.GONE
                    val result = response.body()!!.rajaongkir.results
                    if (result.isNotEmpty()) {
                        div_metode.visibility = View.GONE
                        rv_metode.visibility = View.VISIBLE
                        displayOngkir(result[0].code.toUpperCase(), result[0].costs)
                    }
                }else {
                    Log.d("Error","gagal memuat data:" + response.message())
                }
            }

            override fun onFailure(call: Call<ResponOngkir>, t: Throwable) {
                Log.d("Error","gagal memuat data:" + t.message)
            }

        })
    }

    var ongkir = ""
    var kurir = ""
    var layanan = ""
    lateinit var adapter : AdapterKeranjang
    var listProduk = ArrayList<Produk>()

    private fun displayProduk(){
        listProduk = myDb.daoKeranjang().getAll() as ArrayList
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        rv_produk.adapter =  AdapterPengiriman(this, listProduk)
        rv_produk.layoutManager = layoutManager
    }

    private fun displayOngkir(_kurir: String, arrayList: ArrayList<Costs>){

        var arrayOngkir = ArrayList<Costs>()
        for (i in arrayList.indices){
            var ongkir = arrayList[i]
            if (i == 0){
                ongkir.isActive = true
            }
            arrayOngkir.add(ongkir)
        }

        setTotal(arrayOngkir[0].cost[0].value)
        ongkir = arrayOngkir[0].cost[0].value
        kurir = _kurir
        layanan = arrayOngkir[0].service

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        var adapter: AdapterKurir? = null
        adapter = AdapterKurir(arrayOngkir, _kurir, object : AdapterKurir.Listeners {
            override fun onClicked(data: Costs, index: Int) {
                val newArrayOngkir = ArrayList<Costs>()
                for (ongkir in arrayOngkir) {
                    ongkir.isActive = data.description == ongkir.description
                    newArrayOngkir.add(ongkir)
                }
                arrayOngkir = newArrayOngkir
                adapter!!.notifyDataSetChanged()
                setTotal(data.cost[0].value)

                ongkir = data.cost[0].value
                kurir = _kurir
                layanan = data.service
            }

        })
        rv_metode.adapter = adapter
        rv_metode.layoutManager = layoutManager
    }

    fun setTotal(ongkir: String) {
        tv_ongkir.text = Helper().gantiRupiah(ongkir)
        tv_total.text = Helper().gantiRupiah(Integer.valueOf(ongkir) + totalHarga)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onResume() {
        checkAlamat()
        displayProduk()
        super.onResume()
    }

}