package com.example.marawisstore.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marawisstore.R
import com.example.marawisstore.adapter.AdapterProdukTransaksi
import com.example.marawisstore.helper.Helper
import com.example.marawisstore.helper.SharedPref
import com.example.marawisstore.model.DetailTransaksi
import com.example.marawisstore.model.Transaksi
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_detail_transaksi.*
import kotlinx.android.synthetic.main.item_produk_transaksi.*
import kotlinx.android.synthetic.main.toolbar_baru.*

class DetailTransaksiActivity : AppCompatActivity() {

    lateinit var s:SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_transaksi)
        Helper().setToolbar(this, toolbar_baru, "Detail Pesanan")

        val json = intent.getStringExtra("transaksi")
        val transaksi = Gson().fromJson(json, Transaksi::class.java)

        setData(transaksi)
        displayProduk(transaksi.details)
    }

    fun setData(t: Transaksi){
        tv_status.text = t.status
        tv_kodetrx.text = t.kode_trx
        val formatBaru = "dd MMMM yyyy, kk:mm"
        tv_tgl.text = Helper().convertTanggal(t.created_at, formatBaru)
        tv_nama_pelanggan.text = t.nama
        tv_alamat.text = t.alamat
        tv_telpon.text = t.telpon
        tv_metode.text = t.bank + " Transfer"
        tv_totalBelanja.text = Helper().gantiRupiah(t.total_harga)
        tv_ongkir.text = Helper().gantiRupiah(t.ongkir)
        tv_total.text = Helper().gantiRupiah(t.jumlah_bayar)
    }

    fun displayProduk(transaksis: ArrayList<DetailTransaksi>) {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        rv_produk.adapter = AdapterProdukTransaksi(transaksis)
        rv_produk.layoutManager = layoutManager
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}