package com.mojodigi.selfiepro.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mojodigi.selfiepro.R;
import com.mojodigi.selfiepro.enums.BackgroundType;
import com.mojodigi.selfiepro.interfaces.BackgroundListener;
import com.mojodigi.selfiepro.model.BackgroundModel;

import java.util.ArrayList;
import java.util.List;


public class BackgroundAdapter extends RecyclerView.Adapter<BackgroundAdapter.ViewHolder> {

    private BackgroundListener mBackgroundListener;
    private Context mContext ;
    private List<BackgroundModel> mBackgroundList ;


    public  BackgroundAdapter(Context context, ArrayList<BackgroundModel> collageFrameList , BackgroundListener backgroundListener){

        this.mContext = context;
        this.mBackgroundList = collageFrameList;
        this.mBackgroundListener = backgroundListener;

    }

    public BackgroundAdapter(BackgroundListener backgroundListener) {

        mBackgroundListener = backgroundListener;

        mBackgroundList = new ArrayList<>();

        mBackgroundList.add(new BackgroundModel("NONE", R.drawable.bg_00,  BackgroundType.NONE));
        mBackgroundList.add(new BackgroundModel("ONE", R.drawable.bg_01,  BackgroundType.ONE));
        mBackgroundList.add(new BackgroundModel("TWO", R.drawable.bg_02,   BackgroundType.TWO));
        mBackgroundList.add(new BackgroundModel("THREE", R.drawable.bg_03,  BackgroundType.THREE));
        mBackgroundList.add(new BackgroundModel("FOUR", R.drawable.bg_04,   BackgroundType.FOUR));
        mBackgroundList.add(new BackgroundModel("FIVE", R.drawable.bg_05,  BackgroundType.FIVE));
        mBackgroundList.add(new BackgroundModel("SIX", R.drawable.bg_06,  BackgroundType.SIX));
        mBackgroundList.add(new BackgroundModel("SEVEN", R.drawable.bg_07,   BackgroundType.SEVEN));
        mBackgroundList.add(new BackgroundModel("EIGHT", R.drawable.bg_08,   BackgroundType.EIGHT));
        mBackgroundList.add(new BackgroundModel("NINE", R.drawable.bg_09,   BackgroundType.NINE));
        mBackgroundList.add(new BackgroundModel("TEN", R.drawable.bg_10,   BackgroundType.TEN));



    }




    @NonNull
    @Override
    public BackgroundAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {


        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_collage_filter, viewGroup, false);
        // View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.collage_frame_itemview, viewGroup, false);
        return new BackgroundAdapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull BackgroundAdapter.ViewHolder viewHolder, final int position) {

        BackgroundModel item = mBackgroundList.get(position);

        viewHolder.mBackgroundItemImage.setImageResource(item.backgroundIdSmall);
        viewHolder.mBackgroundItemText.setText(item.backgroundName);



    }

    @Override
    public int getItemCount() {

        return(null!=mBackgroundList? mBackgroundList.size():0);
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public  View mItemView;

        //public LinearLayout mBackgroundItemLLayout;
        public ImageView mBackgroundItemImage;
        public TextView mBackgroundItemText;

        ViewHolder(View itemView) {
            super(itemView);

            this.mItemView = itemView;

            mBackgroundItemImage =(ImageView)itemView.findViewById(R.id.idCollageItemViewImage);
            mBackgroundItemText = (TextView)itemView.findViewById(R.id.idCollageItemViewText);

//            mBackgroundItemLLayout = (LinearLayout) itemView.findViewById(R.id.idBackgroundItemLLayout);
//            mBackgroundItemImage =(ImageView)itemView.findViewById(R.id.idBackgroundItemImage);
//            mBackgroundItemText = (TextView)itemView.findViewById(R.id.idBackgroundItemText);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBackgroundListener.onBackgroundSelected(mBackgroundList.get(getLayoutPosition()).getBackgroundIdBig(), mBackgroundList.get(getLayoutPosition()).getBackgroundType());

                }
            });


        }
    }

}

