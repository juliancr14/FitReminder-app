package com.cardoppc.fitreminder.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.cardoppc.fitreminder.R
import com.cardoppc.fitreminder.model.ProviderType
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)

        setup()
    }

    private fun setup() {

        title = "Autenticación"

        val btnRegistrar= findViewById<TextView>(R.id.tvRegistrarse)
        val btnIngresar: Button = findViewById(R.id.btnIngresar)
        val txtEmail = findViewById<EditText>(R.id.txtEmail)
        val txtPassword = findViewById<EditText>(R.id.txtPassword)

        btnRegistrar.setOnClickListener{
            if (txtEmail.text.isNotEmpty() && txtPassword.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(txtEmail.text.toString(),
                    txtPassword.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful) {
                            showHome(it.result?.user?.email?: "", ProviderType.BASIC)
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
                        showHome(it.result?.user?.email?: "", ProviderType.BASIC)
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

    private fun showHome(email: String, provider: ProviderType) {

        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.toString())
        }
        startActivity(homeIntent)
    }

}
