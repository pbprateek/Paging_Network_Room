package com.prateek.github.githubsearch;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.prateek.github.githubsearch.Network.GithubClient;
import com.prateek.github.githubsearch.Network.GithubService;
import com.prateek.github.githubsearch.db.GithubRepositoryLocal;
import com.prateek.github.githubsearch.db.GithubRepositoryLocalAndNetwork;
import com.prateek.github.githubsearch.db.RepoDatabase;
import com.prateek.github.githubsearch.models.Repo;
import com.prateek.github.githubsearch.ui.ReposAdapter;
import com.prateek.github.githubsearch.ui.SearchRepositoriesViewModel;
import com.prateek.github.githubsearch.ui.ViewModelFactory;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String LAST_SEARCH_QUERY = "last_search_query";
    private static final String  DEFAULT_QUERY = "Android";

    private SearchRepositoriesViewModel viewModel;
    private ReposAdapter adapter;
    private RecyclerView list;
    private String query;
    private EditText editText;
    private TextView emptyList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_repositories);
        list=findViewById(R.id.list);
        editText=findViewById(R.id.search_repo);
        emptyList=findViewById(R.id.emptyList);



        RepoDatabase database = RepoDatabase.getDatabase(this);
        GithubRepositoryLocal repositoryLocal=new GithubRepositoryLocal(database.wordDao(), Executors.newSingleThreadExecutor());

        GithubRepositoryLocalAndNetwork repositoryLocalAndNetwork=new GithubRepositoryLocalAndNetwork(GithubClient.getClient().create(GithubService.class),repositoryLocal);

        viewModel= ViewModelProviders.of(this,new ViewModelFactory(repositoryLocalAndNetwork)).get(SearchRepositoriesViewModel.class);

        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        list.addItemDecoration(decoration);


        initAdapter();
        if(savedInstanceState!=null) {
            query = savedInstanceState.getString(LAST_SEARCH_QUERY);
        }
        else{
            query=DEFAULT_QUERY;
        }
        viewModel.searchRepo(query);
        initSearch(query);
    }

    private void initSearch(String query) {

        editText.setText(query);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_GO){
                    updateRepoListFromInput();
                    return true;
                }
                return false;
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                    updateRepoListFromInput();
                    return true;
                }
                return false;
            }
        });


    }

    private void updateRepoListFromInput() {

        String qu=editText.getText().toString().trim();
        if(!qu.isEmpty()){
            list.scrollToPosition(0);
            viewModel.searchRepo(qu);
            adapter.submitList(null); //to avoid error i guess
        }
    }

    private void initAdapter() {

        adapter=new ReposAdapter();

        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);


        viewModel.repos.observe(this, new Observer<PagedList<Repo>>() {
            @Override
            public void onChanged(@Nullable PagedList<Repo> repos) {
                Log.d("Activity", "list:"+repos.size());
                showEmptyList(repos.size()==0);
                Log.d("HERE123","ReposObserveMain");
                adapter.submitList(repos);

            }
        });

        viewModel.networkErrors.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void showEmptyList(boolean show) {


        if (show) {
            emptyList.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
        } else {
            emptyList.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
        }

    }
}
