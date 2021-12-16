package com.example.marawisstore.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.marawisstore.MainActivity
import com.example.marawisstore.R
import com.example.marawisstore.activity.*
import com.example.marawisstore.adapter.AdapterKeranjang
import com.example.marawisstore.helper.SharedPref
import com.example.marawisstore.model.Produk
import com.example.marawisstore.room.MyDatabase
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_pengiriman.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_keranjang.*
import kotlinx.android.synthetic.main.toolbar_costume.*

class KeranjangFragment : Fragment() {

    lateinit var myDb: MyDatabase
    lateinit var s: SharedPref

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_keranjang,container,false)

        myDb = MyDatabase.getInstance(requireActivity())!!
        s = SharedPref(requireActivity())

        init(view)
        mainButton()
        refreshApp()
        emptyKeranjang()
        return view
    }

    private fun refreshApp() {
       swRefresh.setOnRefreshListener {
           displayProduk()
           swRefresh.isRefreshing = false
       }
    }

    lateinit var adapter : AdapterKeranjang
    var listProduk = ArrayList<Produk>()
    private fun displayProduk(){
        listProduk = myDb.daoKeranjang().getAll() as ArrayList
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        adapter = AdapterKeranjang(requireActivity(), listProduk, object : AdapterKeranjang.Listeners{
            override fun onUpdate() {
                hitungTotal()
                emptyKeranjang()
            }

            override fun onDelete(position: Int) {
                listProduk.removeAt(position)
                adapter.notifyDataSetChanged()
            }
        })

        rvProduk.adapter = adapter
        rvProduk.layoutManager = layoutManager
    }

    var totalHarga = 0
    var totalBerat = 0
    private fun hitungTotal(){
        val listProduk = myDb.daoKeranjang().getAll() as ArrayList

        totalHarga = 0
        var isSelectedAll = true
        for (produk in listProduk){
            if(produk.selected){
                val berat = Integer.valueOf(produk.berat)
                totalBerat += (berat * produk.jumlah)
                val harga = Integer.valueOf(produk.harga)
                totalHarga += (harga * produk.jumlah)
            }else{
                isSelectedAll = false
            }
        }
        cbAll.isChecked = isSelectedAll
        tvTotal.text = com.example.marawisstore.helper.Helper().gantiRupiah(totalHarga)
    }

    private fun emptyKeranjang(){
        val listProduks = myDb.daoKeranjang().getAll() as ArrayList

        if (listProduks.isEmpty()){
            emptyKeranjang.visibility = View.VISIBLE
            divHeader.visibility = View.GONE
            divFooter.visibility = View.GONE
        } else {
            emptyKeranjang.visibility = View.GONE
        }
    }

    private fun mainButton(){
        btnBayar.setOnClickListener {

            if (s.getStatusLogin()){
                var isThereProduk = false

                for (p in listProduk){
                    if (p.selected) isThereProduk = true
                }

                if (isThereProduk){
                    val intent = Intent(activity,PengirimanActivity::class.java)
                    intent.putExtra("extraa","" + totalBerat)
                    intent.putExtra("extra","" + totalHarga)
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(),"Tidak ada produk yang terpilih", Toast.LENGTH_SHORT).show()
                }
            } else {
                startActivity(Intent(requireActivity(), MasukActivity::class.java))
            }
        }

        tvDelete.setOnClickListener {
            val listDelete = ArrayList<Produk>()
            for (p in listProduk){
                if (p.selected) listDelete.add(p)
            }
            delete(listDelete)
        }

        btnBelanja.setOnClickListener {
            startActivity(Intent(activity, AllProdukLainActivity::class.java))
        }


        cbAll.setOnClickListener {
            for (i in listProduk.indices){
                val produk = listProduk[i]
                produk.selected = cbAll.isChecked
                listProduk[i] = produk
            }
            adapter.notifyDataSetChanged()
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

    lateinit var tvDelete: TextView
    lateinit var rvProduk: RecyclerView
    lateinit var tvTotal: TextView
    lateinit var btnBayar: RelativeLayout
    lateinit var btnBelanja: RelativeLayout
    lateinit var emptyKeranjang: LinearLayout
    lateinit var divHeader: RelativeLayout
    lateinit var divFooter: LinearLayout
    lateinit var swRefresh: SwipeRefreshLayout
    lateinit var cbAll: CheckBox

    private fun init(view: View){
        tvDelete = view.findViewById(R.id.tv_delete)
        btnBelanja = view.findViewById(R.id.btn_belanja)
        rvProduk = view.findViewById(R.id.rv_produk)
        tvTotal = view.findViewById(R.id.tv_total)
        swRefresh = view.findViewById(R.id.swipeToRefresh)
        btnBayar = view.findViewById(R.id.btn_bayar)
        divFooter = view.findViewById(R.id.div_footer)
        divHeader = view.findViewById(R.id.div_header)
        emptyKeranjang = view.findViewById(R.id.empty_keranjang)
        cbAll = view.findViewById(R.id.cb_all)
    }

    override fun onResume() {
        hitungTotal()
        mainButton()
        displayProduk()
        emptyKeranjang()
        super.onResume()
    }

}