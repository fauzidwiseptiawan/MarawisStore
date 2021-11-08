package com.example.marawisstore.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.example.marawisstore.R
import com.example.marawisstore.app.ApiConfigAlamat
import com.example.marawisstore.helper.Helper
import com.example.marawisstore.model.Alamat
import com.example.marawisstore.model.ModelAlamat
import com.example.marawisstore.model.ResponModel
import com.example.marawisstore.room.MyDatabase
import com.example.marawisstore.util.ApiKey
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_tambah_alamat.*
import kotlinx.android.synthetic.main.toolbar_baru.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TambahAlamatActivity : AppCompatActivity() {

    var provinsi = ModelAlamat.Provinsi()
    var kota = ModelAlamat.Provinsi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_alamat)
        //Set Toolbar
        Helper().setToolbar(this, toolbar_baru, "Tambah Alamat")

        mainButton()
        getProvinsi()
    }

    private fun mainButton(){
        btn_simpan.setOnClickListener {
            simpan()
        }
    }

    private fun simpan(){
        if(edt_nama.text.isEmpty()){
            error(edt_nama)
            return
        } else if(edt_telpon.text.isEmpty()){
            error(edt_telpon)
            return
        } else if(edt_type.text.isEmpty()){
            error(edt_type)
            return
        } else if(edt_alamat.text.isEmpty()){
            error(edt_alamat)
            return
        } else if(edt_kodePos.text.isEmpty()){
            error(edt_kodePos)
            return
        }

        if (provinsi.province_id == "0") {
            toast("Silahkan pilih provinsi")
            return
        }

        if (kota.city_id == "0") {
            toast("Silahkan pilih Kota")
            return
        }

        val alamat = Alamat()
        alamat.nama = edt_nama.text.toString()
        alamat.type = edt_type.text.toString()
        alamat.telpon = edt_telpon.text.toString()
        alamat.alamat = edt_alamat.text.toString()
        alamat.kodepos = edt_kodePos.text.toString()

        alamat.id_provinsi = Integer.valueOf(provinsi.province_id)
        alamat.provinsi = provinsi.province
        alamat.id_kota = Integer.valueOf(kota.city_id)
        alamat.kota = kota.city_name

        insert(alamat)
    }

    private fun error(editText: EditText) {
        editText.error = "Kolom tidak boleh kosong"
        editText.requestFocus()
    }

    fun toast(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }

    private fun getProvinsi(){
        ApiConfigAlamat.instanceRetrofit.getProvinsi(ApiKey.key).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                //Handle ketika gagal

            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {

                if (response.isSuccessful) {
//
                    pb.visibility = View.GONE
                    div_provinsi.visibility = View.VISIBLE

                    val res = response.body()!!
                    val arryString = ArrayList<String>()

                    val listProvinsi = res.rajaongkir.results
                    for (prov in listProvinsi){
                        arryString.add(prov.province)
                    }

                    val adapter = ArrayAdapter<Any>(this@TambahAlamatActivity, R.layout.item_dropdown, arryString.toTypedArray())
                    spn_provinsi.setAdapter(adapter)
                    spn_provinsi.setOnItemClickListener { parent, view, position, id ->

                        if(position != 0) {
                            provinsi = listProvinsi[position - 0]
                            val idProv = provinsi.province_id
                            getKota(idProv)

                        }
                    }

                }else {
                    Log.d("Error","gagal memuat data:" + response.message())
                }
            }

        })
    }

    private fun getKota(id: String){
        ApiConfigAlamat.instanceRetrofit.getKota(ApiKey.key, id).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                //Handle ketika gagal

            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {

                if (response.isSuccessful) {
//
                    pb.visibility = View.GONE
                    div_kota.visibility = View.VISIBLE

                    val res = response.body()!!
                    val listArray = res.rajaongkir.results

                    val arryString = ArrayList<String>()
                    for (kota in listArray){
                        arryString.add(kota.type + " " + kota.city_name)
                    }

                    val adapter = ArrayAdapter<Any>(this@TambahAlamatActivity, R.layout.item_dropdown, arryString.toTypedArray())
                    spn_kota.setAdapter(adapter)
                    spn_kota.setOnItemClickListener { parent, view, position, id ->

                        if(position != 0) {
                            kota = listArray[position - 0]
                            val kodePos = kota.postal_code
                            edt_kodePos.setText(kodePos)
                        }
                    }

                }else {
                    Log.d("Error","gagal memuat data:" + response.message())
                }
            }

        })
    }

    //Insert Data Ke Alamat
    private fun insert(data: Alamat){
        val myDb = MyDatabase.getInstance(this)!!
        if (myDb.daoAlamat().getByStatus(true) == null){
            data.isSelected = true
        }

        CompositeDisposable().add(Observable.fromCallable { myDb.daoAlamat().insert(data) }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Toast.makeText(this,"Yay! Alamat berhasil ditambahkan",Toast.LENGTH_SHORT).show()
                    onBackPressed()
                })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}