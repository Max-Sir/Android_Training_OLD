package com.coherentsolutions.by.max.sir.androidtrainingtasks.regestrationmodule.ui.login


import android.util.Patterns
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.coherentsolutions.by.max.sir.androidtrainingtasks.MyApplication.Companion.INFO_TAG
import com.coherentsolutions.by.max.sir.androidtrainingtasks.MyApplication.Companion.uiScope
import com.coherentsolutions.by.max.sir.androidtrainingtasks.R
import com.coherentsolutions.by.max.sir.androidtrainingtasks.data.LoginRepository
import com.coherentsolutions.by.max.sir.androidtrainingtasks.data.Result
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities.User
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities.UserResponse
import com.coherentsolutions.by.max.sir.androidtrainingtasks.network.PetstoreService.API_KEY
import com.coherentsolutions.by.max.sir.androidtrainingtasks.network.PetstoreService.SERVER_TAG
import com.coherentsolutions.by.max.sir.androidtrainingtasks.network.RetrofitService
import com.coherentsolutions.by.max.sir.androidtrainingtasks.network.ServerStatusResponse
import com.coherentsolutions.by.max.sir.androidtrainingtasks.persistence.PetstorePersistence
import com.coherentsolutions.by.max.sir.androidtrainingtasks.persistence.UserPersistence
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    var retrofitService: RetrofitService,
    var userPersistence: UserPersistence,
    var petstorePersistence: PetstorePersistence
) :
    ViewModel() {

    val loginFormState by lazyOf(MutableLiveData<LoginFormState>())

    val loadingEvent by lazyOf(MutableLiveData(false))

    val loginResult by lazyOf(MutableLiveData<LoginResult>())

    companion object {
        const val LOGIN_VIEW_MODEL_TAG = "login-tag"
    }


    fun login(
        username: String,
        password: String,
        email: String,
        phone: String,
        firstname: String,
        lastname: String
    ) {
        // can be launched in a separate asynchronous job
        val result = loginRepository.login(username, password, email, phone, firstname, lastname)

        Timber.tag(INFO_TAG).i("LOGIN VIEW MODEL FUN")
        if (result is Result.Success) {
            Timber.tag(INFO_TAG).i("LOGIN VIEW MODEL FUN FINISHED SUCCESSFULLY")
            loginResult.value =
                LoginResult(success = LoggedInUserView(displayName = result.data.displayName))

        } else {
            Timber.tag(INFO_TAG).i("LOGIN VIEW MODEL FUN FINISHED WITH FAILED CASE")
            loginResult.value = LoginResult(error = R.string.login_failed)
        }
        Timber.tag(INFO_TAG).i("LOGIN VIEW MODEL FUN FINISHED")
    }

    fun loginDataChanged(
        username: String,
        password: String,
        email: String,
        phone: String,
        firstname: String,
        lastname: String
    ) {
        when {
            !isUserNameValid(username) -> {
                this.loginFormState.value = LoginFormState(
                    usernameError = R.string.invalid_username
                )
            }
            !isPasswordValid(password) -> {
                this.loginFormState.value = LoginFormState(
                    passwordError = R.string.invalid_password
                )
            }
            !isEmailValid(email) -> {
                this.loginFormState.value = LoginFormState(
                    emailError = R.string.invalid_email
                )
            }
            !isLastNameValid(lastname) -> {
                this.loginFormState.value = LoginFormState(
                    lastnameError = R.string.invalid_lastname
                )
            }
            !isFirstNameValid(firstname) -> {
                this.loginFormState.value = LoginFormState(
                    firstnameError = R.string.invalid_firstname
                )
            }

            !isPhoneValid(phone) -> {
                this.loginFormState.value =
                    LoginFormState(phoneError = R.string.invalid_phone_number)
            }

            else -> {
                this.loginFormState.value = LoginFormState(
                    isDataValid = true
                )
            }
        }
        Timber.tag(INFO_TAG).i("LOGIN DATA CHANGED VIEW MODEL FUN FINISHED")
    }

    private fun validateBeforePosting(userResponse: UserResponse): Boolean =
        setOf(
            userResponse.email,
            userResponse.firstName,
            userResponse.id,
            userResponse.lastName,
            userResponse.password,
            userResponse.phone,
            userResponse.userStatus,
            userResponse.username
        ).any { element -> element == "" }

    /**
     * @POST usage
     */
    fun postUser(userResponse: UserResponse, resultCallback: (Boolean) -> Unit) {
        if (validateBeforePosting(userResponse)) {
            resultCallback(false)
            return
        }
        startLoadingEvent()
        addUser(
            User(
                userResponse.email,
                userResponse.firstName,
                userResponse.id,
                userResponse.lastName,
                userResponse.password,
                userResponse.phone,
                userResponse.userStatus,
                userResponse.username
            )
        )
        val createUserRequest = retrofitService.createUser(API_KEY, userResponse)

        createUserRequest.enqueue(object : Callback<ServerStatusResponse> {
            override fun onResponse(
                call: Call<ServerStatusResponse>,
                response: Response<ServerStatusResponse>
            ) {
                endLoadingEvent()
                resultCallback(true)
                Timber.tag(SERVER_TAG).d("POSTED $userResponse")
                Timber.tag(SERVER_TAG).i("GOOD REQUEST ${response.body().toString()}")
            }

            override fun onFailure(call: Call<ServerStatusResponse>, t: Throwable) {
                endLoadingEvent()
                resultCallback(false)
                Timber.tag(SERVER_TAG).d("BAD REQUEST ${t.message}")
            }
        })

    }

    @Suppress("unused")
    fun saveUserToPreferences(userResponse: UserResponse) {
        petstorePersistence.saveUser(userResponse)
    }

    fun startLoadingEvent() {
        loadingEvent.value = true
    }

    fun endLoadingEvent() {
        loadingEvent.value = false
    }

    private fun addUser(user: User) {
        uiScope.launch {
            userPersistence.add(user)
            Timber.tag(LOGIN_VIEW_MODEL_TAG).i("DATABASE ADD USER: $user")
        }
    }


    private fun isLastNameValid(lastname: String): Boolean {
        return lastname != ""
    }

    private fun isFirstNameValid(firstname: String): Boolean {
        return firstname != ""
    }

    private fun isPhoneValid(phone: String): Boolean {
        Timber.i("PHONE VALIDATION CALL")
        return Patterns.PHONE.matcher(phone).matches()
    }

    private fun isEmailValid(email: String): Boolean {
        Timber.i("EMAIL VALIDATION CALL")
        return Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isNotEmpty()
    }


    // A placeholder username validation check
    fun isUserNameValid(username: String): Boolean {
        Timber.i("LOGIN FORM `USERNAME` VALIDATION")
        return username.isNotBlank() && username.toList()
            .any {
                it in 'A'..'Z'
                        || it in 'a'..'z'
            }
                && username.toList()
            .all {
                it in 'A'..'Z'
                        || it in 'a'..'z'
                        || it in '0'..'9'
                        || it in "$.@   #%_-"
            }
    }

    // A placeholder password validation check
    fun isPasswordValid(password: String): Boolean {
        Timber.i("LOGIN SCREEN `PASSWORD VALIDATION`")
        /**TODO delete ||true in final version it's just for testing purposes only*/
        @Suppress("SimplifyBooleanWithConstants")
        return password.length >= 8
                && password.all { it in '!'..'~' }
                && password.any { it !in 'A'..'Z' && it !in 'a'..'z' && it !in '0'..'9' }
                && password.any { it in 'A'..'Z' }
                && password.any { it in '0'..'9' }
                && password.any { it in 'a'..'z' } || true

    }
}
