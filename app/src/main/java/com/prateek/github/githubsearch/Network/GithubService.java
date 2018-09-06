package com.prateek.github.githubsearch.Network;

import com.prateek.github.githubsearch.models.GithubSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GithubService {

    @GET("search/repositories?sort=stars")
    Call<GithubSearchResponse> searchRepos(@Query("q") String query,

                                           @Query("page") int page,

                                           @Query("per_page") int itemsPerPage);
}
