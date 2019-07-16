package com.mojodigi.selfiepro.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mojodigi.selfiepro.R;
import com.mojodigi.selfiepro.enums.StickersRecyclerType;
import com.mojodigi.selfiepro.interfaces.OnStickersRecyclerSelected;
import com.mojodigi.selfiepro.model.StickersRecyclerModel;

import java.util.ArrayList;
import java.util.List;

public class StickersListRecyclerAdapter extends RecyclerView.Adapter<StickersListRecyclerAdapter.ViewHolder> {

    private List<StickersRecyclerModel> stickersRecyclerModelList  ;

    private OnStickersRecyclerSelected mOnStickersRecyclerSelected;

    private static int mPosition = -1;

    public StickersListRecyclerAdapter(OnStickersRecyclerSelected onCollageEditTollsSelected) {

        mOnStickersRecyclerSelected = onCollageEditTollsSelected;
        stickersRecyclerModelList = new ArrayList<>();

        stickersRecyclerModelList.add(new StickersRecyclerModel("Trendy", R.drawable.sticker_1, StickersRecyclerType.STICKER_ONE));
        stickersRecyclerModelList.add(new StickersRecyclerModel("Minions", R.drawable.sticker_28, StickersRecyclerType.STICKER_TWO));
        stickersRecyclerModelList.add(new StickersRecyclerModel("Comic", R.drawable.sticker_39, StickersRecyclerType.STICKER_THREE));
        stickersRecyclerModelList.add(new StickersRecyclerModel("Couple", R.drawable.sticker_52, StickersRecyclerType.STICKER_FOUR));
        stickersRecyclerModelList.add(new StickersRecyclerModel("Kitty", R.drawable.sticker_21, StickersRecyclerType.STICKER_FIVE));
        stickersRecyclerModelList.add(new StickersRecyclerModel("Love", R.drawable.love_1, StickersRecyclerType.STICKER_SIX));
        stickersRecyclerModelList.add(new StickersRecyclerModel("Donald Duck", R.drawable.duck_1, StickersRecyclerType.STICKER_SEVEN));
        stickersRecyclerModelList.add(new StickersRecyclerModel("Doraemon", R.drawable.doraemon_1, StickersRecyclerType.STICKER_EIGHT));
        stickersRecyclerModelList.add(new StickersRecyclerModel("Shin Chan", R.drawable.shin_chan_1, StickersRecyclerType.STICKER_NINE));

    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_stickers_recycler, parent, false);

        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        StickersRecyclerModel item = stickersRecyclerModelList.get(position);

        holder.stickerRecyclerText.setText(item.stickersRecyclerName);
        holder.stickerRecyclerImgView.setImageResource(item.stickersRecyclerIcon);

        if(mPosition >=0 && mPosition ==  position){
            holder.stickerRecyclerItemLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        else
        {
            holder.stickerRecyclerItemLayout.setBackgroundColor(Color.parseColor("#fcf5df"));
        }

    }

    @Override
    public int getItemCount() {
        return stickersRecyclerModelList.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout stickerRecyclerItemLayout;
        ImageView stickerRecyclerImgView;
        TextView stickerRecyclerText;

        ViewHolder(View itemView) {
            super(itemView);

            stickerRecyclerItemLayout = itemView.findViewById(R.id.stickerRecyclerItemLayout);

            stickerRecyclerImgView = itemView.findViewById(R.id.stickerRecyclerImgView);
            stickerRecyclerText = itemView.findViewById(R.id.stickerRecyclerText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPosition = getAdapterPosition();
                    notifyDataSetChanged();
                    // Log.e( "mPosition "+mPosition , "position "+getAdapterPosition() );
                    mOnStickersRecyclerSelected.onStickersRecyclerSelected(stickersRecyclerModelList.get(getLayoutPosition()).stickersRecyclerType);

                }
            });
        }
    }
}

