package com.coherentsolutions.by.max.sir.androidtrainingtasks.home.pet

import android.graphics.Bitmap
import android.util.Base64
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.coherentsolutions.by.max.sir.androidtrainingtasks.MyApplication.Companion.uiScope
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities.PetResponse
import com.coherentsolutions.by.max.sir.androidtrainingtasks.network.PetstoreService.API_KEY
import com.coherentsolutions.by.max.sir.androidtrainingtasks.network.RetrofitService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import javax.inject.Inject


class PetViewModel @Inject constructor(
    var retrofitService: RetrofitService
) : ViewModel() {


    fun passArg(petResponse: PetResponse) {
        pet.postValue(petResponse)
    }

    val pet by lazyOf(MutableLiveData<PetResponse>())


    fun upload(bmp: Bitmap) {
        uiScope.launch {
            val stream = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray: ByteArray = stream.toByteArray()
            bmp.recycle()

            val requestBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("additionalMetadata", "image")
                .addFormDataPart("file", Base64.encodeToString(byteArray, Base64.NO_WRAP))
                .build()
            if (pet.value != null) {
                val deferred =
                    retrofitService.uploadImage(API_KEY, pet.value!!.id, requestBody)

                try {
                    deferred.await()
                } catch (t: Throwable) {

                }
            }
        }
    }


}