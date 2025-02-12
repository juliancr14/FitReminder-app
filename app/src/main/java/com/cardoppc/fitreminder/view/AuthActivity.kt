package com.cardoppc.fitreminder.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import com.cardoppc.fitreminder.R
import com.cardoppc.fitreminder.viewModel.AuthViewModel
import com.cardoppc.fitreminder.viewModel.AuthViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.messaging.FirebaseMessaging

class AuthActivity : AppCompatActivity() {
    private lateinit var authViewModel: AuthViewModel
    private val GOOGLE_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)

        // Usar la ViewModel Factory para crear el ViewModel
        val factory = AuthViewModelFactory(this)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        notification()
        setup()
        session()
    }

    override fun onStart() {
        super.onStart()
        val authLayout = findViewById<View>(R.id.authLayout)
        authLayout.visibility = View.INVISIBLE
    }

    private fun sendLoginNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "water_reminder",
                "Recordatorios de Agua",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, "water_reminder")
            .setSmallIcon(R.drawable.ic_water) // Icono de la notificación
            .setContentTitle("Bienvenido")
            .setContentText("Tienes que tomar agua")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(1, notificationBuilder.build())
    }

    private fun session() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)
        val authLayout = findViewById<View>(R.id.authLayout)

        if (email != null && provider != null) {
            authLayout.visibility = View.INVISIBLE
            showHome(email)
        }
    }

    private fun notification() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("FCM", "Error al obtener el token", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM", "Este es el token del dispositivo: $token")
        }
    }

    private fun setup() {
        title = "Autenticación"

        val btnRegistrar = findViewById<TextView>(R.id.tvRegistrarse)
        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        val btnGoogle = findViewById<Button>(R.id.btnGoogle)
        val txtEmail = findViewById<EditText>(R.id.txtEmail)
        val txtPassword = findViewById<EditText>(R.id.txtPassword)

        btnRegistrar.setOnClickListener {
            if (txtEmail.text.isNotEmpty() && txtPassword.text.isNotEmpty()) {
                authViewModel.createUser(
                    txtEmail.text.toString(),
                    txtPassword.text.toString(),
                    onSuccess = { showHome(txtEmail.text.toString()) },
                    onFailure = { showAlert() }
                )
            }
        }

        btnIngresar.setOnClickListener {
            if (txtEmail.text.isNotEmpty() && txtPassword.text.isNotEmpty()) {
                authViewModel.signInUser(
                    txtEmail.text.toString(),
                    txtPassword.text.toString(),
                    onSuccess = {
                        showHome(txtEmail.text.toString())
                        sendLoginNotification()
                    },
                    onFailure = { showAlert() }
                )
            }
        }

        btnGoogle.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Hay un error con la autenticación del usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String) {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", "BASIC") // O "GOOGLE" si es autenticación con Google
        prefs.apply()

        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(homeIntent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    authViewModel.signInWithGoogle(
                        account,
                        onSuccess = {
                            val email = account.email ?: ""
                            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                            prefs.putString("email", email)
                            prefs.putString("provider", "GOOGLE")
                            prefs.apply()
                            showHome(email)
                        },
                        onFailure = { showAlert() }
                    )
                }
            } catch (e: ApiException) {
                showAlert()
            }
        }
    }
}