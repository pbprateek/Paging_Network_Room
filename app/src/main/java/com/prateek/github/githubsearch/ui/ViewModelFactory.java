package com.prateek.github.githubsearch.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.test.suitebuilder.annotation.Suppress;

import com.prateek.github.githubsearch.db.GithubRepositoryLocalAndNetwork;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private GithubRepositoryLocalAndNetwork localAndNetwork;
    public ViewModelFactory(GithubRepositoryLocalAndNetwork repositoryLocalAndNetwork){
        this.localAndNetwork=repositoryLocalAndNetwork;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        return (T) new SearchRepositoriesViewModel(localAndNetwork);
    }
}
