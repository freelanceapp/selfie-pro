package com.mojodigi.selfiepro.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mojodigi.selfiepro.R;
import com.mojodigi.selfiepro.enums.FrameType;
import com.mojodigi.selfiepro.interfaces.OnFrameSelected;
import com.mojodigi.selfiepro.model.FrameModel;

import java.util.ArrayList;
import java.util.List;

public class FrameRecycleAdapter extends RecyclerView.Adapter<FrameRecycleAdapter.ViewHolder> {

    private Context mContext ;
    private List<FrameModel> mFrameList ;
    private OnFrameSelected mOnFrameSelected;


    public  FrameRecycleAdapter(Context context, ArrayList<FrameModel> collageFrameList , OnFrameSelected onFrameSelected){

        this.mContext = context;
        this.mFrameList = collageFrameList;
        this.mOnFrameSelected = onFrameSelected;

    }



    public FrameRecycleAdapter(OnFrameSelected onFrameSelected) {

        mOnFrameSelected = onFrameSelected;

        mFrameList = new ArrayList<>();


       // mFrameList.add(new FrameModel("
        // ", R.drawable.frame_11,  FrameType.F_ELEVEN));
      //  mFrameList.add(new FrameModel("TWELVE", R.drawable.frame_12,   FrameType.F_TWELVE));

        mFrameList.add(new FrameModel("NONE", R.drawable.svg_none, R.drawable.svg_none,   FrameType.F_NONE));
        mFrameList.add(new FrameModel("ONE", R.drawable.small_frame_1, R.drawable.frame_1,   FrameType.F_ONE));
        mFrameList.add(new FrameModel("TWO", R.drawable.small_frame_2, R.drawable.frame_2,  FrameType.F_TWO));
        mFrameList.add(new FrameModel("THREE",  R.drawable.small_frame_3, R.drawable.frame_3,   FrameType.F_THREE));
        mFrameList.add(new FrameModel("FOUR",  R.drawable.small_frame_4, R.drawable.frame_4,  FrameType.F_FOUR));
        mFrameList.add(new FrameModel("FIVE", R.drawable.small_frame_5, R.drawable.frame_5,   FrameType.F_FIVE));
        mFrameList.add(new FrameModel("SIX",  R.drawable.small_frame_6, R.drawable.frame_6,  FrameType.F_SIX));
        mFrameList.add(new FrameModel("SEVEN",  R.drawable.small_frame_7, R.drawable.frame_7,   FrameType.F_SEVEN));
        mFrameList.add(new FrameModel("EIGHT",  R.drawable.small_frame_8, R.drawable.frame_8,  FrameType.F_EIGHT));
        mFrameList.add(new FrameModel("NINE", R.drawable.small_frame_9, R.drawable.frame_9,   FrameType.F_NINE));
        mFrameList.add(new FrameModel("TEN",  R.drawable.frame_12, R.drawable.frame_12,   FrameType.F_TEN));

        // mFrameList.add(new FrameModel("ELEVEN",  R.drawable.small_frame_11, R.drawable.frame_11,   FrameType.F_ELEVEN));
        //mFrameList.add(new FrameModel("TWELVE",  R.drawable.small_frame_12, R.drawable.frame_12,   FrameType.F_TWELVE));

    }




    @NonNull
    @Override
    public FrameRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {


        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.frame_itemview, viewGroup, false);
        return new FrameRecycleAdapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull FrameRecycleAdapter.ViewHolder viewHolder, final int position) {

        FrameModel item = mFrameList.get(position);

        viewHolder.mFrameItemImage.setImageResource(item.mFrameIdSmall);
        viewHolder.mFrameItemText.setText(item.mFrameName);



    }

    @Override
    public int getItemCount() {

        return(null!=mFrameList? mFrameList.size():0);
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public  View mItemView;

        public ConstraintLayout mFrameItemViewLayout;
        public ImageView mFrameItemImage;
        public TextView mFrameItemText;

        ViewHolder(View itemView) {
            super(itemView);

            this.mItemView = itemView;

            mFrameItemViewLayout =(ConstraintLayout)itemView.findViewById(R.id.idFrameItemViewLayout);
            mFrameItemImage =(ImageView)itemView.findViewById(R.id.idFrameItemViewImage);
            mFrameItemText = (TextView)itemView.findViewById(R.id.idFrameItemViewText);

//            mCollageFrameItemLLayout = (LinearLayout) itemView.findViewById(R.id.idCollageFrameItemLLayout);
//            mCollageFrameItemImage =(ImageView)itemView.findViewById(R.id.idCollageFrameItemImage);
//            mCollageFrameItemText = (TextView)itemView.findViewById(R.id.idCollageFrameItemText);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnFrameSelected.onFrameSelected(mFrameList.get(getLayoutPosition()).getmFrameIdBig(), mFrameList.get(getLayoutPosition()).getmFrameType());

                }
            });


        }
    }

}

