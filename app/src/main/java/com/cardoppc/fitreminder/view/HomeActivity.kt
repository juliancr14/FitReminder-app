package com.cardoppc.fitreminder.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.cardoppc.fitreminder.R
import com.cardoppc.fitreminder.databinding.ActivityHomeBinding
import com.cardoppc.fitreminder.viewModel.HomeViewModel
import com.cardoppc.fitreminder.viewModel.HomeViewModelFactory
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private val homeViewModel: HomeViewModel by viewModels { HomeViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.barra) // Ícono del menú hamburguesa

        // Configurar DrawerLayout y Toggle para el menú hamburguesa
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Configurar navegación del NavigationView
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_update_info -> {
                    startActivity(Intent(this, UpdateActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_progress_history -> {
                    startActivity(Intent(this, ProgressActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_logout -> {
                    getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).edit().clear().apply()
                    startActivity(Intent(this, AuthActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }.also {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }

        // Cargar los datos del usuario desde Firestore
        loadUserProfile()
    }

    private fun loadUserProfile() {
        homeViewModel.fetchUserProfile(
            onSuccess = { userProfile ->
                val weight = userProfile["weight"]?.toString()?.toDoubleOrNull()
                val height = userProfile["height"]?.toString()?.toDoubleOrNull()
                val fatPercentage = userProfile["fatPercentage"]?.toString()?.toDoubleOrNull()

                // Calcular IMC
                if (weight != null && height != null) {
                    val heightInMeters = height / 100
                    val imc = weight / (heightInMeters * heightInMeters)
                    binding.imcValue.text = "IMC: %.2f".format(imc)
                    binding.imcStatus.text = "Estado: ${getImcStatus(imc)}"
                } else {
                    binding.imcValue.text = "IMC: Falta información"
                    binding.imcStatus.text = "Estado: Falta información"
                }

                // Actualizar gráficos de barras
                setupBarCharts(weight, height, fatPercentage)
            },
            onFailure = {
                binding.imcValue.text = "IMC: Falta información"
                binding.imcStatus.text = "Estado: Falta información"
                setupBarCharts(null, null, null)
            }
        )
    }

    private fun getImcStatus(imc: Double): String {
        return when {
            imc < 18.5 -> "Bajo peso"
            imc in 18.5..24.9 -> "Normal"
            imc in 25.0..29.9 -> "Sobrepeso"
            else -> "Obesidad"
        }
    }

    private fun setupBarCharts(weight: Double?, height: Double?, fatPercentage: Double?) {
        // Configurar gráfico de peso
        val weightEntries = listOf(BarEntry(0f, weight?.toFloat() ?: 0f))
        val weightDataSet = BarDataSet(weightEntries, "Peso").apply {
            color = Color.BLUE
            valueTextColor = Color.WHITE
        }
        binding.weightBarChart.apply {
            data = BarData(weightDataSet)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("Peso"))
            axisLeft.axisMaximum = 150f
            axisRight.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = false
            invalidate()
        }

        // Configurar gráfico de altura
        val heightEntries = listOf(BarEntry(0f, height?.toFloat() ?: 0f))
        val heightDataSet = BarDataSet(heightEntries, "Altura").apply {
            color = Color.GREEN
            valueTextColor = Color.WHITE
        }
        binding.heightBarChart.apply {
            data = BarData(heightDataSet)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("Altura"))
            axisLeft.axisMaximum = 250f
            axisRight.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = false
            invalidate()
        }

        // Configurar gráfico de grasa corporal
        val fatEntries = listOf(BarEntry(0f, fatPercentage?.toFloat() ?: 0f))
        val fatDataSet = BarDataSet(fatEntries, "Grasa Corporal").apply {
            color = Color.RED
            valueTextColor = Color.WHITE
        }
        binding.fatBarChart.apply {
            data = BarData(fatDataSet)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("Grasa"))
            axisLeft.axisMaximum = 100f
            axisRight.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = false
            invalidate()
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}