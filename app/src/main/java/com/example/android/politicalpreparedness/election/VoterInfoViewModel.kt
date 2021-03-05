package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.domain.ApiStatus
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.AdministrationBody
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.ElectionsRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class VoterInfoViewModel(application: Application) : ViewModel() {

    private val electionsDatabase = ElectionDatabase.getInstance(application)
    private val electionsRepository = ElectionsRepository(electionsDatabase)

    private val _voterInformationElection = MutableLiveData<Election>()
    val voterInformationElection: LiveData<Election>
        get() = _voterInformationElection

    private val _voterInformationElectionAdministrationBody = MutableLiveData<AdministrationBody>()
    val voterInformationElectionAdministrationBody: LiveData<AdministrationBody>
        get() = _voterInformationElectionAdministrationBody

    private val _voterInformationCorrespondenceAddress = MutableLiveData<Address>()
    val voterInformationCorrespondenceAddress: LiveData<Address>
        get() = _voterInformationCorrespondenceAddress

    private val _isElectionSaved = MutableLiveData<Boolean>()
    val isElectionSaved: LiveData<Boolean>
        get() = _isElectionSaved

    private val _url = MutableLiveData<String>()
    val url: LiveData<String>
        get() = _url

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status

    fun getVoterInformation(electionId: Int, division: Division) {
        viewModelScope.launch {
            try {
                _status.value = ApiStatus.LOADING
                val savedElection : Election? = electionsDatabase.electionDao.getElection(electionId)
                _isElectionSaved.value = savedElection != null
                val voterInformationResponse = electionsRepository.getVoterInformation(electionId, division)
                _voterInformationElection.value = voterInformationResponse.election
                _voterInformationElectionAdministrationBody.value = voterInformationResponse.state?.first()?.electionAdministrationBody
                _voterInformationCorrespondenceAddress.value = voterInformationResponse.state?.first()?.electionAdministrationBody?.correspondenceAddress
                _status.value = ApiStatus.DONE
            } catch (e: Exception) {
                _status.value = ApiStatus.ERROR
                clear()
            }
        }
    }

    fun followUnfollowElection() {
        viewModelScope.launch {
            _voterInformationElection.value?.let {
                if (_isElectionSaved.value == true) {
                    electionsRepository.deleteElection(it.id)
                    _isElectionSaved.value = false
                } else {
                    electionsRepository.saveElection(it)
                    _isElectionSaved.value = true
                }
            }
        }
    }

    fun navigateToUrl(url : String) {
        _url.value = url
    }

    fun navigateToUrlCompleted() {
        _url.value = null
    }

    private fun clear() {
        _voterInformationElection.value = null
        _voterInformationElectionAdministrationBody.value = null
        _voterInformationCorrespondenceAddress.value = null
    }
}