package com.cardoppc.fitreminder.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.cardoppc.fitreminder.R
import com.cardoppc.fitreminder.databinding.ActivityProgressBinding
import com.cardoppc.fitreminder.viewModel.ProgressViewModel
import com.cardoppc.fitreminder.viewModel.ProgressViewModelFactory

class ProgressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProgressBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private val progressViewModel: ProgressViewModel by viewModels { ProgressViewModelFactory(this) }

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
                    true
                }
                R.id.nav_update_info -> {
                    val intent = Intent(this, UpdateActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_logout -> {
                    val prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).edit()
                    prefs.clear()
                    prefs.apply()
                    val intent = Intent(this, AuthActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }.also {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }

        // Configuración del botón para registrar progreso
        binding.registerButton.setOnClickListener {
            val weightInput = binding.weightInput.text.toString()
            if (weightInput.isNotEmpty()) {
                progressViewModel.saveProgress(weightInput,
                    onSuccess = { showToast("Progreso registrado: $weightInput") },
                    onFailure = { showToast("Error al registrar progreso") }
                )
            } else {
                showToast("Por favor, ingresa un valor")
            }
        }
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}