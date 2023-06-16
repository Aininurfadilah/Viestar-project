package com.example.viestar

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.example.viestar.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
//    private lateinit var user: UserModel
//    private lateinit var pref: SessionManager

//    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            context.startActivity(intent)
        }
    }

//    private lateinit var storyDao: StoryAppDao
//    private val loginViewModel: LoginViewModel by viewModels {
//        ViewModelFactory(application, this)
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        pref = SessionManager(this)

        auth = FirebaseAuth.getInstance()

//        getSetting()
        setupView()
        playAnimation()
        setupAction()
    }

//    private fun getSetting() {
//        val pref = SettingPreferences.getInstance(dataStore)
//        val mainViewModel =
//            ViewModelProvider(this, SettingViewModelFactory(pref))[SettingViewModel::class.java]
//        mainViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
//            if (isDarkModeActive) {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//            } else {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//            }
//        }
//    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {

            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                email.isEmpty() -> {
                    binding.emailEditText.error = "Masukkan email"
                }
                !email.isEmailValid() -> {
                    binding.emailEditText.error = getString(R.string.empty_email)
                }
                password.isEmpty() -> {
                    binding.passwordEditText.error = "Masukkan password"
                }
                password.length < 8 -> {
                    binding.passwordEditText.error = getString(R.string.password_must_more8)
                }
                else -> {
                    binding.loadingLogin.visibility = View.VISIBLE
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this@LoginActivity,
                            OnCompleteListener<AuthResult?> { task -> // ketika gagal locin maka akan do something
                                if (!task.isSuccessful) {
                                    binding.loadingLogin.visibility = View.INVISIBLE
                                    Toast.makeText(this, "Gagal Login : ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                } else {
                                    binding.loadingLogin.visibility = View.INVISIBLE
                                    val bundle = Bundle()
                                    bundle.putString("email", email)
                                    bundle.putString("pass", password)
                                    startActivity(
                                        Intent(this@LoginActivity, MainActivity::class.java)
                                            .putExtra("emailpass", bundle)
                                    )
                                    finish()
                                }
                            })

//                    loginViewModel.login(email, password).observe(this) { result ->
//                        if (result != null) {
//                            when(result) {
//                                is Result.Loading -> {
//                                    showLoading(true)
//                                }
//                                is Result.Success -> {
//                                    loginProcess(result.data, email)
//                                    showLoading(false)
//                                }
//                                is Result.Error -> {
//                                    showLoading(false)
//                                    Toast.makeText(this, "Email atau Password Salah", Toast.LENGTH_LONG).show()
//                                }
//                            }
//                        }
//                    }

                }
            }

        }

        binding.tvActionRegister.setOnClickListener {
            RegisterActivity.start(this)
        }
    }


//    private fun loginProcess(data: LoginResponse, email: String) {
//        if (data.error) {
//            Toast.makeText(this, data.message, Toast.LENGTH_LONG).show()
//        } else {
//            pref.apply {
//                saveToken(KEY_TOKEN, data.loginResult.token)
//                saveToken(KEY_USERNAME, data.loginResult.name)
//                saveToken(KEY_EMAIL, email)
//            }
//            Toast.makeText(this, data.message, Toast.LENGTH_LONG).show()
//            val i = Intent(this@LoginActivity, MainActivity::class.java)
//            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//            startActivity(i)
//            finish()
//        }
//    }

//    private fun showLoading(state: Boolean) {
//        binding.apply {
//            loadingLogin.isVisible = state
//            emailEditText.isInvisible = state
//            passwordEditText.isInvisible = state
//            loginButton.isInvisible = state
//        }
//    }

    private fun playAnimation() {
//        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
//            duration = 6000
//            repeatCount = ObjectAnimator.INFINITE
//            repeatMode = ObjectAnimator.REVERSE
//        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
//        val message = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(500)
        val emailTextView = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.emailEditText, View.ALPHA, 1f).setDuration(500)
        val passwordTextView = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.passwordEditText, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, emailTextView, emailEditTextLayout, passwordTextView, passwordEditTextLayout, login)
            startDelay = 500
        }.start()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}