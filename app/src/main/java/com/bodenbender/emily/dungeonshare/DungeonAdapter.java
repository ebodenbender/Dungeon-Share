package com.bodenbender.emily.dungeonshare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


public class DungeonAdapter extends RecyclerView.Adapter<DungeonAdapter.DungeonViewHolder> {
    private DungeonList dungeonList;
    private LayoutInflater inflater;
    private ItemClickListener clickListener;

    public DungeonAdapter(Context context, DungeonList dungeonList) {
        this.inflater = LayoutInflater.from(context);
        this.dungeonList = dungeonList;
    }

    @NonNull
    @Override
    public DungeonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.dungeon_card, parent, false);
        return new DungeonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DungeonViewHolder holder, int position) {
        String dungeonName = dungeonList.getDungeonAt(position).getDungeon_name();
        String dmName = dungeonList.getDungeonAt(position).getDm_name();
        holder.dungeonName.setText(dungeonName);
        holder.dmName.setText(dmName);
    }

    @Override
    public int getItemCount() {
        return dungeonList.getDungeonSize();
    }

    public class DungeonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView dungeonName;
        TextView dmName;
        CardView cardView;

        public DungeonViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.dungeonCard);
            dungeonName = itemView.findViewById(R.id.dungeonName);
            dmName = itemView.findViewById(R.id.dmName);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    public Dungeon getItem(int id) {
        return dungeonList.getDungeonAt(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface DungeonList {
        Dungeon getDungeonAt(int position);
        int getDungeonSize();
    }
}
