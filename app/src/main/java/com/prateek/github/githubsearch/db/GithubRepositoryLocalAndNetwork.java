package com.prateek.github.githubsearch.db;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.prateek.github.githubsearch.Interfaces.InsertFinished;
import com.prateek.github.githubsearch.Network.GithubService;
import com.prateek.github.githubsearch.models.GithubSearchResponse;
import com.prateek.github.githubsearch.models.Repo;
import com.prateek.github.githubsearch.models.RepoSearchResult;

import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;

//Repository class that works with local and remote data sources.
public class GithubRepositoryLocalAndNetwork {

    private final int NETWORK_PAGE_SIZE = 50;

    private GithubService githubService;
    private GithubRepositoryLocal githubRepositoryLocal;
    private MutableLiveData<String> networkErrors;

    private int lastRequestedPage = 1;
    private boolean isRequestInProgress = false;

    public GithubRepositoryLocalAndNetwork(GithubService service, GithubRepositoryLocal repositoryLocal) {
        this.githubRepositoryLocal = repositoryLocal;
        this.githubService = service;
        networkErrors = new MutableLiveData<>();
    }

    public RepoSearchResult Search(String query) {
        lastRequestedPage = 1;
        requestAndSaveData(query);

        // Get data from the local cache
        LiveData<List<Repo>> data = githubRepositoryLocal.reposByName(query);
        if (data.getValue() != null)
            Log.d("HERE1", data.getValue().size() + "");


        return new RepoSearchResult(data, networkErrors);
    }

    public void requestMore(String query) {
        requestAndSaveData(query);
    }

    private void requestAndSaveData(String query) {
        if (isRequestInProgress)
            return;

        query = query + "in:name,description";
        isRequestInProgress = true;

        retrofit2.Call<GithubSearchResponse> callback = githubService.searchRepos(query, lastRequestedPage, NETWORK_PAGE_SIZE);
        callback.enqueue(new Callback<GithubSearchResponse>() {
            @Override
            public void onResponse(retrofit2.Call<GithubSearchResponse> call, Response<GithubSearchResponse> response) {

                if (response.isSuccessful()) {
                    List<Repo> repos = response.body().getItems();
                    githubRepositoryLocal.insert(repos, new InsertFinished() {
                        @Override
                        public void insertFinished() {
                            lastRequestedPage++;
                            isRequestInProgress = false;
                        }
                    });

                } else {
                    networkErrors.postValue(response.errorBody().toString());
                    isRequestInProgress = false;
                }


            }

            @Override
            public void onFailure(retrofit2.Call<GithubSearchResponse> call, Throwable t) {

                networkErrors.postValue(t.getMessage());
                isRequestInProgress = false;
                Log.d("HERE1", "hereabc");

            }
        });


    }
}
