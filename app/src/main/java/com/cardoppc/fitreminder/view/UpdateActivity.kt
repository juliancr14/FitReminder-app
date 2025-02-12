package com.cardoppc.fitreminder.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cardoppc.fitreminder.R
import com.cardoppc.fitreminder.databinding.ActivityUpdateBinding
import com.cardoppc.fitreminder.viewModel.UpdateViewModel
import com.cardoppc.fitreminder.viewModel.UpdateViewModelFactory
import androidx.appcompat.app.ActionBarDrawerToggle

class UpdateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateBinding
    private val updateViewModel: UpdateViewModel by viewModels { UpdateViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Actualizar Información"

        // Configurar DrawerLayout y Toggle para el menú hamburguesa
        val toggle = ActionBarDrawerToggle(
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
                R.id.nav_progress_history -> {
                    val intent = Intent(this, ProgressActivity::class.java)
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
            }
        }

        // Configuración del botón para guardar cambios
        binding.saveButton.setOnClickListener {
            val weightInput = binding.editWeight.text.toString()
            val heightInput = binding.editHeight.text.toString()
            val nameInput = binding.editName.text.toString()

            if (weightInput.isNotEmpty() && heightInput.isNotEmpty() && nameInput.isNotEmpty()) {
                val userInfo = mapOf(
                    "weight" to weightInput,
                    "height" to heightInput,
                    "name" to nameInput
                )

                updateViewModel.updateUserProfile(userInfo,
                    onSuccess = { showToast("Información actualizada") },
                    onFailure = { showToast("Error al actualizar información") }
                )
            } else {
                showToast("Por favor, completa todos los campos")
            }
        }
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}