package com.example.marawisstore.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Adapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marawisstore.MainActivity
import com.example.marawisstore.R
import com.example.marawisstore.adapter.AdapterBank
import com.example.marawisstore.app.ApiConfig
import com.example.marawisstore.helper.Helper
import com.example.marawisstore.model.Bank
import com.example.marawisstore.model.Checkout
import com.example.marawisstore.model.ResponModel
import com.example.marawisstore.model.Transaksi
import com.example.marawisstore.room.MyDatabase
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_detail_transaksi.*
import kotlinx.android.synthetic.main.activity_pembayaran.*
import kotlinx.android.synthetic.main.activity_succes.*
import kotlinx.android.synthetic.main.activity_succes.tv_tgl
import kotlinx.android.synthetic.main.toolbar_baru.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SuccesActivity : AppCompatActivity() {

    var nominal = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_succes)
        Helper().setToolbar(this, toolbar_baru, "Bank Transfer")

        setValues()
        mainButton()
    }

    fun mainButton(){
        btn_copyNoRek.setOnClickListener {
            copyText(tv_nomorRekening.text.toString())
        }

        btn_copyNominal.setOnClickListener {
            copyText(nominal.toString())
        }

        btn_cekStatus.setOnClickListener {
            startActivity(Intent(this, RiwayatActivity::class.java))
        }
    }

    fun copyText(text: String){
        val copyManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val copyText = ClipData.newPlainText("text", text)
        copyManager.setPrimaryClip(copyText)

        Toast.makeText(this, "Text berhasil dicopy", Toast.LENGTH_LONG).show()
    }

    fun setValues(){
        val jsBank = intent.getStringExtra("bank")
        val jsTransaksi = intent.getStringExtra("transaksi")
        val jsCheckout = intent.getStringExtra("checkout")

        val bank = Gson().fromJson(jsBank, Bank::class.java)
        val transaksi = Gson().fromJson(jsTransaksi, Transaksi::class.java)
        val checkout = Gson().fromJson(jsCheckout, Checkout::class.java)

//        Hapus keranjang
        val myDb = MyDatabase.getInstance(this)!!

        for (produk in checkout.produks){
            myDb.daoKeranjang().deleteById(produk.kode_produk)
        }

        tv_nomorRekening.text = bank.rekening
        tv_namaPenerima.text = bank.penerima
        image_bank.setImageResource(bank.image)

        val formatBaru = "dd MMMM yyyy, kk:mm"
        tv_btsBayar.text = Helper().convertTanggal(transaksi.expired_at, formatBaru)

        nominal = Integer.valueOf(transaksi.jumlah_bayar)
        tv_nominal.text = Helper().gantiRupiah(nominal)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onResume() {
        setValues()
        super.onResume()
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
        super.onBackPressed()
    }

}