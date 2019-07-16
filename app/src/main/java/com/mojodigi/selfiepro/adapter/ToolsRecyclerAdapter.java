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
import com.mojodigi.selfiepro.enums.ToolsRecyclerType;
import com.mojodigi.selfiepro.interfaces.OnToolsRecyclerSelected;
import com.mojodigi.selfiepro.model.ToolsRecyclerModel;
import java.util.ArrayList;
import java.util.List;


public class ToolsRecyclerAdapter extends RecyclerView.Adapter<ToolsRecyclerAdapter.ViewHolder> {

    private List<ToolsRecyclerModel> toolsRecyclerModelList  ;

    private OnToolsRecyclerSelected mOnToolsRecyclerSelected;

    private static int mPosition = -1;

    public ToolsRecyclerAdapter(OnToolsRecyclerSelected onCollageEditTollsSelected) {

        mOnToolsRecyclerSelected = onCollageEditTollsSelected;
        toolsRecyclerModelList = new ArrayList<>();

       toolsRecyclerModelList.add(new ToolsRecyclerModel("Crop", R.drawable.svg_crop, ToolsRecyclerType.CROP));
       //toolsRecyclerModelList.add(new ToolsRecyclerModel("Rotation", R.drawable.svg_rotate_right, ToolsRecyclerType.ROTATION));
        toolsRecyclerModelList.add(new ToolsRecyclerModel("Left", R.drawable.svg_rotate_left, ToolsRecyclerType.RT_LEFT));
       toolsRecyclerModelList.add(new ToolsRecyclerModel("Right", R.drawable.svg_rotate_right, ToolsRecyclerType.RT_RIGHT));
       toolsRecyclerModelList.add(new ToolsRecyclerModel("Flip", R.drawable.svg_flip, ToolsRecyclerType.FLIP));

    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tools_recycler, parent, false);

        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        ToolsRecyclerModel item = toolsRecyclerModelList.get(position);

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
                    mOnToolsRecyclerSelected.onToolsRecyclerSelected(toolsRecyclerModelList.get(getLayoutPosition()).toolsRecyclerType);

                }
            });
        }
    }
}

