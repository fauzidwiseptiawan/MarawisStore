package com.example.marawisstore.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.example.marawisstore.MainActivity
import com.example.marawisstore.R
import com.example.marawisstore.activity.*
import com.example.marawisstore.helper.SharedPref

class AkunFragment : Fragment() {

    lateinit var s:SharedPref
    lateinit var btnLogout:TextView
    lateinit var btnWishList:RelativeLayout
    lateinit var btnRiwayat:RelativeLayout
    lateinit var btnAlamat:RelativeLayout
    lateinit var btnKeranjang:RelativeLayout
    lateinit var tvNamaProfil:TextView
    lateinit var tvTelponProfil:TextView
    lateinit var tvEmailProfil:TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_akun,container,false)
        init(view)

        s = SharedPref(requireActivity())

        mainButton()
        setData()
        return view
    }

    fun mainButton(){
        btnLogout.setOnClickListener{
            startActivity(Intent(activity, MainActivity::class.java))
            Toast.makeText(activity,"Anda berhasil logout", Toast.LENGTH_SHORT).show()
            s.setStatusLogin(false)
        }

        btnWishList.setOnClickListener {
            startActivity(Intent(activity, FavoritProdukActivity::class.java))
        }

        btnKeranjang.setOnClickListener {
            startActivity(Intent(activity, KeranjangProdukActivity::class.java))
        }

        btnAlamat.setOnClickListener {
            startActivity(Intent(activity, ListAlamatActivity::class.java))
        }

        btnRiwayat.setOnClickListener {
            startActivity(Intent(activity, RiwayatActivity::class.java))
        }
    }

    fun setData() {
        if(s.getUser() == null){
            val intent = Intent(activity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            return
        }

        val user = s.getUser()!!

        tvNamaProfil.text = user.username
        tvTelponProfil.text = user.telpon
        tvEmailProfil.text = user.email
    }

    private fun init(view: View) {
        btnLogout = view.findViewById(R.id.btn_prosesLogout)
        btnRiwayat = view.findViewById(R.id.btn_riwayat)
        btnAlamat = view.findViewById(R.id.btn_alamat)
        btnWishList = view.findViewById(R.id.btn_wishList)
        btnKeranjang = view.findViewById(R.id.btn_keranjang)
        tvNamaProfil = view.findViewById(R.id.tv_nama_profil)
        tvEmailProfil = view.findViewById(R.id.tv_email_profil)
        tvTelponProfil = view.findViewById(R.id.tv_telpon_profil)
    }

}