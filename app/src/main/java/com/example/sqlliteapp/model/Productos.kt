package com.example.sqliteapp.model
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.sqliteapp.db.HelperDB
class Productos(context: Context?) {
    private var helper: HelperDB? = null
    private var db: SQLiteDatabase? = null
    init {
        helper = HelperDB(context)
        db = helper!!.getWritableDatabase()
    }
    companion object {
        //TABLA PRODUCTOS
        val TABLE_NAME_PRODUCTOS = "productos"

        //nombre de los campos de la tabla contactos
        val COL_ID = "idproductos"
        val COL_IDCATEGORIA = "idcategoria"
        val COL_DESCRIPCION = "descripcion"
        val COL_PRECIO = "precio"
        val COL_CANTIDAD = "cantidad"

        //sentencia SQL para crear la tabla.
        val CREATE_TABLE_PRODUCTOS = (
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_PRODUCTOS + "("
                        + COL_ID + " integer primary key autoincrement,"
                        + COL_IDCATEGORIA + " integer NOT NULL,"
                        + COL_DESCRIPCION + " varchar(150) NOT NULL,"
                        + COL_PRECIO + " decimal(10,2) NOT NULL,"
                        + COL_CANTIDAD + " integer,"
                        + "FOREIGN KEY(idcategoria) REFERENCES categoria (idcategoria));"
        )
    }
    // ContentValues
    fun generarContentValues(
        idcategoria: Int?,
        descripcion: String?,
        precio: Double?,
        cantidad: Int?
    ): ContentValues? {
        val valores = ContentValues()
        valores.put(Productos.COL_IDCATEGORIA, idcategoria)
        valores.put(Productos.COL_DESCRIPCION, descripcion)
        valores.put(Productos.COL_PRECIO, precio)
        valores.put(Productos.COL_CANTIDAD, cantidad)
        return valores
    }
    //Agregar un nuevo registro
    fun addNewProducto(idcategoria: Int?, descripcion: String?, precio: Double?,
                       cantidad: Int?) {
        db!!.insert(
            TABLE_NAME_PRODUCTOS,
            null,
            generarContentValues(idcategoria, descripcion, precio, cantidad)
        )
    }
    // Eliminar un registro
    fun deleteProducto(id: Int) {
        db!!.delete(TABLE_NAME_PRODUCTOS, "$COL_ID=?", arrayOf(id.toString()))
    }
    //Modificar un registro
    fun updateProducto(
        id: Int,
        idcategoria: Int?,
        descripcion: String?,
        precio: Double?,
        cantidad: Int?
    ) {
        db!!.update(
            TABLE_NAME_PRODUCTOS, generarContentValues(idcategoria, descripcion,
                precio, cantidad),
            "$COL_ID=?", arrayOf(id.toString())
        )
    }
    // Mostrar un registro particular
    fun searchProducto(id: Int): Cursor? {
        val columns = arrayOf(COL_ID, COL_IDCATEGORIA, COL_DESCRIPCION, COL_PRECIO,
            COL_CANTIDAD)
        return db!!.query(
            TABLE_NAME_PRODUCTOS, columns,
            "$COL_ID=?", arrayOf(id.toString()), null, null, null
        )
    }
    // Mostrar todos los registros
    fun searchProductosAll(): Cursor? {
        val columns = arrayOf(COL_ID, COL_IDCATEGORIA, COL_DESCRIPCION, COL_PRECIO,
            COL_CANTIDAD)
        return db!!.query(
            TABLE_NAME_PRODUCTOS, columns,
            null, null, null, null, "${Productos.COL_DESCRIPCION} ASC"
        )
    }
}
