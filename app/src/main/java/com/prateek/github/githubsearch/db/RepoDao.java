package com.prateek.github.githubsearch.db;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.prateek.github.githubsearch.models.Repo;

import java.util.List;

@Dao
public interface RepoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Repo> repos);


    // Room generates all necessary code to update the LiveData when the database is updated.
    // As of version 1.0, Room uses the list of tables accessed in the query to decide whether to update instances of LiveData.


    @Query("SELECT * FROM repos WHERE (name LIKE :queryString) OR (description LIKE " +

            ":queryString) ORDER BY stars DESC, name ASC")
    LiveData<List<Repo>> reposByName(String queryString);


}
