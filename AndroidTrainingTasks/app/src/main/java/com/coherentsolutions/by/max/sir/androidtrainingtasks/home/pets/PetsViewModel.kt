package com.coherentsolutions.by.max.sir.androidtrainingtasks.home.pets

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.coherentsolutions.by.max.sir.androidtrainingtasks.MyApplication.Companion.appScope
import com.coherentsolutions.by.max.sir.androidtrainingtasks.MyApplication.Companion.uiScope
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities.PetResponse
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities.PetStatus
import com.coherentsolutions.by.max.sir.androidtrainingtasks.network.PetstoreService.SERVER_TAG
import com.coherentsolutions.by.max.sir.androidtrainingtasks.network.RetrofitService
import com.coherentsolutions.by.max.sir.androidtrainingtasks.persistence.PetPersistence
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class PetsViewModel @Inject constructor(
    var retrofitService: RetrofitService,
    var petPersistence: PetPersistence,
    var persistence: PetPersistence
) : ViewModel() {
    val pets by lazyOf(MutableLiveData<List<PetResponse>>(ArrayList()))


    val eventLoading by lazy { MutableLiveData(false) }

    val navigateToPetDetailsHelper by lazyOf(MutableLiveData<PetResponse>())


    private lateinit var getJob: Job

    private var deferred
            : Deferred<List<PetResponse>>? = null

    private var isGetRequestInProgress: Boolean = false

    companion object {
        const val PETS_VIEW_MODEL_TAG = "petsVM"
    }

    private fun startLoadingEvent() {
        eventLoading.postValue(true)
    }

    private fun endLoadingEvent() {
        eventLoading.postValue(false)
    }

    fun getAllPets() {
        startLoadingEvent()
        appScope.launch {
            val petsFromDb = petPersistence.getAll()
            Timber.i("size of db is ${petsFromDb.size} elements")
            if (petsFromDb.isEmpty()) {
                Timber.i("Empty")
                getPets(PetStatus.ALL)
            } else {
                pets.postValue(pets.value?.plus(petsFromDb.map { it.toPetResponse() }
                    .filter { it !in requireNotNull(pets.value) }))
                val petList =
                    retrofitService.getPetsByStatus(PetStatus.ALL.value).await()
                petList.forEach {
                    if (!petsFromDb.contains(it.toPet())) {
                        addPet(it)
                        pets.postValue(pets.value?.plus(it))
                    } else {
                        Timber.i("There was one such element")
                    }
                }
                endLoadingEvent()
            }

        }
    }

    fun getPets(status: PetStatus = PetStatus.ALL) {
        getJob = uiScope.launch {
            val getPetsDeferred = retrofitService
            try {
                Timber.tag(SERVER_TAG).i("GET PETS CALL")
                if (deferred != null && deferred?.isActive == true) {
                    isGetRequestInProgress = false
                    deferred?.cancel()
                }
                isGetRequestInProgress = true
                deferred = getPetsDeferred.getPetsByStatus(status.value)
                val petList = deferred?.await()
                pets.value = petList
                endLoadingEvent()
                isGetRequestInProgress = false
                petList?.forEach {
                    addPet(it)
                }

            } catch (t: Throwable) {
                pets.value = ArrayList()
                endLoadingEvent()
                isGetRequestInProgress = false
                Timber.tag(PETS_VIEW_MODEL_TAG).i("THROW THE EXCEPTION WHILE GET ${t.message}")
            }
        }
    }

    internal fun addPet(petResponse: PetResponse) {
        uiScope.launch {
            val pet = petResponse.toPet()
            persistence.add(pet)
        }
    }

    @Suppress("unused", "unused_variable")
    internal fun getPet(id: Long) {
        uiScope.launch {
            val pet = persistence.get(id)
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    internal fun clearAll() {
        uiScope.launch {
            persistence.clearAll()
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.tag(PETS_VIEW_MODEL_TAG).i("OnCleared called()")
        /**
         * @note that code for testing purposes only you mustn't do like that on real projects
         * TODO remove this line after release application and onCleared() too
         */
        //clearAll()
    }

    fun displayPetDetails(pet: PetResponse) {
        navigateToPetDetailsHelper.value = pet
    }

    fun displayPetDetailsComplete() {
        navigateToPetDetailsHelper.value = null
    }
}