package com.example.android.politicalpreparedness.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.politicalpreparedness.network.models.Election

/**
 * A Data Access Object for use with [ElectionDatabase]. Contains CRUD methods.
 */
@Dao
interface ElectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(election: Election)

    @Query("SELECT * FROM election_table ORDER BY id DESC")
    fun getAllElections(): List<Election>

    @Query("SELECT * from election_table WHERE id = :id")
    suspend fun getElection(id: Int): Election?

    @Query("DELETE FROM election_table WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM election_table")
    suspend fun clear()
}