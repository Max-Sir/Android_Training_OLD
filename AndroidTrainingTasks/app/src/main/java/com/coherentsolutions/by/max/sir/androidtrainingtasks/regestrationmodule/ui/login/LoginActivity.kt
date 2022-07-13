package com.coherentsolutions.by.max.sir.androidtrainingtasks.regestrationmodule.ui.login

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.Observer
import com.coherentsolutions.by.max.sir.androidtrainingtasks.MyApplication.Companion.INFO_TAG
import com.coherentsolutions.by.max.sir.androidtrainingtasks.R
import com.coherentsolutions.by.max.sir.androidtrainingtasks.ble.BLEActivity
import com.coherentsolutions.by.max.sir.androidtrainingtasks.databinding.ActivityLoginBinding
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.HomeActivity
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities.UserResponse
import com.coherentsolutions.by.max.sir.androidtrainingtasks.persistence.PetstorePersistence
import com.coherentsolutions.by.max.sir.androidtrainingtasks.security.BiometricPromptUtils
import com.coherentsolutions.by.max.sir.androidtrainingtasks.security.BiometricPromptUtils.CIPHERTEXT_WRAPPER
import com.coherentsolutions.by.max.sir.androidtrainingtasks.security.BiometricPromptUtils.SHARED_PREFS_FILENAME
import com.coherentsolutions.by.max.sir.androidtrainingtasks.security.CiphertextWrapper
import com.coherentsolutions.by.max.sir.androidtrainingtasks.security.CryptographyManagerImpl
import com.coherentsolutions.by.max.sir.androidtrainingtasks.service.persistence
import com.coherentsolutions.by.max.sir.androidtrainingtasks.webcontent.TermsAndConditionsActivity
import com.google.firebase.iid.FirebaseInstanceId
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import timber.log.Timber
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val cryptographyManager by lazyOf(CryptographyManagerImpl())
    private var ciphertextWrapper: CiphertextWrapper? = null

    @Inject
    lateinit var loginViewModel: LoginViewModel

    private lateinit var binding: com.coherentsolutions.by.max.sir.androidtrainingtasks.databinding.ActivityLoginBinding

    override fun onNewIntent(intent: Intent?) {

        ciphertextWrapper = getWrapper()
        Timber.i("cipher")
        super.onNewIntent(intent)
    }

    private fun getWrapper(): CiphertextWrapper? =
        cryptographyManager.getCiphertextWrapperFromSharedPrefs(
            applicationContext,
            SHARED_PREFS_FILENAME,
            Context.MODE_PRIVATE,
            CIPHERTEXT_WRAPPER
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        ciphertextWrapper = getWrapper()
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            Timber.tag(INFO_TAG).i("token: ${it.token}")
        }
        Timber.tag(INFO_TAG).i("LOGIN ACTIVITY ON CREATE()")
        super.onCreate(savedInstanceState)

        /**if THERE IS ANY USER DATA ARE IN PREF*/
        Timber.i("oiiogjewogijewogijewgjewjgoiewjogijew")
        Timber.i("kjn ${persistence<PetstorePersistence>().getCrypto().toString()}")

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showBiometricPromptForDecryption()

        Timber.tag(INFO_TAG).i("LOGIN ACTIVITY INFLATED")
        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading
        val email = binding.emailEditLogin
        val phone = binding.phoneEditLogin
        val firstname = binding.firstnameEditLogin
        val lastname = binding.lastnameEditLogin
        val viewSet = setOf(username, password, email, phone, firstname, lastname)

        showSoftKeyboard(username)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            when {
                loginState.usernameError != null -> {
                    username.error = getString(loginState.usernameError)
                }
                loginState.firstnameError != null -> {
                    firstname.error = getString(loginState.firstnameError)
                }
                loginState.lastnameError != null -> {
                    lastname.error = getString(loginState.lastnameError)
                }
                loginState.passwordError != null -> {
                    password.error = getString(loginState.passwordError)
                }
                loginState.phoneError != null -> {
                    phone.error = getString(loginState.phoneError)
                }
                loginState.emailError != null -> {
                    email.error = getString(loginState.emailError)
                }
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            setResult(RESULT_OK)

            //Complete and destroy login activity once successful

            Timber.tag(INFO_TAG).i("LOGIN ACTIVITY - USER SAVE() TO SHARED PREF BY PERSISTENCE")

            val user = UserResponse(
                email = "${binding.emailEditLogin.text}",
                phone = "${binding.phoneEditLogin.text}",
                userStatus = 0,
                id = Random.nextInt(Int.MAX_VALUE),
                lastName = "${binding.lastnameEditLogin.text}",
                firstName = "${binding.firstnameEditLogin.text}",
                username = "${binding.username.text}",
                password = "${binding.password.text}"
            )
            if (isOnline(this))
                loginViewModel.postUser(user) { isSuccess ->
                    if (isSuccess) {
                        if (loginResult.success != null) {
                            updateUiWithUser(loginResult.success)
                        }
                        val intent =
                            Intent(this, TermsAndConditionsActivity::class.java)
                        Timber.tag(INFO_TAG).i("LOGIN ACTIVITY - INTENT SUCCESSFULLY INSTATED")

                        persistence<PetstorePersistence>().saveUser(user)
                        /**
                         * TODO if will pass username by intent instead of SharedPreferences:
                         *          "intent.putExtra(USERNAME, user.username)"
                         */
                        startActivity(intent)

                        Timber.tag(
                            INFO_TAG
                        ).i(
                            "LOGIN ACTIVITY - STARTED ACTIVITY ${HomeActivity::class.simpleName}"
                        )
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            getString(R.string.please_check_and_correct_your_data),
                            Toast.LENGTH_SHORT
                        ).show()

                        loginViewModel.loginDataChanged(
                            username.text.toString(),
                            password.text.toString(),
                            email.text.toString(),
                            phone.text.toString(),
                            firstname.text.toString(),
                            lastname.text.toString()
                        )
                    }
                }
            else {
                return@Observer
            }
            Timber.tag(INFO_TAG)
                .i("LOGIN ACTIVITY - USER SAVED TO SHARED PREF BY PERSISTENCE SUCCESSFULLY")

        })

        loginViewModel.loadingEvent.observe(this, { loadingActive ->
            if (loadingActive) {
                loading.show()
            } else {
                loading.hide()
            }
        })

        login.isSelected
        viewSet.forEach {
            it.afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString(),
                    email.text.toString(),
                    phone.text.toString(),
                    firstname.text.toString(),
                    lastname.text.toString()
                )
            }
        }

        password.apply {

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString(),
                            email.text.toString(),
                            phone.text.toString(),
                            firstname.text.toString(),
                            lastname.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                if (binding.useBiometrics.isChecked) {
                    showBiometricPromptForEncryption()
                }
                loginViewModel.login(
                    username.text.toString(),
                    password.text.toString(),
                    email.text.toString(),
                    phone.text.toString(),
                    firstname.text.toString(),
                    lastname.text.toString()
                )
            }
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        if (isOnline(this)) {
            val welcome = getString(R.string.welcome)
            Toast.makeText(
                applicationContext,
                "$welcome ${binding.username.text ?: model.displayName}",
                Toast.LENGTH_LONG
            ).show()
        } else {
            showLoginFailed(R.string.check_internet_and_try_again)
        }
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.login_activity_options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.bleSearch) {
            startActivity(Intent(this@LoginActivity, BLEActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.exit_text))
            .setMessage(getString(R.string.are_u_sure_u_wanna_exit_question_text))
            .setPositiveButton(getText(R.string.yes)) { dialog, _ ->
                finish()
                dialog.cancel()
            }
            .setNegativeButton(getText(R.string.no)) { dialog, _ ->
                dialog.cancel()
            }.create().show()
    }

    @Suppress("deprecation")
    private fun showBiometricPromptForEncryption() {
        val canAuthenticate = BiometricManager.from(applicationContext).canAuthenticate()
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            val secretKeyName = "biometric_sample_encryption_key"
            val cipher = cryptographyManager.getInitializedCipherForEncryption(secretKeyName)
            val biometricPrompt =
                BiometricPromptUtils.createBiometricPrompt(this, ::encryptAndStoreUsername)
            val promptInfo = BiometricPromptUtils.createPromptInfo(this)
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }

    @Suppress("deprecation")
    private fun showBiometricPromptForDecryption() {
        val canAuthenticate = BiometricManager.from(applicationContext).canAuthenticate()
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            ciphertextWrapper?.let { textWrapper ->
                val secretKeyName = "biometric_sample_encryption_key"
                Timber.i("hellom ${textWrapper.ciphertext.toString()}")
                val cipher = cryptographyManager.getInitializedCipherForDecryption(
                    secretKeyName, textWrapper.initializationVector
                )
                val biometricPrompt =
                    BiometricPromptUtils.createBiometricPrompt(
                        this,
                        ::decryptServerTokenFromStorage
                    )
                val promptInfo = BiometricPromptUtils.createPromptInfo(this)
                biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
            }
        }
    }


    private fun encryptAndStoreUsername(authResult: BiometricPrompt.AuthenticationResult) {
        loginViewModel.startLoadingEvent()

        authResult.cryptoObject?.cipher?.apply {
            val text = binding.username.text.toString()
            Timber.plant(Timber.DebugTree())
            Timber.d("text before: $text")
            val encryptedServerTokenWrapper =
                cryptographyManager.encryptData(text, this)
            Timber.d("ciphertext is now is: ${encryptedServerTokenWrapper.ciphertext}")
            cryptographyManager.persistCiphertextWrapperToSharedPrefs(
                encryptedServerTokenWrapper,
                applicationContext,
                SHARED_PREFS_FILENAME,
                Context.MODE_PRIVATE,
                CIPHERTEXT_WRAPPER
            )
        }
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
        loginViewModel.endLoadingEvent()
    }

    private fun decryptServerTokenFromStorage(authResult: BiometricPrompt.AuthenticationResult) {
        ciphertextWrapper?.let { textWrapper ->
            authResult.cryptoObject?.cipher?.let {
                val plaintextUsername =
                    cryptographyManager.decryptData(textWrapper.ciphertext, it)
                Timber.i("plaintext: $plaintextUsername")
                persistence<PetstorePersistence>().saveUser(
                    UserResponse.createEmptyUserToHold(
                        plaintextUsername
                    )
                )
                startActivity(Intent(this, HomeActivity::class.java))
            }
        }
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    Timber.tag("Internet").i("NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    Timber.tag("Internet").i("NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    Timber.tag("Internet").i("NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
