package com.cardoppc.fitreminder.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.cardoppc.fitreminder.R
import com.cardoppc.fitreminder.databinding.ActivityProgressBinding
import com.cardoppc.fitreminder.viewModel.UpdateViewModel
import com.cardoppc.fitreminder.viewModel.UpdateViewModelFactory
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.text.SimpleDateFormat
import java.util.*

class ProgressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProgressBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private val updateViewModel: UpdateViewModel by viewModels { UpdateViewModelFactory(this) }

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

        // Cargar la información del usuario y el historial de pesos
        loadUserProfile()
        loadProgressHistory(binding.chart)

        // Configuración del botón para registrar progreso
        binding.registerButton.setOnClickListener {
            val weightInput = binding.weightInput.text.toString()
            if (weightInput.isNotEmpty()) {
                updateViewModel.saveProgress(weightInput,
                    onSuccess = {
                        showToast("Nuevo progreso registrado: $weightInput")
                        loadUserProfile() // Recargar el perfil del usuario
                        loadProgressHistory(binding.chart) // Recargar el historial de pesos
                    },
                    onFailure = { showToast("Error al registrar progreso") }
                )
            } else {
                showToast("Por favor, ingresa un valor")
            }
        }
    }

    private fun loadUserProfile() {
        updateViewModel.fetchUserProfile(
            onSuccess = { userProfile ->
                val weight = userProfile["weight"]?.toString() ?: "Falta información"
                binding.userWeight.text = "Peso actual: $weight kg"
            },
            onFailure = {
                binding.userWeight.text = "Peso actual: Falta información"
            }
        )
    }

    private fun loadProgressHistory(chart: LineChart) {
        updateViewModel.fetchProgressHistory(
            onSuccess = { progressList ->
                val entries = mutableListOf<Entry>()
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                for ((index, progress) in progressList.withIndex()) {
                    val weight = progress["weight"].toString().toFloatOrNull() ?: 0f
                    val timestamp = progress["timestamp"] as? Date
                    val formattedDate = timestamp?.let { dateFormat.format(it) } ?: "Fecha desconocida"
                    entries.add(Entry(index.toFloat(), weight))
                }

                val dataSet = LineDataSet(entries, "Historial de Peso")
                dataSet.color = resources.getColor(android.R.color.holo_purple)
                dataSet.valueTextColor = android.R.color.white
                dataSet.setDrawCircles(true)
                dataSet.setDrawValues(true)

                val lineData = LineData(dataSet)
                chart.data = lineData
                chart.invalidate()
            },
            onFailure = {
                showToast("No se pudo cargar el historial de progreso")
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