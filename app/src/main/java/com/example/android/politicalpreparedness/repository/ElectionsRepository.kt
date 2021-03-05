package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * The repository class containing operations for use with both network and database operations.
 */
class ElectionsRepository(private val electionDatabase: ElectionDatabase) {

    /**
     * Retrieves upcoming elections from the Google Civics API.
     */
    suspend fun getUpcomingElections(): List<Election> {
        return CivicsApi.retrofitService.getElections().elections
    }

    /**
     * Retrieves saved elections from the database.
     */
    suspend fun getSavedElections() : List<Election>? {
        var savedElections:List<Election>? = null
        withContext(Dispatchers.IO) {
            savedElections = electionDatabase.electionDao.getAllElections()
        }

        return savedElections
    }

    /**
     * Retrieves voter information from the Google Civics API.
     */
    suspend fun getVoterInformation(electionId : Int, division: Division): VoterInfoResponse {
        var address : String = division.country
        if (division.country.isNotBlank() && division.state.isNotBlank()) {
            address = division.country  + ", " + division.state
        }

        return CivicsApi.retrofitService.getVoterInformation(address, electionId.toLong(), false)
    }

    /**
     * Deletes an [Election] from the database.
     */
    suspend fun deleteElection(electionId: Int) {
        withContext(Dispatchers.IO) {
            electionDatabase.electionDao.delete(electionId)
        }
    }

    /**
     * Saves an [Election] to the database.
     */
    suspend fun saveElection(election: Election) {
        withContext(Dispatchers.IO) {
            electionDatabase.electionDao.insert(election)
        }
    }
}