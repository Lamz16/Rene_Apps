package com.lamz.reneapps.ui.add_story

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.lamz.reneapps.R
import com.lamz.reneapps.data.ResultState
import com.lamz.reneapps.databinding.FragmentAddStoryBinding
import com.lamz.reneapps.ui.main.MainActivity
import com.lamz.reneapps.ui.ViewModelFactory
import com.lamz.reneapps.ui.maps.MapsActivity
import com.lamz.reneapps.ui.welcome.WelcomeActivity

class AddStoryFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentAddStoryBinding? = null
    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var locationManager: LocationManager
    private var gpsStatus: Boolean = false

    private val binding get() = _binding!!
    private var currentImageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddStoryBinding.inflate(inflater, container, false)
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (!user.isLogin){
                startActivity(Intent(requireActivity(), WelcomeActivity::class.java))
                activity?.finish()
            }
            binding.uploadButton.setOnClickListener { getMyLastLocation() }
        }


        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


    }

    override fun onMapReady(p0: GoogleMap) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(requireActivity())
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLastLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }

                else -> {
                    // No location access granted.
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        val shareLocation = binding.btnLocation
        if (shareLocation.isChecked) {
            if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
                checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        uploadStory(location.latitude.toString(), location.longitude.toString())
                        Log.d(
                            "koordinat",
                            "getMylastLocation: ${location.latitude}, ${location.longitude}  "
                        )
                    } else {

                        val intents = Intent(requireContext(), MapsActivity::class.java)
                        startActivity(intents)

                        gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

                        Toast.makeText(
                            requireActivity(),
                            "Please open turn on your gps and set your location",
                            Toast.LENGTH_SHORT
                        ).show()
                        if (gpsStatus){
                            val intent = Intent(requireContext(), MapsActivity::class.java)
                            startActivity(intent)
                        }
                        else {
                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    }
                }
            } else {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        } else if (!shareLocation.isChecked) {
            uploadStory()
        }

    }

    private fun uploadStory() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, requireContext()).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.edtDescription.text.toString()

            viewModel.uploadImage( imageFile, description)
            viewModel.upload.observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> {
                            showLoading(true)
                        }

                        is ResultState.Success -> {
                            result.data.message.let { showToast(it) }
                            showLoading(false)
                            val intent = Intent(context, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            activity?.finish()
                        }

                        is ResultState.Error -> {
                            showToast(result.error)
                            showLoading(false)
                        }
                    }
                }

            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun uploadStory(lat: String, lon: String) {

        currentImageUri?.let { uri ->

            val imageFile = uriToFile(uri, requireContext()).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.edtDescription.text.toString()
            showLoading(true)

            viewModel.uploadImage(
                imageFile,
                description,
                lat, lon
            )
            viewModel.upload.observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> {
                            showLoading(true)
                        }

                        is ResultState.Success -> {
                            result.data.message.let { showToast(it) }
                            showLoading(false)
                            val intent = Intent(context, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            activity?.finish()
                        }

                        is ResultState.Error -> {
                            showToast(result.error)
                            showLoading(false)
                        }
                    }
                }
            }

        } ?: showToast(getString(R.string.empty_image_warning))
    }


}