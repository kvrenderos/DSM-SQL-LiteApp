package com.example.sqlliteapp

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sqlliteapp.db.HelperDB
import com.example.sqlliteapp.model.Categoria
import com.example.sqlliteapp.model.Productos

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var managerCategoria: Categoria? = null
    private var managerProductos: Productos? = null
    private var dbHelper: HelperDB? = null
    private var db: SQLiteDatabase? = null
    private var cursor: Cursor? = null

    private var txtIdDB: TextView? = null
    private var txtId: EditText? = null
    private var txtNombre: EditText? = null
    private var txtPrecio: EditText? = null
    private var txtCantidad: EditText? = null
    private var cmbCategorias: Spinner? = null

    private var btnAgregar: Button? = null
    private var btnActualizar: Button? = null
    private var btnEliminar: Button? = null
    private var btnBuscar: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Referencias de los controles
        txtIdDB = findViewById(R.id.txtIdDB)
        txtId = findViewById(R.id.txtId)
        txtNombre = findViewById(R.id.txtNombre)
        txtPrecio = findViewById(R.id.txtPrecio)
        txtCantidad = findViewById(R.id.txtCantidad)

        cmbCategorias = findViewById(R.id.cmbCategorias)

        btnAgregar = findViewById(R.id.btnAgregar)
        btnActualizar = findViewById(R.id.btnActualizar)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnBuscar = findViewById(R.id.btnBuscar)

        // Inicializar base de datos
        dbHelper = HelperDB(this)
        db = dbHelper?.writableDatabase

        // Cargar categorías
        setSpinnerCategorias()

        // Eventos
        btnAgregar?.setOnClickListener(this)
        btnActualizar?.setOnClickListener(this)
        btnEliminar?.setOnClickListener(this)
        btnBuscar?.setOnClickListener(this)
    }

    private fun setSpinnerCategorias() {

        managerCategoria = Categoria(this)

        // Insertar categorías por defecto
        managerCategoria?.insertValuesDefault()

        cursor = managerCategoria?.showAllCategoria()

        val categorias = ArrayList<String>()

        if (cursor != null && cursor!!.count > 0) {

            cursor!!.moveToFirst()

            do {
                categorias.add(cursor!!.getString(1))
            } while (cursor!!.moveToNext())
        }

        val adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categorias
        )

        adaptador.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        cmbCategorias?.adapter = adaptador
    }

    override fun onClick(view: View) {

        managerProductos = Productos(this)

        val nombre = txtNombre?.text.toString().trim()
        val precio = txtPrecio?.text.toString().trim()
        val cantidad = txtCantidad?.text.toString().trim()
        val idproducto = txtId?.text.toString().trim()

        val categoria =
            cmbCategorias?.selectedItem?.toString()?.trim() ?: ""

        val idcategoria =
            managerCategoria?.searchID(categoria)

        // Verificar conexión
        if (db == null) {

            Toast.makeText(
                this,
                "No se puede conectar a la Base de Datos",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        when (view.id) {

            // =========================
            // AGREGAR
            // =========================
            R.id.btnAgregar -> {

                if (verificarFormulario("insertar")) {

                    try {

                        managerProductos?.addNewProducto(
                            idcategoria,
                            nombre,
                            precio.toDouble(),
                            cantidad.toInt()
                        )

                        Toast.makeText(
                            this,
                            "Producto agregado",
                            Toast.LENGTH_LONG
                        ).show()

                        limpiarFormulario()

                    } catch (e: Exception) {

                        Toast.makeText(
                            this,
                            "Error al agregar: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            // =========================
            // ACTUALIZAR
            // =========================
            R.id.btnActualizar -> {

                if (verificarFormulario("actualizar")) {

                    try {

                        managerProductos?.updateProducto(
                            idproducto.toInt(),
                            idcategoria,
                            nombre,
                            precio.toDouble(),
                            cantidad.toInt()
                        )

                        Toast.makeText(
                            this,
                            "Producto actualizado",
                            Toast.LENGTH_LONG
                        ).show()

                        limpiarFormulario()

                    } catch (e: Exception) {

                        Toast.makeText(
                            this,
                            "Error al actualizar: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            // =========================
            // ELIMINAR
            // =========================
            R.id.btnEliminar -> {

                if (verificarFormulario("eliminar")) {

                    try {

                        managerProductos?.deleteProducto(
                            idproducto.toInt()
                        )

                        Toast.makeText(
                            this,
                            "Producto eliminado",
                            Toast.LENGTH_LONG
                        ).show()

                        limpiarFormulario()

                    } catch (e: Exception) {

                        Toast.makeText(
                            this,
                            "Error al eliminar: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            // =========================
            // BUSCAR
            // =========================
            R.id.btnBuscar -> {

                if (verificarFormulario("buscar")) {

                    buscarProducto(idproducto)
                }
            }
        }
    }

    // ==========================================
    // BUSCAR PRODUCTO
    // ==========================================

    private fun buscarProducto(id: String) {

        try {

            val idProducto = id.toInt()

            val resultado =
                managerProductos?.searchProducto(idProducto)

            if (resultado != null && resultado.moveToFirst()) {

                // Columna 0 = idproductos
                txtId?.setText(
                    resultado.getString(0)
                )

                // Columna 1 = idcategoria
                val idCategoria =
                    resultado.getInt(1)

                // Columna 2 = descripcion
                txtNombre?.setText(
                    resultado.getString(2)
                )

                // Columna 3 = precio
                txtPrecio?.setText(
                    resultado.getString(3)
                )

                // Columna 4 = cantidad
                txtCantidad?.setText(
                    resultado.getString(4)
                )

                // Buscar nombre de la categoría
                val nombreCategoria =
                    managerCategoria?.searchNombre(idCategoria)

                // Seleccionar categoría en el Spinner
                if (nombreCategoria != null) {

                    val adaptador =
                        cmbCategorias?.adapter

                    if (adaptador != null) {

                        for (i in 0 until adaptador.count) {

                            if (adaptador.getItem(i)
                                    .toString() == nombreCategoria
                            ) {

                                cmbCategorias?.setSelection(i)
                                break
                            }
                        }
                    }
                }

                Toast.makeText(
                    this,
                    "Producto encontrado",
                    Toast.LENGTH_LONG
                ).show()

                resultado.close()

            } else {

                Toast.makeText(
                    this,
                    "Producto no encontrado",
                    Toast.LENGTH_LONG
                ).show()
            }

        } catch (e: NumberFormatException) {

            Toast.makeText(
                this,
                "El ID debe ser un número válido",
                Toast.LENGTH_LONG
            ).show()

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Error al buscar: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // ==========================================
    // VALIDAR FORMULARIO
    // ==========================================

    private fun verificarFormulario(opc: String): Boolean {

        var response = true

        var notificacion =
            "Se han generado algunos errores. Favor verifíquelos."

        val nombre =
            txtNombre?.text.toString().trim()

        val precio =
            txtPrecio?.text.toString().trim()

        val cantidad =
            txtCantidad?.text.toString().trim()

        val idproducto =
            txtId?.text.toString().trim()

        // =========================
        // INSERTAR / ACTUALIZAR
        // =========================

        if (opc == "insertar" || opc == "actualizar") {

            if (nombre.isEmpty()) {

                txtNombre?.error =
                    "Ingrese el nombre del producto"

                txtNombre?.requestFocus()

                response = false
            }

            if (precio.isEmpty()) {

                txtPrecio?.error =
                    "Ingrese el precio del producto"

                txtPrecio?.requestFocus()

                response = false

            } else {

                try {

                    precio.toDouble()

                } catch (e: NumberFormatException) {

                    txtPrecio?.error =
                        "Ingrese un precio válido"

                    response = false
                }
            }

            if (cantidad.isEmpty()) {

                txtCantidad?.error =
                    "Ingrese la cantidad inicial"

                txtCantidad?.requestFocus()

                response = false

            } else {

                try {

                    cantidad.toInt()

                } catch (e: NumberFormatException) {

                    txtCantidad?.error =
                        "Ingrese una cantidad válida"

                    response = false
                }
            }

            // Para actualizar se necesita ID
            if (opc == "actualizar" && idproducto.isEmpty()) {

                notificacion =
                    "No se ha seleccionado un producto"

                response = false
            }
        }

        // =========================
        // ELIMINAR / BUSCAR
        // =========================

        else if (opc == "eliminar" || opc == "buscar") {

            if (idproducto.isEmpty()) {

                notificacion =
                    "Ingrese el ID del producto"

                response = false
            } else {

                try {

                    idproducto.toInt()

                } catch (e: NumberFormatException) {

                    notificacion =
                        "El ID debe ser un número válido"

                    response = false
                }
            }
        }

        // Mostrar errores
        if (!response) {

            Toast.makeText(
                this,
                notificacion,
                Toast.LENGTH_LONG
            ).show()
        }

        return response
    }

    // ==========================================
    // LIMPIAR FORMULARIO
    // ==========================================

    private fun limpiarFormulario() {

        txtId?.setText("")
        txtNombre?.setText("")
        txtPrecio?.setText("")
        txtCantidad?.setText("")

        txtNombre?.requestFocus()
    }

    // ==========================================
    // CERRAR RECURSOS
    // ==========================================

    override fun onDestroy() {

        super.onDestroy()

        cursor?.close()
        db?.close()
        dbHelper?.close()
    }
}
