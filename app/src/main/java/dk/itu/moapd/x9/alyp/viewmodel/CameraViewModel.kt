package dk.itu.moapd.x9.alyp.viewmodel

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {
    /**
     * The current selected camera.
     */
    private var _selector = MutableLiveData<CameraSelector>()

    /**
     * A `LiveData` which publicly exposes any update in the camera selector.
     */
    val selector: LiveData<CameraSelector>
        get() = _selector

    /**
     * The last captured image Uri
     */
    private var _imageUri = MutableLiveData<Uri?>()
    private val _localImageUri = MutableLiveData<Uri?>()

    /**
     * publicly exposes any update in the last captured image Uri
     */
    val imageUri: LiveData<Uri?> get() = _imageUri

    val localImageUri: LiveData<Uri?> get() = _localImageUri

    /**
     * This method will be executed when the user interacts with the camera selector component. It
     * sets the selector into the LiveData instance
     *
     * @param selector A set of requirements and priorities used to select a camera
     */
    fun onCameraSelectorChanged(selector: CameraSelector) {
        this._selector.value = selector
    }

    /**
     * Update the last captured image Uri
     *
     * @param uri The new image Uri
     */
    fun onImageUriChanged(uri: Uri?) {
        _imageUri.value = uri
    }

    fun onLocalImageUriChanged(uri: Uri?) {
        _localImageUri.value = uri
    }
}