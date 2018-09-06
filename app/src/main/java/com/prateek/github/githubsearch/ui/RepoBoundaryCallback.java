package com.prateek.github.githubsearch.ui;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.util.Log;

import com.prateek.github.githubsearch.Interfaces.InsertFinished;
import com.prateek.github.githubsearch.Network.GithubService;
import com.prateek.github.githubsearch.db.GithubRepositoryLocal;
import com.prateek.github.githubsearch.models.GithubSearchResponse;
import com.prateek.github.githubsearch.models.Repo;

import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;

public class RepoBoundaryCallback extends PagedList.BoundaryCallback{

    private String query;
    private GithubService githubService;
    private GithubRepositoryLocal githubRepositoryLocal;
    private MutableLiveData<String> networkErrors;

    private int lastRequestedPage = 1;
    private boolean isRequestInProgress = false;
    private final int NETWORK_PAGE_SIZE = 50;




    public RepoBoundaryCallback(String query, GithubService service, GithubRepositoryLocal repositoryLocal){

        this.query=query;
        this.githubService=service;
        this.githubRepositoryLocal=repositoryLocal;
        networkErrors = new MutableLiveData<>();
    }

    public MutableLiveData<String> getNetworkErrors() {
        return networkErrors;
    }

    @Override
    public void onZeroItemsLoaded() {
        requestAndSaveData(query);
    }

    @Override
    public void onItemAtEndLoaded(@NonNull Object itemAtEnd) {
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
