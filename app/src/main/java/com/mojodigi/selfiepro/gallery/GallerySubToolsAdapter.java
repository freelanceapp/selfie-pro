package com.mojodigi.selfiepro.gallery;

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

public class GallerySubToolsAdapter extends RecyclerView.Adapter<GallerySubToolsAdapter.ViewHolder> {

    private List<GallerySubToolsModel> gallerySubToolsModelList  ;
    private OnGallerySubToolsSelected mOnGallerySubToolsSelected;
    private static int mPosition = -1;

    public GallerySubToolsAdapter(OnGallerySubToolsSelected onGallerySubTollsSelected) {

        mOnGallerySubToolsSelected = onGallerySubTollsSelected;
        gallerySubToolsModelList = new ArrayList<>();

        gallerySubToolsModelList.add(new GallerySubToolsModel("Crop", R.drawable.svg_crop, GallerySubToolsType.GALLERY_CROP));
        gallerySubToolsModelList.add(new GallerySubToolsModel("Rt Left", R.drawable.svg_rotate_left, GallerySubToolsType.GALLERY_RT_LEFT));
        gallerySubToolsModelList.add(new GallerySubToolsModel("Rt Right", R.drawable.svg_rotate_right, GallerySubToolsType.GALLERY_RT_RIGHT));
        gallerySubToolsModelList.add(new GallerySubToolsModel("Rt None", R.drawable.svg_rt_none, GallerySubToolsType.GALLERY_RT_NONE));
        gallerySubToolsModelList.add(new GallerySubToolsModel("Flip", R.drawable.svg_flip, GallerySubToolsType.GALLERY_FLIP));

    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_subtools_recycler, parent, false);

        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        GallerySubToolsModel item = gallerySubToolsModelList.get(position);

        holder.toolsRecyclerText.setText(item.gallerySubToolName);
        holder.toolsRecyclerImgView.setImageResource(item.gallerySubToolIcon);

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
        return gallerySubToolsModelList.size();
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
                    mOnGallerySubToolsSelected.onGallerySubToolsSelected(gallerySubToolsModelList.get(getLayoutPosition()).gallerySubToolsType);
                }
            });
        }
    }
}

