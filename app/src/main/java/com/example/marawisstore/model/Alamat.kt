package com.example.marawisstore.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alamat")
class Alamat {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idTb")
    var idTb = 0

    var id = 0
    var nama = ""
    var telpon = ""
    var type = ""
    var alamat = ""

    var id_provinsi = 0
    var id_kota = 0
    var provinsi = ""
    var kota = ""
    var kodepos = ""
    var isSelected = false

}