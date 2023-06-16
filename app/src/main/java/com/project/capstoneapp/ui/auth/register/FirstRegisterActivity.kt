package com.project.capstoneapp.ui.auth.register

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.project.capstoneapp.databinding.ActivityFirstRegisterBinding
import com.project.capstoneapp.ui.auth.login.LoginActivity

class FirstRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFirstRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()
    }

    private fun setListeners() {
        binding.apply {
            edName.addTextChangedListener(watcher)
            edEmail.addTextChangedListener(watcher)
            edPassword.addTextChangedListener(watcher)

            btnNext.setOnClickListener {
                val intentToSecondRegister =
                    Intent(this@FirstRegisterActivity, SecondRegisterActivity::class.java)
                binding.apply {
                    intentToSecondRegister.putExtra(
                        SecondRegisterActivity.NAME,
                        edName.text.toString()
                    )
                    intentToSecondRegister.putExtra(
                        SecondRegisterActivity.EMAIL,
                        edEmail.text.toString()
                    )
                    intentToSecondRegister.putExtra(
                        SecondRegisterActivity.PASSWORD,
                        edPassword.text.toString()
                    )
                }
                startActivity(intentToSecondRegister)
            }

            btnLogin.setOnClickListener {
                val iLogin = Intent(this@FirstRegisterActivity, LoginActivity::class.java)
                startActivity(iLogin)
            }
        }
    }


    private val watcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            setRegisterButtonEnable()
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

    private fun setRegisterButtonEnable() {
        binding.apply {
            btnNext.isEnabled =
                edName.text != null && edEmail.text != null && edPassword.text != null && edName.text.toString()
                    .isNotEmpty() && edEmail.text.toString()
                    .isNotEmpty() && edPassword.text.toString()
                    .isNotEmpty() && edName.error == null && edEmail.error == null && edPassword.error == null
        }
    }
}