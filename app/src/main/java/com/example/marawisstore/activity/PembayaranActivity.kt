package com.example.marawisstore.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Adapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marawisstore.R
import com.example.marawisstore.adapter.AdapterBank
import com.example.marawisstore.app.ApiConfig
import com.example.marawisstore.helper.Helper
import com.example.marawisstore.model.Bank
import com.example.marawisstore.model.Checkout
import com.example.marawisstore.model.ResponModel
import com.example.marawisstore.model.Transaksi
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_pembayaran.*
import kotlinx.android.synthetic.main.toolbar_baru.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PembayaranActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pembayaran)
        Helper().setToolbar(this, toolbar_baru, "Pembayaran")

        displayPembayaran()
    }

    fun displayPembayaran(){
        val arrBank = ArrayList<Bank>()
        arrBank.add(Bank("BCA", "989282123", "PT Marawis Almadinah", R.drawable.logo_bca))
        arrBank.add(Bank("MANDIRI", "1002982123", "PT Marawis Almadinah", R.drawable.logo_madiri))
        arrBank.add(Bank("BNI", "99822333212", "PT Marawis Almadinah", R.drawable.logo_bni))

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        rv_data.layoutManager = layoutManager
        rv_data.adapter = AdapterBank(arrBank, object : AdapterBank.Listeners {
            override fun onClicked(data: Bank, index: Int) {
                bayar(data)
            }
        })
    }

    private fun bayar(bank: Bank){
        val json = intent.getStringExtra("extra")!!.toString()
        val checkout = Gson().fromJson(json, Checkout::class.java)
        checkout.bank = bank.nama
        checkout.no_rekening = bank.rekening

        ApiConfig.instanceRetrofit.checkout(checkout).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                //Handle ketika gagal

            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                val respon = response.body()!!
                //Handle ketika sukses
                if (respon.success == 1) {

                    val jsBank = Gson().toJson(bank, Bank::class.java)
                    val jsTrnsaksi = Gson().toJson(respon.transaksi, Transaksi::class.java)
                    val jsCheckout = Gson().toJson(checkout, Checkout::class.java)

                    val intent = Intent(this@PembayaranActivity, SuccesActivity::class.java)
                    intent.putExtra("bank", jsBank)
                    intent.putExtra("transaksi", jsTrnsaksi)
                    intent.putExtra("checkout", jsCheckout)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@PembayaranActivity, respon.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onResume() {
        displayPembayaran()
        super.onResume()
    }
}