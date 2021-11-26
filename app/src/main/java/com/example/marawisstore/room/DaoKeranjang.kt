package com.example.marawisstore.room

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.example.marawisstore.model.Produk

@Dao
interface DaoKeranjang {
    @Insert(onConflict = REPLACE)
    fun insert(data: Produk)

    @Delete
    fun delete(data: Produk)

    @Delete
    fun delete(data: List<Produk>)

    @Update
    fun update(data: Produk): Int

    @Query("SELECT * from keranjang ORDER BY kode_produk ASC")
    fun getAll(): List<Produk>

    @Query("SELECT * FROM keranjang WHERE kode_produk = :id LIMIT 1")
    fun getProduk(id: String): Produk

    @Query("DELETE FROM keranjang WHERE kode_produk = :id")
    fun deleteById(id: String): Int

    @Query("DELETE FROM keranjang")
    fun deleteAll(): Int
}