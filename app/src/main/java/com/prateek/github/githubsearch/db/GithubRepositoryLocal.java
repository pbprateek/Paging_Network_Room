package com.prateek.github.githubsearch.db;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.util.Log;

import com.prateek.github.githubsearch.Interfaces.InsertFinished;
import com.prateek.github.githubsearch.models.Repo;

import java.util.List;
import java.util.concurrent.Executor;


//Class that handles the DAO local data source.
public class GithubRepositoryLocal {

    private RepoDao repoDao;
    private Executor executor;

    public GithubRepositoryLocal(RepoDao dao, Executor exe){
        this.repoDao=dao;
        this.executor=exe;
    }

    public void insert(final List<Repo> repos, final InsertFinished finished){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                repoDao.insert(repos);
                Log.d("HERE123","insertLocal");
                finished.insertFinished();
            }
        });
    }


    public DataSource.Factory<Integer, Repo> reposByName(String query){
        Log.d("HERE123","reposBynameLocal");
        return repoDao.reposByName("%" + query.replace(' ', '%') + "%");
    }


}
