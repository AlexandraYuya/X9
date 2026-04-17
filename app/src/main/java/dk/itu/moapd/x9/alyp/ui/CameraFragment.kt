package dk.itu.moapd.x9.alyp.ui

import android.Manifest
import dk.itu.moapd.x9.alyp.R
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import dk.itu.moapd.x9.alyp.databinding.FragmentCameraBinding
import dk.itu.moapd.x9.alyp.viewmodel.CameraViewModel
import dk.itu.moapd.x9.alyp.viewmodel.ReportViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Inspired by "Getting Started with CameraX", from android studio offical documentation: https://developer.android.com/codelabs/camerax-getting-started#0
 * and inspired by Fabricio Narcizo's code examples from "10-4_CameraX-MDC": https://github.com/fabricionarcizo/moapd2026/tree/main/lecture10/10-4_CameraX-MDC
 */
class CameraFragment : Fragment() {

    /**
     * A set of private constants.
     */
    companion object {
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
        private const val TAG = "CameraFragment"
        private const val MIME_TYPE_JPEG = "image/jpeg"
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA, // can also include video permission
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
    private val cameraViewModel: CameraViewModel by activityViewModels()
    private val storage = Firebase.storage("gs://moapd-2026-bf43d.firebasestorage.app")
    private var _binding: FragmentCameraBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    /**
     * This instance provides `takePicture()` functions to take a picture to memory or save to a
     * file, and provides image metadata.
     */
    private var imageCapture: ImageCapture? = null

    /**
     * The camera selector allows to select a camera or return a filtered set of cameras.
     */
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private lateinit var cameraExecutor: ExecutorService
    // Small local cache for the observed imageUri so the click listener can reference it.
    private var imageUriLocal: Uri? = null

    /**
     * Permission launcher. Called when callback is initiated, and a location permission isn't found.
     * checks if location permission has been granted, if so subscribe to location service.
     */
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions() // have multiple request entries
    ) { permissions ->

        var permissionGranted = true
        permissions.entries.forEach { // loop through each permission entry, if theres no value then permission is set to false, or if anyof the permissions aren't granted
            if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                permissionGranted = false
        }
        // handle permission granted/rejected
        if (permissionGranted) {
            startCamera()
        } else {
            // Use view (nullable) to avoid crashes if view is destroyed
            view?.let {
                Snackbar.make(it, "Permission request denied", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        Log.d(TAG, "onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView() called")
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated() called")

        // Observe imageUri from the ViewModel so the fragment UI reflects the latest saved photo.
        cameraViewModel.imageUri.observe(viewLifecycleOwner) { uri ->
            // Update the local UI state if needed. Keep a small local cache so the click
            // listener below can access it without directly reading LiveData each time.
            imageUriLocal = uri
        }

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }

        // The current selected camera.
        cameraViewModel.selector.observe(viewLifecycleOwner) {
            // Only update the local selector when ViewModel provides a non-null value.
            // This avoids resetting to DEFAULT_BACK_CAMERA on configuration change
            // when LiveData doesn't have a value yet.
            cameraSelector = it ?: cameraSelector
        }

        // Set up the listeners for take photo and camera switch
        binding.apply {
            buttonImageCapture.setOnClickListener { takePhoto() }

            buttonCameraSwitch.apply {
                // disable button until the camera is set up
                isEnabled = false
                setOnClickListener {
                    cameraViewModel.onCameraSelectorChanged(
                        if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA)
                            CameraSelector.DEFAULT_BACK_CAMERA
                        else
                            CameraSelector.DEFAULT_FRONT_CAMERA
                    )
                    // Re-start to update selected camera
                    startCamera()
                }
            }

            // Set up the listener for the photo view button.
//            buttonImageViewer.setOnClickListener {
//                imageUriLocal?.let { uri ->
//                    requireActivity().findNavController()
//                        .navigate(R.id.a, bundleOf("ARG_IMAGE" to uri.toString()))
//                }
//            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
        cameraExecutor.shutdown()
    }

    /**
     * Called when 'take photo' btn is pressed.
     */
    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name for uniqueness.
        val timestamp = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
        val fileName = "IMG$timestamp.jpg"

        // create a MediaStore content value to hold the image
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)  // unique image filename
            put(MediaStore.MediaColumns.MIME_TYPE, MIME_TYPE_JPEG) // image format
            put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis())
            put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis() / 1000)
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM) // path to stored images
        }

        // Create output options object which contains file + metadata. Specify the format of our output.
        // Add our created MediaStore entry
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            requireContext().contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(output: ImageCapture.OutputFileResults){
                    // Create a storage reference from our app
                    val storageRef = storage.reference
                    val file = output.savedUri
                    // Create a child reference
                    val imagesRef: StorageReference = storageRef.child("images/${file?.lastPathSegment}")
                    val uploadTask = imagesRef.putFile(file!!)

                    uploadTask.continueWithTask { task ->
                        if(!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        imagesRef.downloadUrl
                    }.addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            val downloadUri = task.result
                            cameraViewModel.onImageUriChanged(downloadUri)
                            Toast.makeText(requireContext(), "Photo capture succeeded: ${output.savedUri}", Toast.LENGTH_SHORT).show()
                            Log.d(TAG, "Photo capture succeeded: ${output.savedUri}")
                        }else {
                            Toast.makeText(context, "failed to upload image to storage", Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }
            }
        )
    }

    private fun startCamera() {
        // Creates an instance which is used to bind the lifecycle of the camera to the lifecycle of the owner, so they're in sync. Eliminates the need to handle opening and closing the camera since CameraX is lifecycle aware.
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        // add listener to the camera provider
        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of our camera to the lifecycle owner within the app's process.
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Using CameraX Preview class, initialize object, get surface provider from viewfinder and set it on the preview to connect to UI.
            // viewFinder finds the 'PreviewView' from our layout file, setSurfaceProvider tells the preview where to send the captured frames to.
            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(binding.viewFinder.surfaceProvider) }

            imageCapture = ImageCapture.Builder().build()

            try {
                // Unbind before rebinding
                cameraProvider.unbindAll()

                // Bind to camera fragment lifecycle using class-level cameraSelector
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

                binding.buttonCameraSwitch.isEnabled = canSwitchCamera(cameraProvider)

            } catch(exc: Exception) { // catch if fails i.e if app is not in focus
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext())) // returns an executer than runs on the main thread
    }

    /**
     * Checks if the Android device has two cameras and it is possible to change the used camera.
     *
     * @param provider The process camera provider.
     */
    private fun canSwitchCamera(provider: ProcessCameraProvider): Boolean {
        return try {
            provider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) &&
                    provider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
        } catch (_: CameraInfoUnavailableException) {
            false
        }
    }

    private fun requestPermissions() {
        requestPermissionLauncher.launch(REQUIRED_PERMISSIONS) // launch the permission request screen
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }
}