package com.macbitsgoa.bitsgridwatch.rankings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.macbitsgoa.bitsgridwatch.R;

import java.util.ArrayList;

public class RankingAdapter extends RecyclerView.Adapter<RankingViewHolder> {

    ArrayList<RankingModel> rankingModels;

    public RankingAdapter(ArrayList<RankingModel> rankingModels){
        this.rankingModels=rankingModels;
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.rank_item, parent, false);
        return new RankingViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int i) {
        holder.scoreTv.setText(String.valueOf(rankingModels.get(i).score));
        holder.rankTv.setText(String.valueOf(rankingModels.get(i).rank));
    }

    @Override
    public int getItemCount() {
        return rankingModels.size();
    }
}
