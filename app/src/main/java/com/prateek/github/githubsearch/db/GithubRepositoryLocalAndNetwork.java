package com.prateek.github.githubsearch.db;


import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.prateek.github.githubsearch.Network.GithubService;
import com.prateek.github.githubsearch.models.RepoSearchResult;
import com.prateek.github.githubsearch.ui.RepoBoundaryCallback;

//Repository class that works with local and remote data sources.
public class GithubRepositoryLocalAndNetwork {


    private final int DATABASE_PAGE_SIZE = 20;

    private GithubService githubService;
    private GithubRepositoryLocal githubRepositoryLocal;


    public GithubRepositoryLocalAndNetwork(GithubService service, GithubRepositoryLocal repositoryLocal) {
        this.githubRepositoryLocal = repositoryLocal;
        this.githubService = service;

    }

    public RepoSearchResult Search(String query) {
        // Get data from the local cache
        DataSource.Factory dataSourceFactory = githubRepositoryLocal.reposByName(query);

        RepoBoundaryCallback boundaryCallback = new RepoBoundaryCallback(query, githubService, githubRepositoryLocal);

        LiveData<String> networkErrors = boundaryCallback.getNetworkErrors();


        //returns LiveData
        LiveData data = new LivePagedListBuilder(dataSourceFactory,
                new PagedList.Config.Builder().setPageSize(20)
                        .setPrefetchDistance(10)
                        .setEnablePlaceholders(true)
                        .build())
                .setBoundaryCallback(boundaryCallback).build();

        return new RepoSearchResult(data, networkErrors);
    }


}
