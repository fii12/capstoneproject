package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse

/**
 * The repository class containing operations for use with network operations.
 */
class RepresentativesRepository() {

    /**
     * Retrieves representatives from the Google Civics API.
     */
    suspend fun getRepresentatives(address: Address, includeOffices: Boolean, levels: String?, roles: String?): RepresentativeResponse {
        return CivicsApi.retrofitService.getRepresentatives(address, includeOffices, levels, roles)
    }
}