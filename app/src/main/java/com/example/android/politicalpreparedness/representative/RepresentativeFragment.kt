package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.domain.ApiStatus
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListener
import com.example.android.politicalpreparedness.representative.model.Representative
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.*

/**
 * A [Fragment] that displays a [List] of [Representative] instances.
 */
class RepresentativeFragment : Fragment() {

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 99
    }

    private val viewModel: RepresentativeViewModel by lazy {
        ViewModelProvider(this).get(RepresentativeViewModel::class.java)
    }

    private lateinit var representativesListAdapter: RepresentativeListAdapter
    private lateinit var binding: FragmentRepresentativeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        
        representativesListAdapter = RepresentativeListAdapter(RepresentativeListener {
            Timber.i("Representative clicked.")
        })

        binding.representativesRecyclerView.adapter = representativesListAdapter

        viewModel.representatives.observe(viewLifecycleOwner, Observer<List<Representative>> { representatives ->
            representativesListAdapter.representatives = representatives
        })

        binding.buttonSearch.setOnClickListener {
            hideKeyboard()
            viewModel.getRepresentatives()
        }

        binding.buttonLocation.setOnClickListener {
            hideKeyboard()
            getRepresentativesByLocation()
        }

        viewModel.status.observe(viewLifecycleOwner, Observer<ApiStatus> { apiStatus ->
            when (apiStatus) {
                ApiStatus.ERROR -> {
                    Snackbar.make(requireView(), R.string.error_representatives, Snackbar.LENGTH_LONG).show()
                }
                else -> {}
            }
        })

        return binding.root
    }

    /**
     * Retrieves a [List] of [Representative] instances using the user's location.
     */
    private fun getRepresentativesByLocation() {
        return if (isPermissionGranted()) {
            getLocation()
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Snackbar.make(requireView(), R.string.error_location_services_required, Snackbar.LENGTH_LONG).show()
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
            }
        }
    }

    /**
     * Determines if the user has granted permission to location services.
     */
    private fun isPermissionGranted() : Boolean {
        return (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    /**
     * Obtains the user's location and retrieves the [List] of [Representative] instances based on the location's [Address].
     */
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        // Use Location Service to obtain a FusedLocationProviderClient
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation.addOnSuccessListener { location:Location? ->
            if (location != null) {
                val address = geoCodeLocation(location)
                viewModel.getRepresentatives(address)
            }
        }.addOnFailureListener { e -> Timber.e(e, "Error obtaining last location.") }
    }

    /**
     * Retrieves an [Address] using the [Geocoder].
     */
    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    /**
     * Callback when a request for permission has been made and processed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Check if the request code was the same code we used
        if (PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION == requestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // User has granted permissions, so request the location
                getLocation()
            } else {
                // User has denied permissions so show a snackbar message
                Snackbar.make(requireView(), R.string.error_location_services_required, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Hides the system keyboard.
     */
    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }
}