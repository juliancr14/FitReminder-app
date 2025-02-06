package com.cardoppc.fitreminder.view

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.cardoppc.fitreminder.R

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val bundle = intent.extras
        val email = bundle?.getString("email") ?: ""
        val provider = bundle?.getString("provider") ?: ""

        setup(email, provider)

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()
    }

    private fun setup(email: String, provider: String) {
        title = "Inicio"

        val txtEmail = findViewById<TextView>(R.id.txtVEmail)
        val txtVProveedor = findViewById<TextView>(R.id.txtVProveedor)
        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesion)

        txtEmail.text = email
        txtVProveedor.text = provider

        btnCerrarSesion.setOnClickListener {
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
