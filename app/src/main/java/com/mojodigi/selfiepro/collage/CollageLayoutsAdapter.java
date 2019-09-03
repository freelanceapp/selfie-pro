package com.mojodigi.selfiepro.collage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mojodigi.selfiepro.R;

import java.util.ArrayList;
import java.util.List;



public class CollageLayoutsAdapter extends RecyclerView.Adapter<CollageLayoutsAdapter.ViewHolder> {

    private Context mContext ;
    private List<CollageLayoutsModel> mCollageFrameList ;
    private OnCollageLayoutSelected mOnCollageFrameSelected;


    public CollageLayoutsAdapter(Context context, ArrayList<CollageLayoutsModel> collageFrameList , OnCollageLayoutSelected onCollageFrameSelected){

        this.mContext = context;
        this.mCollageFrameList = collageFrameList;
        this.mOnCollageFrameSelected = onCollageFrameSelected;

    }

    public CollageLayoutsAdapter(OnCollageLayoutSelected onCollageFrameSelected) {

        mOnCollageFrameSelected = onCollageFrameSelected;

        mCollageFrameList = new ArrayList<>();

        mCollageFrameList.add(new CollageLayoutsModel("ONE", R.drawable.collage_1_0,   CollageLayoutsType.ONE_ZERO));
        mCollageFrameList.add(new CollageLayoutsModel("TWO", R.drawable.collage_2_1,   CollageLayoutsType.TWO_ONE));
        mCollageFrameList.add(new CollageLayoutsModel("THREE", R.drawable.collage_2_2,  CollageLayoutsType.TWO_TWO));
        mCollageFrameList.add(new CollageLayoutsModel("FOUR", R.drawable.collage_3_1,   CollageLayoutsType.THREE_ONE));
        mCollageFrameList.add(new CollageLayoutsModel("FIVE", R.drawable.collage_3_2,   CollageLayoutsType.THREE_TWO));
        mCollageFrameList.add(new CollageLayoutsModel("SIX", R.drawable.collage_4_1,    CollageLayoutsType.FOUR_ONE));
        mCollageFrameList.add(new CollageLayoutsModel("SEVEN", R.drawable.collage_4_2,   CollageLayoutsType.FOUR_TWO));
        mCollageFrameList.add(new CollageLayoutsModel("EIGHT", R.drawable.collage_4_4,   CollageLayoutsType.FOUR_FOUR));
        mCollageFrameList.add(new CollageLayoutsModel("NINE", R.drawable.collage_6_1,   CollageLayoutsType.SIX_ONE));


        //mCollageFrameList.add(new CollageFrameModel("Ten", R.drawable.collage_3_3,   CollageFrameType.THREE_THREE));
       // mCollageFrameList.add(new CollageFrameModel("Eleven", R.drawable.collage_4_3,  CollageFrameType.FOUR_THREE));

        /* mCollageFrameList.add(new CollageFrameModel("One", R.drawable.collage_1_0, R.drawable.collage_1_0, CollageFrameType.ONE_ZERO));
        mCollageFrameList.add(new CollageFrameModel("Seven", R.drawable.collage_4_2, R.drawable.collage4_2, CollageFrameType.FOUR_TWO));
        mCollageFrameList.add(new CollageFrameModel("Eight", R.drawable.collage_4_3, R.drawable.collage4_3, CollageFrameType.FOUR_THREE));
        mCollageFrameList.add(new CollageFrameModel("Nine", R.drawable.collage_4_4, R.drawable.collage4_4, CollageFrameType.FOUR_FOUR));
        mCollageFrameList.add(new CollageFrameModel("Ten", R.drawable.collage_6_1, R.drawable.collage6_1, CollageFrameType.SIX_ONE));
*/


    }




    @NonNull
    @Override
    public CollageLayoutsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {


         View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_collage_filter, viewGroup, false);
       // View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.collage_frame_itemview, viewGroup, false);
        return new CollageLayoutsAdapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull CollageLayoutsAdapter.ViewHolder viewHolder, final int position) {

        CollageLayoutsModel item = mCollageFrameList.get(position);

        viewHolder.mCollageFrameItemImage.setImageResource(item.collageLayoutIdSmall);
        viewHolder.mCollageFrameItemText.setText(item.collageName);



    }

    @Override
    public int getItemCount() {

        return(null!=mCollageFrameList? mCollageFrameList.size():0);
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public  View mItemView;

        public LinearLayout mCollageFrameItemLLayout;
        public ImageView mCollageFrameItemImage;
        public TextView mCollageFrameItemText;

        ViewHolder(View itemView) {
            super(itemView);

            this.mItemView = itemView;

            mCollageFrameItemImage =(ImageView)itemView.findViewById(R.id.idCollageItemViewImage);
            mCollageFrameItemText = (TextView)itemView.findViewById(R.id.idCollageItemViewText);

//            mCollageFrameItemLLayout = (LinearLayout) itemView.findViewById(R.id.idCollageFrameItemLLayout);
//            mCollageFrameItemImage =(ImageView)itemView.findViewById(R.id.idCollageFrameItemImage);
//            mCollageFrameItemText = (TextView)itemView.findViewById(R.id.idCollageFrameItemText);



             itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnCollageFrameSelected.onCollageLayoutSelected(mCollageFrameList.get(getLayoutPosition()).getCollageLayoutIdBig(), mCollageFrameList.get(getLayoutPosition()).getCollageLayoutType());

                }
            });


        }
    }

}

