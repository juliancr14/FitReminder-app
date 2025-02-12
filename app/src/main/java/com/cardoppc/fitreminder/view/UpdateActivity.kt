package com.cardoppc.fitreminder.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.cardoppc.fitreminder.R
import com.cardoppc.fitreminder.databinding.ActivityUpdateBinding
import com.cardoppc.fitreminder.viewModel.UpdateViewModel
import com.cardoppc.fitreminder.viewModel.UpdateViewModelFactory

class UpdateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private val updateViewModel: UpdateViewModel by viewModels { UpdateViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Actualizar Información"

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
            }.also {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }

        // Cargar la información del usuario desde Firestore
        loadUserProfile()

        // Configuración del botón para guardar cambios
        binding.saveButton.setOnClickListener {
            val updatedInfo = mutableMapOf<String, Any>()

            // Agregar solo los campos que el usuario haya modificado
            val newName = binding.editName.text.toString()
            if (newName.isNotEmpty()) {
                updatedInfo["name"] = newName
            }

            val newWeight = binding.editWeight.text.toString()
            if (newWeight.isNotEmpty()) {
                updatedInfo["weight"] = newWeight
            }

            val newHeight = binding.editHeight.text.toString()
            if (newHeight.isNotEmpty()) {
                updatedInfo["height"] = newHeight
            }

            // Si hay datos para actualizar, llamar al ViewModel
            if (updatedInfo.isNotEmpty()) {
                updateViewModel.updateUserProfile(updatedInfo,
                    onSuccess = {
                        showToast("Información actualizada")
                        loadUserProfile() // Recargar la información para actualizar la UI
                    },
                    onFailure = { showToast("Error al actualizar información") }
                )
            } else {
                showToast("No hay cambios para guardar")
            }
        }
    }

    private fun loadUserProfile() {
        updateViewModel.fetchUserProfile(
            onSuccess = { userProfile ->
                binding.userName.text = userProfile["name"]?.toString() ?: "Falta información"
                binding.userWeight.text = "Peso: ${userProfile["weight"] ?: "Falta información"} kg"
                binding.userHeight.text = "Estatura: ${userProfile["height"] ?: "Falta información"} cm"
            },
            onFailure = {
                binding.userName.text = "Falta información"
                binding.userWeight.text = "Peso: Falta información"
                binding.userHeight.text = "Estatura: Falta información"
            }
        )
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