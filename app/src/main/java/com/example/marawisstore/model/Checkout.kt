package com.example.marawisstore.model

class Checkout {
    lateinit var costumer_id:String
    lateinit var total_qty:String
    lateinit var total_harga:String
    lateinit var total_berat:String
    lateinit var nama:String
    lateinit var telpon:String
    lateinit var alamat:String
    lateinit var status:String
    lateinit var layanan:String
    lateinit var kurir:String
    lateinit var ongkir:String
    lateinit var resi:String
    lateinit var bank:String
    lateinit var no_rekening:String
    lateinit var catatan:String
    lateinit var jumlah_bayar:String
    lateinit var expired_at:String
    var produks = ArrayList<Item>()

    class Item{
        lateinit var kode_produk:String
        lateinit var qty:String
        lateinit var berat:String
        lateinit var harga:String
    }

}
