package com.cardoppc.fitreminder.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.cardoppc.fitreminder.R
import com.cardoppc.fitreminder.model.providerType
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)

        setUp()
    }

    private fun setUp() {

        title = "Autenticación"

        val btnRegistrar: Button = findViewById(R.id.btnRegistrar)
        val btnIngresar: Button = findViewById(R.id.btnIngresar)
        var txtEmail = findViewById<TextView>(R.id.txtEmail)
        var txtPassword = findViewById<TextView>(R.id.txtPassword)

        btnRegistrar.setOnClickListener{
            if (txtEmail.text.isNotEmpty() && txtPassword.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(txtEmail.text.toString(),
                    txtPassword.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful) {
                            showHome(it.result?.user?.email?: "", providerType.BASIC)
                        } else {
                            showAlert()
                        }
                }
            }
        }

        btnIngresar.setOnClickListener{
            if (txtEmail.text.isNotEmpty() && txtPassword.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(txtEmail.text.toString(),
                    txtPassword.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email?: "", providerType.BASIC)
                    } else {
                        showAlert()
                    }
                }
            }
        }

    }

    private fun showAlert() {

        val builder = AlertDialog.Builder(this)

        builder.setTitle("Error")
        builder.setMessage("Hay un error con la autenticación el usuario")
        builder.setPositiveButton("Aceptar", null)

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: providerType) {

        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider)
        }
        startActivity(homeIntent)
    }

}
