package com.prateek.github.githubsearch.db;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.prateek.github.githubsearch.models.Repo;

@Database(
        entities = {Repo.class},
        version = 1,
        exportSchema = false
)
public abstract class RepoDatabase extends RoomDatabase {

    public abstract RepoDao wordDao();

    private static RepoDatabase repoDatabase = null;

    public static RepoDatabase getDatabase(final Context context) {

        if (repoDatabase == null) {
            synchronized (RepoDatabase.class) {
                if (repoDatabase == null) {

                    repoDatabase = Room.databaseBuilder(context.getApplicationContext(), RepoDatabase.class, "Github.db")
                            .build();

                }
            }
        }

        return repoDatabase;
    }


}
