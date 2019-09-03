package com.mojodigi.selfiepro.collage;

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

import java.util.ArrayList;
import java.util.List;

public class CollageEditToolsAdapter extends RecyclerView.Adapter<CollageEditToolsAdapter.ViewHolder> {

    private List<CollageEditToolsModel> mCollageEditToolsList  ;

    private OnCollageEditTollsSelected mOnCollageEditTollsSelected;

    private static int mPosition = -1;

    public CollageEditToolsAdapter(OnCollageEditTollsSelected onCollageEditTollsSelected) {

        mOnCollageEditTollsSelected = onCollageEditTollsSelected;
        mCollageEditToolsList = new ArrayList<>();

        mCollageEditToolsList.add(new CollageEditToolsModel("Collage", R.drawable.svg_collage, CollageEditToolsType.COLLAGE));
        mCollageEditToolsList.add(new CollageEditToolsModel("Tools", R.drawable.svg_tools, CollageEditToolsType.TOOLS));
        mCollageEditToolsList.add(new CollageEditToolsModel("Effects", R.drawable.svg_effects, CollageEditToolsType.EFFECTS));
        mCollageEditToolsList.add(new CollageEditToolsModel("Adjust", R.drawable.svg_adjust, CollageEditToolsType.ADJUST));
        mCollageEditToolsList.add(new CollageEditToolsModel("Edit", R.drawable.svg_send_edit, CollageEditToolsType.EDIT));

    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_collage_edittools, parent, false);

        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        CollageEditToolsModel item = mCollageEditToolsList.get(position);

        holder.mCollageEditToolsText.setText(item.mToolName);
        holder.mCollageEditToolsImgView.setImageResource(item.mToolIcon);

        if(mPosition >=0 && mPosition ==  position){
            holder.collageEditItemViewLayout.setBackgroundColor(Color.parseColor("#fcf5df"));
        }
        else
        {
            holder.collageEditItemViewLayout.setBackgroundColor(Color.parseColor("#f7d775"));
        }
    }

    @Override
    public int getItemCount() {
        return mCollageEditToolsList.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout collageEditItemViewLayout;
        ImageView mCollageEditToolsImgView;
        TextView mCollageEditToolsText;

        ViewHolder(View itemView) {
        super(itemView);

            collageEditItemViewLayout = itemView.findViewById(R.id.collageEditItemViewLayout);
            mCollageEditToolsImgView = itemView.findViewById(R.id.idCollageEditToolsImgView);
            mCollageEditToolsText = itemView.findViewById(R.id.idCollageEditToolsText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPosition = getAdapterPosition();
                    notifyDataSetChanged();
                   // Log.e( "mPosition "+mPosition , "position "+getAdapterPosition() );
                    mOnCollageEditTollsSelected.onCollageEditTollsSelected(mCollageEditToolsList.get(getLayoutPosition()).mCollageEditToolsType);
                }
            });
        }
    }
}

