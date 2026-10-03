package com.example.sqlliteapp.model
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.sqlliteapp.db.HelperDB
class Categoria(context: Context?) {
    private var helper: HelperDB? = null
    private var db: SQLiteDatabase? = null
    init {
        helper = HelperDB(context)
        db = helper!!.getWritableDatabase()
    }
    companion object {
        //TABLA PRODUCTOS
        val TABLE_NAME_CATEGORIA = "categoria"
        //nombre de los campos de la tabla contactos
        val COL_ID = "idcategoria"
        val COL_NOMBRE = "nombre"
        //sentencia SQL para crear la tabla
        val CREATE_TABLE_CATEGORIA = (
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_CATEGORIA + "("
                        + COL_ID + " integer primary key autoincrement,"
                        + COL_NOMBRE + " varchar(50) NOT NULL);"
                )
    }
    // ContentValues
    fun generarContentValues(
        nombre: String?
    ): ContentValues? {
        val valores = ContentValues()
        valores.put(COL_NOMBRE, nombre)
        return valores
    }
    fun insertValuesDefault() {
        val categories = arrayOf(
            "Abarrotes",
            "Carnes",
            "Embutidos",
            "Mariscos",
            "Pescado",
            "Bebidas",
            "Verduras",
            "Frutas",
            "Bebidas Carbonatadas",
            "Bebidas no carbonatadas"
        )
// Verificacion si existen registros precargados
        val columns = arrayOf(COL_ID, COL_NOMBRE)
        var cursor: Cursor? =
            db!!.query(TABLE_NAME_CATEGORIA, columns, null, null, null, null, null)
// Validando que se ingrese la informacion solamente una vez, cuando se instala por primera vez la aplicacion
                if (cursor == null || cursor!!.count <= 0) {
// Registrando categorias por defecto
                    for (item in categories) {
                        db!!.insert(TABLE_NAME_CATEGORIA, null, generarContentValues(item))
                    }
                }
    }
    fun showAllCategoria(): Cursor? {
        val columns = arrayOf(COL_ID, COL_NOMBRE)
        return db!!.query(
            TABLE_NAME_CATEGORIA, columns,
            null, null, null, null, "$COL_NOMBRE ASC"
        )
    }
// Debido a que el Spinner solamente guarda el nombre, esta funcion nos ayudara a recuperar el ID de la categoria
    fun searchID(nombre: String): Int? {
        val columns = arrayOf(COL_ID, COL_NOMBRE)
        var cursor: Cursor? = db!!.query(
            TABLE_NAME_CATEGORIA, columns,
            "$COL_NOMBRE=?", arrayOf(nombre.toString()), null, null, null
        )
        cursor!!.moveToFirst()
        return cursor!!.getInt(0)
    }
    fun searchNombre(id: Int): String? {
        val columns = arrayOf(COL_ID, COL_NOMBRE)
        var cursor: Cursor? = db!!.query(
            TABLE_NAME_CATEGORIA, columns,
            "$COL_ID=?", arrayOf(id.toString()), null, null, null
        )
        cursor!!.moveToFirst()
        return cursor!!.getString(1)
    }
}