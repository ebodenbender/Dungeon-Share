package com.bodenbender.emily.dungeonshare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private DungeonRoomList roomList;
    private LayoutInflater inflater;
    private ItemClickListener clickListener;

    public RoomAdapter(Context context, DungeonRoomList roomList) {
        this.inflater = LayoutInflater.from(context);
        this.roomList = roomList;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.room_card, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        String roomName = roomList.getDungeonRoomAt(position).getRoom_name();
        String roomDescription = roomList.getDungeonRoomAt(position).getRoom_description();
        holder.roomName.setText(roomName);
        holder.roomDescription.setText(roomDescription);
    }

    @Override
    public int getItemCount() {
        return roomList.getDungeonSize();
    }

    public class RoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView roomName;
        TextView roomDescription;
        CardView cardView;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.roomCard);
            roomName = itemView.findViewById(R.id.roomName);
            roomDescription = itemView.findViewById(R.id.roomDescription);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    public DungeonRoom getItem(int id) {
        return roomList.getDungeonRoomAt(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface DungeonRoomList {
        DungeonRoom getDungeonRoomAt(int position);
        int getDungeonSize();
    }

}
