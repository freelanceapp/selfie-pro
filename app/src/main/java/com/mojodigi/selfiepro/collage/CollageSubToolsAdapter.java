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


public class CollageSubToolsAdapter extends RecyclerView.Adapter<CollageSubToolsAdapter.ViewHolder> {

    private List<CollageSubToolsModel> toolsRecyclerModelList  ;

    private OnCollageSubToolsSelected mOnCollageSubToolsSelected;

    private static int mPosition = -1;

    public CollageSubToolsAdapter(OnCollageSubToolsSelected onCollageEditTollsSelected) {

        mOnCollageSubToolsSelected = onCollageEditTollsSelected;
        toolsRecyclerModelList = new ArrayList<>();

        toolsRecyclerModelList.add(new CollageSubToolsModel("Crop", R.drawable.svg_crop, CollageSubToolsType.CROP));
        toolsRecyclerModelList.add(new CollageSubToolsModel("Rt Left", R.drawable.svg_rotate_left, CollageSubToolsType.RT_LEFT));
        toolsRecyclerModelList.add(new CollageSubToolsModel("Rt Right", R.drawable.svg_rotate_right, CollageSubToolsType.RT_RIGHT));
        toolsRecyclerModelList.add(new CollageSubToolsModel("Rt None", R.drawable.svg_rt_none, CollageSubToolsType.RT_NONE));
        toolsRecyclerModelList.add(new CollageSubToolsModel("Flip", R.drawable.svg_flip, CollageSubToolsType.FLIP));

    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_subtools_recycler, parent, false);

        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        CollageSubToolsModel item = toolsRecyclerModelList.get(position);

        holder.toolsRecyclerText.setText(item.toolsRecyclerName);
        holder.toolsRecyclerImgView.setImageResource(item.toolsRecyclerIcon);

        if(mPosition >=0 && mPosition ==  position){
            holder.toolsRecyclerItemLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        else
        {
            holder.toolsRecyclerItemLayout.setBackgroundColor(Color.parseColor("#fcf5df"));
        }

    }

    @Override
    public int getItemCount() {
        return toolsRecyclerModelList.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout toolsRecyclerItemLayout;
        ImageView toolsRecyclerImgView;
        TextView toolsRecyclerText;

        ViewHolder(View itemView) {
            super(itemView);

            toolsRecyclerItemLayout = itemView.findViewById(R.id.toolsRecyclerItemLayout);

            toolsRecyclerImgView = itemView.findViewById(R.id.toolsRecyclerImgView);
            toolsRecyclerText = itemView.findViewById(R.id.toolsRecyclerText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPosition = getAdapterPosition();
                    notifyDataSetChanged();
                    // Log.e( "mPosition "+mPosition , "position "+getAdapterPosition() );
                    mOnCollageSubToolsSelected.onCollageSubToolsSelected(toolsRecyclerModelList.get(getLayoutPosition()).toolsRecyclerType);

                }
            });
        }
    }
}

