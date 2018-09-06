package com.prateek.github.githubsearch.ui;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prateek.github.githubsearch.R;
import com.prateek.github.githubsearch.models.Repo;

public class ReposAdapter extends PagedListAdapter<Repo,RecyclerView.ViewHolder> {
    public ReposAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.repo_view_item,parent,false);
        return new RepoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Repo repo=getItem(position);
            ((RepoViewHolder)holder).bind(repo);

    }


    private static final DiffUtil.ItemCallback<Repo> DIFF_CALLBACK =new DiffUtil.ItemCallback<Repo>() {
        @Override
        public boolean areItemsTheSame(Repo oldItem, Repo newItem) {
            return oldItem.getFullName().equals(newItem.getFullName());
        }

        @Override
        public boolean areContentsTheSame(Repo oldItem, Repo newItem) {
            return oldItem.equals(newItem);
        }
    };
}
