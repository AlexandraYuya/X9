package dk.itu.moapd.x9.alyp.viewmodel

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * CameraViewModel holds the UI state for CameraFragment and shares captured image data with ReportFormFragment.
 *
 * Persists the selected camera (front/back) across screen rotations and exposes two image URIs "Uniform Resource Identifier":
 * - localImageUri. The local MediaStore URI used to display the image thumbnail in the report form.
 * - imageUri. The Firebase Storage download URL used when submitting the report.
 *
 * Inspired by Fabricio Narcizo's code examples from "10-4_CameraX-MDC": https://github.com/fabricionarcizo/moapd2026/tree/main/lecture10/10-4_CameraX-MDC
 */
class CameraViewModel : ViewModel() {

    /**
     * The current selected camera, persisted across screen rotations.
     */
    private var _selector = MutableLiveData<CameraSelector>()
    /**
     * The Firebase Storage download URL of the last captured image.
     * Available only after Firebase upload completes.
     * HTTPS URL pointing to the photo stored in Firebase Storage.
     */
    private var _imageUri = MutableLiveData<Uri?>()
    /**
     * The local MediaStore URI of the last captured image.
     * Used to display the image thumbnail in ReportFormFragment without waiting for the upload.
     * Immediately available after the photo is taken. URI pointing to the saved location of the photo on the device.
     * URI from MediaStore can be loaded directly into the ImageView container, without the need of an image loading library.
     */
    private val _localImageUri = MutableLiveData<Uri?>()

    val selector: LiveData<CameraSelector>
        get() = _selector
    val imageUri: LiveData<Uri?> get() = _imageUri
    val localImageUri: LiveData<Uri?> get() = _localImageUri

    /**
     * Updates the selected camera selector.
     * @param selector The new camera selector to apply.
     */
    fun onCameraSelectorChanged(selector: CameraSelector) {
        this._selector.value = selector
    }

    /**
     * Updates the Firebase Storage download URL after a successful upload.
     * @param uri The Firebase Storage download URL.
     */
    fun onImageUriChanged(uri: Uri?) {
        _imageUri.value = uri
    }

    /**
     * Updates the local MediaStore URI immediately after photo capture.
     * @param uri The local content URI of the captured image.
     */
    fun onLocalImageUriChanged(uri: Uri?) {
        _localImageUri.value = uri
    }
}