package com.coherentsolutions.by.max.sir.androidtrainingtasks.home.user

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.coherentsolutions.by.max.sir.androidtrainingtasks.MyApplication.Companion.INFO_TAG
import com.coherentsolutions.by.max.sir.androidtrainingtasks.MyApplication.Companion.uiScope
import com.coherentsolutions.by.max.sir.androidtrainingtasks.database.UserDatabase.Companion.DATABASE_TAG
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities.State
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities.UserResponse
import com.coherentsolutions.by.max.sir.androidtrainingtasks.network.PetstoreService.API_KEY
import com.coherentsolutions.by.max.sir.androidtrainingtasks.network.PetstoreService.SERVER_TAG
import com.coherentsolutions.by.max.sir.androidtrainingtasks.network.RetrofitService
import com.coherentsolutions.by.max.sir.androidtrainingtasks.network.ServerStatusResponse
import com.coherentsolutions.by.max.sir.androidtrainingtasks.persistence.PetstorePersistence
import com.coherentsolutions.by.max.sir.androidtrainingtasks.persistence.UserPersistence
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class UserViewModel @Inject constructor(
    var userPersistence: UserPersistence,
    var petstorePersistence: PetstorePersistence,
    var retrofitService: RetrofitService
) : ViewModel() {

    val user by lazyOf(MutableLiveData<UserResponse>())

    private var getUserCallFlag: Boolean = false

    val loadingEvent by lazyOf(MutableLiveData(false))

    val nullUserEvent by lazyOf(MutableLiveData(false))

    private lateinit var userRequestCall: Call<UserResponse?>


    private fun startLoadingEvent() {
        loadingEvent.value = true
    }

    fun clearCryptoStorage() = petstorePersistence.clearCrypto()

    private fun endLoadingEvent() {
        loadingEvent.value = false
    }

    /**
     * TODO add "username:String" parametr to updateUserAfterSignIn(username:String) like this
     *  if you'll return to using intent
     */
    fun updateUserAfterSignIn() {
        uiScope.launch {
            val username = loadUser()?.username
            if (username == null) {
                nullUserEvent.value = true
                return@launch
            }

            if (getUserCallFlag) {
                userRequestCall.cancel()
            }
            getUserCallFlag = true
            startLoadingEvent()
            val service = retrofitService
            Timber.tag(INFO_TAG).i("username")
            get(username)
            userRequestCall = service.getUser(API_KEY, username)
            userRequestCall.enqueue(object : Callback<UserResponse?> {
                override fun onResponse(
                    call: Call<UserResponse?>,
                    response: Response<UserResponse?>
                ) {
                    Timber.tag(SERVER_TAG).d("@GET method RESPONSE Successful ${response.body()}")
                    if (response.body() != null) {
                        user.value = response.body()
                        uiScope.launch {
                            delay(400)
                        }
                        endLoadingEvent()
                        getUserCallFlag = false
                        return
                    } else {

                        /**
                         *  @note that this recursive call needed to get from swagger not null
                         *  @cause is that swagger api @GET method works time by time
                         *  @author of such bug is Swagger team
                         *  @link https://petstore.swagger.io
                         *  @also note the same problem with @DELETE method
                         *  @see deleteUser()
                         */
                        getUserCallFlag = false
                        updateUserAfterSignIn()
                    }
                }

                override fun onFailure(call: Call<UserResponse?>, t: Throwable) {
                    Timber.tag(SERVER_TAG)
                        .d("%s%s", "@GET method RESPONSE Failure" + "\n", t.message)
                    endLoadingEvent()
                    getUserCallFlag = false
                }

            })
        }
    }


    private fun loadUser(): UserResponse? {
        val db = petstorePersistence
        val userFromDb = db.loadUser()
        return userFromDb

    }

    fun deleteUser(callback: (State) -> Unit) {
        val username = user.value?.username
        if (username == null) {
            callback(State.FAILED)
            return
        }
        startLoadingEvent()
        val service = retrofitService
        Timber.tag(SERVER_TAG).i("DELETE USERNAME: $username")
        delete(username)
        val response = service.deleteUser(API_KEY, username)
        response.enqueue(object : Callback<ServerStatusResponse> {
            override fun onResponse(
                call: Call<ServerStatusResponse>,
                response: Response<ServerStatusResponse>
            ) {
                Timber.tag(SERVER_TAG).i("DELETE SUCCESSFULLY user ${user.value!!}")
                when (response.body()?.code) {
                    200 -> {
                        Timber.tag(SERVER_TAG).i("DELETE 200 OK")
                        callback(State.SUCCEED)
                        endLoadingEvent()
                    }
                    400, 404 -> {
                        Timber.tag(SERVER_TAG).i("DELETE 40x BAD")
                        callback(State.FAILED)
                        endLoadingEvent()
                    }
                    else -> {
                        Timber.tag(SERVER_TAG).i("${response.body()?.code}")
                        //throw IllegalArgumentException("No such documented code")
                        callback(State.FAILED)
                        endLoadingEvent()
                    }

                }
            }

            override fun onFailure(call: Call<ServerStatusResponse>, t: Throwable) {
                Timber.tag(SERVER_TAG).i("DELETE FAILED user ${t.message}")
                callback(State.FAILED)
                endLoadingEvent()
            }
        })


    }

    fun get(username: String) {
        uiScope.launch {
            val user = userPersistence.get(username) ?: return@launch
            Timber.tag(DATABASE_TAG).i("DATABASE GET value: $user")
            //TODO("Not yet needed")
        }
    }

    fun delete(username: String) {
        uiScope.launch {
            userPersistence.delete(username)
            Timber.tag(DATABASE_TAG).i("DATABASE DELETE value : $username")
        }
    }

    fun clearCurrentUser() = petstorePersistence.clearCurrentUser()


}