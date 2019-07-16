package com.mojodigi.selfiepro.tools;

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
import com.mojodigi.selfiepro.interfaces.OnItemSelected;
import com.mojodigi.selfiepro.model.ToolModel;

import java.util.ArrayList;
import java.util.List;


public class EditingToolsAdapter extends RecyclerView.Adapter<EditingToolsAdapter.ViewHolder> {

    private List<ToolModel> mToolList  ;
    private OnItemSelected mOnItemSelected;
    private static int mPosition = -1;


    public EditingToolsAdapter(OnItemSelected onItemSelected) {

        mOnItemSelected = onItemSelected;
        mToolList = new ArrayList<>();

         //mToolList.add(new ToolModel("Camera", R.drawable.svg_camera, ToolType.CAMERA));
        // mToolList.add(new ToolModel("Gallery", R.drawable.svg_gallery, ToolType.GALLERY));
        // mToolList.add(new ToolModel("Effects", R.drawable.svg_effects, ToolType.EFFECTS));
         //mToolList.add(new ToolModel("Frame", R.drawable.svg_frame, ToolType.FRAME));
        //mToolList.add(new ToolModel("Background", R.drawable.svg_background, ToolType.BACKGROUND));
         //mToolList.add(new ToolModel("Crop", R.drawable.svg_crop, ToolType.CROP));

        mToolList.add(new ToolModel("Text", R.drawable.svg_text, ToolType.TEXT));
        mToolList.add(new ToolModel("Stickers", R.drawable.svg_stickers, ToolType.STICKERS));
        mToolList.add(new ToolModel("Brush", R.drawable.ic_brush, ToolType.BRUSH));
        mToolList.add(new ToolModel("Eraser", R.drawable.ic_eraser, ToolType.ERASER));
        mToolList.add(new ToolModel("Emoji", R.drawable.ic_insert_emoticon, ToolType.EMOJI));

    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_editing_tools, parent, false);



        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToolModel item = mToolList.get(position);
        holder.imageEditText.setText(item.mToolName);
        holder.imageEditIcon.setImageResource(item.mToolIcon);

        if(mPosition >=0 && mPosition ==  position){
            holder.imageEditItemViewLayout.setBackgroundColor(Color.parseColor("#fcf5df"));
        }
        else
        {
            holder.imageEditItemViewLayout.setBackgroundColor(Color.parseColor("#f7d775"));
        }
    }

    @Override
    public int getItemCount() {
        return mToolList.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout imageEditItemViewLayout;
        ImageView imageEditIcon;
        TextView imageEditText;

        ViewHolder(View itemView) {
            super(itemView);

            imageEditItemViewLayout = itemView.findViewById(R.id.imageEditItemViewLayout);
            imageEditIcon = itemView.findViewById(R.id.imageEditIcon);
            imageEditText = itemView.findViewById(R.id.imageEditText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mPosition = getAdapterPosition();

                    mOnItemSelected.onToolSelected(mToolList.get(getLayoutPosition()).mToolType);

                    notifyDataSetChanged();

                }
            });
        }
    }


}

