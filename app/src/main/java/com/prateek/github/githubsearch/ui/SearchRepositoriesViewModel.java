package com.prateek.github.githubsearch.ui;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;
import android.util.Log;

import com.prateek.github.githubsearch.db.GithubRepositoryLocalAndNetwork;
import com.prateek.github.githubsearch.models.Repo;
import com.prateek.github.githubsearch.models.RepoSearchResult;

import java.util.List;

public class SearchRepositoriesViewModel extends ViewModel {

    private GithubRepositoryLocalAndNetwork repositoryLocalAndNetwork;
    private static final int VISIBLE_THRESHOLD = 5;
    private MutableLiveData<String> queryLiveData;
    private LiveData<RepoSearchResult> repoResult;
    public LiveData<PagedList<Repo>> repos;
    public LiveData<String> networkErrors;

    public SearchRepositoriesViewModel(final GithubRepositoryLocalAndNetwork githubRepositoryLocalAndNetwork) {
        
        //if u want to use Application Context in ur viewmodel then exten AndroidViewModel instead of ViewModel.

        this.repositoryLocalAndNetwork = githubRepositoryLocalAndNetwork;
        queryLiveData = new MutableLiveData<String>();


        //why use transformation?
        //The transformations aren't calculated unless an observer is observing the returned LiveData object.
        //And if there is a bridge between view and repository then u need Transformation to access livedata which is 
        //in repository and u want to access it in view.then use Transformation in viewmodel.


        //here basically we will get RepoSearchResult based on our query,we are transforming
        //it just transforms a query into Searchresult(We will have to provide our own funcanility)
        //Transformations.map observes the LiveData. Whenever a new value is available:
        //it takes the value, applies the Function on in, and sets the Function’s output as a value on the LiveData it returns.
        repoResult = Transformations.map(queryLiveData, new Function<String, RepoSearchResult>() {
            @Override
            public RepoSearchResult apply(String input) {
                Log.d("HERE123","TransformMap");
                return githubRepositoryLocalAndNetwork.Search(input);
            }
        });



        //what basically a switchmap does inside the hood is that it uses MediatorLiveData and whenever a new source is added it removes
        //the previous source,so users can see only the latest results.(Basically removing connection from old livedata)
        //it removes the observer and adds a new one for us with latest LiveData
        repos = Transformations.switchMap(repoResult, new Function<RepoSearchResult, LiveData<PagedList<Repo>>>() {
            @Override
            public LiveData<PagedList<Repo>> apply(RepoSearchResult input) {
                Log.d("HERE123","TransformSwitch");
                return input.getData();
            }
        });

        networkErrors = Transformations.switchMap(repoResult, new Function<RepoSearchResult, LiveData<String>>() {
            @Override
            public LiveData<String> apply(RepoSearchResult input) {
                return input.getNetworkErrors();
            }
        });
    }


    /**
     * Search a repository based on a query string.
     */

    public void searchRepo(String query){
        Log.d("HERE123","SearchViewModel");
        queryLiveData.postValue(query);

    }



    /**
     * Get the last query value.
     */

    private String lastQueryValue(){
        return  queryLiveData.getValue();
    }




}
