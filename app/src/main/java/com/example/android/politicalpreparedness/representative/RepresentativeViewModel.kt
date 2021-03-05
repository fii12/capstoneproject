package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.domain.ApiStatus
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.RepresentativesRepository
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * [ViewModel] designed to store and manage UI-related data in a lifecycle conscious way. Used in
 * [com.example.android.politicalpreparedness.representative.RepresentativeFragment].
 */
class RepresentativeViewModel: ViewModel() {

    // The representatives repository
    private val representativesRepository = RepresentativesRepository()

    // Live Data to hold the list of representatives
    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    // Live Data to hold the address
    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    // Live Data that stores the status of the most recent representative request
    private val _status: MutableLiveData<ApiStatus> = MutableLiveData()
    val status: LiveData<ApiStatus>
        get() = _status

    init {
        _address.value = Address("","","","","")
    }

    /**
     * Retrieves a [List] of [Representative] instances using the provided [Address].
     */
    fun getRepresentatives(address: Address?) {
        viewModelScope.launch {
            _representatives.value = arrayListOf()
            if (address != null && address.isValidAddress()) {
                try {
                    _status.value = ApiStatus.LOADING
                    _address.value = address
                    val (offices, officials) = representativesRepository.getRepresentatives(address, true, null, null)
                    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }
                    Timber.i("Number of representatives: ${representatives.value?.size}")
                    _status.value = ApiStatus.DONE
                } catch (e: Exception) {
                    Timber.e(e, "Could not retrieve the representatives.")
                    _status.value = ApiStatus.ERROR
                }
            }
        }
    }

    /**
     * Retrieves a [List] of [Representative] instances using the stored [Address].
     */
    fun getRepresentatives() {
        viewModelScope.launch {
            getRepresentatives(_address.value)
        }
    }
}
