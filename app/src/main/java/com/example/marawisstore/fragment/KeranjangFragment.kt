package com.example.marawisstore.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.marawisstore.R
import com.example.marawisstore.activity.*
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
import kotlinx.android.synthetic.main.item_harga.*

class KeranjangFragment : Fragment() {

    lateinit var myDb: MyDatabase
    lateinit var s: SharedPref

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_keranjang,container,false)
        init(view)
        myDb = MyDatabase.getInstance(requireActivity())!!
        s = SharedPref(requireActivity())

        mainButton()
        refreshApp()
        return view
    }

    private fun refreshApp() {
       swRefresh.setOnRefreshListener {
           displayProduk()
           hitungTotal()
           selectProduct()
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
                selectProduct()
            }

            override fun onDelete(position: Int) {
                listProduk.removeAt(position)
                adapter.notifyDataSetChanged()
                selectProduct()
                hitungTotal()
            }
        })

        rvProduk.adapter = adapter
        rvProduk.layoutManager = layoutManager
    }

    var hargaAsli = 0
    var totalHarga = 0
    var totalDiskon = 0
    var totalBerat = 0

    private fun hitungTotal(){
        val listProduk = myDb.daoKeranjang().getAll() as ArrayList
        var isSelectedAll = true
        totalHarga = 0
        totalDiskon = 0
        totalBerat = 0
        hargaAsli = 0
        if (listProduk.isEmpty()){
            emptyKeranjang.visibility = View.VISIBLE
            divHeader.visibility = View.GONE
            divFooter.visibility = View.GONE
        } else {
            for (produk in listProduk){
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
            divHeader.visibility = View.VISIBLE
            divFooter.visibility = View.VISIBLE
            emptyKeranjang.visibility = View.GONE
            cbAll.isChecked = isSelectedAll
            tvTotal.text = Helper().gantiRupiah(totalHarga)
        }
    }

    private fun selectProduct(){
        var selectProduct = false

        for (p in listProduk){
            if (p.selected) selectProduct = true
        }
        if (selectProduct){
            lyBayar.visibility = View.VISIBLE
            lyTotal.visibility = View.VISIBLE
            btmHarga.visibility = View.VISIBLE
            tvDelete.visibility = View.VISIBLE
        } else {
            lyBayar.visibility = View.GONE
            lyTotal.visibility = View.GONE
            btmHarga.visibility = View.GONE
            tvDelete.visibility = View.GONE
        }
    }

    @SuppressLint("CutPasteId")
    private fun mainButton(){
        btnBayar.setOnClickListener {
            if (s.getStatusLogin()){
                var isThereProduk = false
                for (p in listProduk){
                    if (p.selected) isThereProduk = true
                }

                if (isThereProduk){
                    val intent = Intent(activity,PengirimanActivity::class.java)
                    intent.putExtra("totalBerat","" + totalBerat)
                    intent.putExtra("totalHarga","" + totalHarga)
                    intent.putExtra("totalDiskon","" + totalDiskon)
                    intent.putExtra("hargaAsli","" + hargaAsli)
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(),"Tidak ada produk yang terpilih", Toast.LENGTH_SHORT).show()
                }
            } else {
                startActivity(Intent(requireActivity(), MasukActivity::class.java))
            }
        }

        tvDelete.setOnClickListener {
            var isSelectedAll = true
            val listDelete = ArrayList<Produk>()
            for (p in listProduk){
                if (p.selected){
                    listDelete.add(p)
                    isSelectedAll = false
                }
            }
            cbAll.isChecked = isSelectedAll
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

        btmHarga.setOnClickListener {
            var isThereProduk = false
            val bottomDialog = BottomSheetDialog(
                    requireActivity(), R.style.BottomSheetDialogtheme
            )
            val bottomSheetView = LayoutInflater.from(requireActivity().applicationContext).inflate(
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

    lateinit var emptyKeranjang: LinearLayout
    lateinit var lyBayar: LinearLayout
    lateinit var lyTotal: LinearLayout
    lateinit var btmHarga: RelativeLayout
    lateinit var btnBayar: RelativeLayout
    lateinit var btnBelanja: RelativeLayout
    lateinit var divHeader: RelativeLayout
    lateinit var divFooter: RelativeLayout
    lateinit var rvProduk: RecyclerView
    lateinit var tvTotal: TextView
    lateinit var tvDelete: TextView
    lateinit var swRefresh: SwipeRefreshLayout
    lateinit var cbAll: CheckBox

    private fun init(view: View){
        lyBayar = view.findViewById(R.id.ly_bayar)
        lyTotal = view.findViewById(R.id.ly_total)
        tvDelete = view.findViewById(R.id.tv_delete)
        btmHarga = view.findViewById(R.id.btm_harga)
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
        selectProduct()
        hitungTotal()
        displayProduk()
        super.onResume()
    }

}
