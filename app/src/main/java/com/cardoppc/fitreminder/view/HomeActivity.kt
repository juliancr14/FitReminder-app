package com.cardoppc.fitreminder.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.cardoppc.fitreminder.R

class HomeActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout

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
        //title = "Inicio"

        drawerLayout = findViewById(R.id.drawer_layout)
        val menuIcon = findViewById<View>(R.id.menu_icon)
        //val txtEmail = findViewById<TextView>(R.id.txtVEmail)
        //val txtVProveedor = findViewById<TextView>(R.id.txtVProveedor)
        //val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesion)

        //txtEmail.text = email
        //txtVProveedor.text = provider

        menuIcon.setOnClickListener {
            if (drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
                drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
            } else {
                drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
            }
        }

        //btnCerrarSesion.setOnClickListener {
        //    val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        //    prefs.clear()
        //    prefs.apply()
        //    onBackPressedDispatcher.onBackPressed()
        //}
    }
}
