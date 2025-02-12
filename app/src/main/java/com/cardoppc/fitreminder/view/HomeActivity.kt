package com.cardoppc.fitreminder.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.cardoppc.fitreminder.R
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bundle = intent.extras
        val email = bundle?.getString("email") ?: ""
        val provider = bundle?.getString("provider") ?: ""

        setupToolbar()
        setupNavigationDrawer()

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()
    }

    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        title = "Inicio"
    }

    private fun setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)

        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_update_info -> {
                    val intent = Intent(this, UpdateActivity::class.java)
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