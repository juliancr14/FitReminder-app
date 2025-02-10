package com.cardoppc.fitreminder.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.cardoppc.fitreminder.R
import com.cardoppc.fitreminder.databinding.ActivityProgressBinding

class ProgressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProgressBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Progreso del Mes"

        // Configurar DrawerLayout y Toggle para el menú hamburguesa
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Configurar navegación del NavigationView
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.nav_update -> {
                    val intent = Intent(this, UpdateActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.nav_logout -> {
                    val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                    prefs.clear()
                    prefs.apply()

                    val intent = Intent(this, AuthActivity::class.java)
                    startActivity(intent)
                    finish()

                    true
                }
                else -> false
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Configuración adicional
        binding.registerButton.setOnClickListener {
            val weightInput = binding.weightInput.text.toString()
            if (weightInput.isNotEmpty()) {
                // Guardar o procesar el nuevo peso ingresado
                // Aquí puedes manejar lógica adicional
                showToast("Nuevo progreso registrado: $weightInput")
            } else {
                showToast("Por favor, ingresa un valor")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}