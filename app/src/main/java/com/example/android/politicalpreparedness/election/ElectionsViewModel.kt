package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.domain.ApiStatus
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.ElectionsRepository
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * [ViewModel] designed to store and manage UI-related data in a lifecycle conscious way. Used in
 * [com.example.android.politicalpreparedness.election.ElectionsFragment].
 */
class ElectionsViewModel(application: Application): ViewModel() {

    // The elections database and elections repository
    private val electionsDatabase = ElectionDatabase.getInstance(application)
    private val electionsRepository = ElectionsRepository(electionsDatabase)

    // Live Data to hold the list of upcoming elections
    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    // Live Data to hold the list of saved elections
    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections: LiveData<List<Election>>
        get() = _savedElections

    // Live Data to hold whether the user is/has navigated to the election details screen
    private val _navigateToVoterInformation = MutableLiveData<Election>()
    val navigateToVoterInformation: LiveData<Election>
        get() = _navigateToVoterInformation

    // Live Data that stores the status of the most recent election request
    private val _status: MutableLiveData<ApiStatus> = MutableLiveData()
    val status: LiveData<ApiStatus>
        get() = _status

    init {
        getUpcomingElections()
        getSavedElections()
    }

    /**
     * Retrieves a [List] of upcoming [Election] instances.
     */
    private fun getUpcomingElections() {
        viewModelScope.launch {
            try {
                _status.value = ApiStatus.LOADING
                val elections = electionsRepository.getUpcomingElections()
                Timber.i("Upcoming elections: ${elections.size}")
                _upcomingElections.value = elections
                _status.value = ApiStatus.DONE
            } catch (e: Exception) {
                Timber.e(e, "Could not retrieve the upcoming elections.")
                _status.value = ApiStatus.ERROR
            }
        }
    }

    /**
     * Retrieves a [List] of saved [Election] instances.
     */
    private fun getSavedElections() {
        viewModelScope.launch {
            try {
                _savedElections.value = electionsRepository.getSavedElections()
                Timber.i("Saved elections: ${_savedElections.value?.size ?: 0}")
            } catch (e: Exception) {
                Timber.e(e, "Could not retrieve the saved elections.")
            }
        }
    }

    /**
     * Sets the [Election] to prior to navigating to the [VoterInfoFragment] view.
     */
    fun navigateToVoterInformation(election: Election) {
        _navigateToVoterInformation.value = election
    }

    /**
     * Clears the selected [Election] after navigating to the [VoterInfoFragment] view.
     */
    fun navigateToVoterInformationCompleted() {
        _navigateToVoterInformation.value = null
    }

    /**
     * Refreshes the saved elections.
     */
    fun refreshElections() {
        getSavedElections()
    }
}