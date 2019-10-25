package com.macbitsgoa.bitsgridwatch.rankings;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.macbitsgoa.bitsgridwatch.R;

public class RankingViewHolder extends RecyclerView.ViewHolder {

    TextView rankTv,scoreTv;
    public RankingViewHolder(@NonNull View itemView) {
        super(itemView);

        rankTv=itemView.findViewById(R.id.rank_tv);
        scoreTv=itemView.findViewById(R.id.score_tv);
    }
}
