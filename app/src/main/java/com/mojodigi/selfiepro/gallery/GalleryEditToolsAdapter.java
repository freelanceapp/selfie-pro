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

public class GalleryEditToolsAdapter  extends RecyclerView.Adapter<GalleryEditToolsAdapter.ViewHolder> {



    private List<GalleryEditToolsModel> mGalleryEditToolsList  ;

    private OnGalleryEditTollsSelected mOnGalleryEditTollsSelected;

    private static int mPosition = -1;

    public GalleryEditToolsAdapter(OnGalleryEditTollsSelected onCollageEditTollsSelected) {

        mOnGalleryEditTollsSelected = onCollageEditTollsSelected;
        mGalleryEditToolsList = new ArrayList<>();

        mGalleryEditToolsList.add(new GalleryEditToolsModel("Gallery", R.drawable.svg_gallery, GalleryEditToolsType.GALLERY));
        mGalleryEditToolsList.add(new GalleryEditToolsModel("Tools", R.drawable.svg_tools, GalleryEditToolsType.TOOLS));
        //mGalleryEditToolsList.add(new GalleryEditToolsModel("Frame", R.drawable.svg_frame, GalleryEditToolsType.FRAME));
        mGalleryEditToolsList.add(new GalleryEditToolsModel("Effects", R.drawable.svg_effects, GalleryEditToolsType.EFFECTS));
        mGalleryEditToolsList.add(new GalleryEditToolsModel("Adjust", R.drawable.svg_adjust, GalleryEditToolsType.ADJUST));
        mGalleryEditToolsList.add(new GalleryEditToolsModel("Edit", R.drawable.svg_send_edit, GalleryEditToolsType.EDIT));

    }




    @NonNull
    @Override
    public GalleryEditToolsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_gallery_edittools, parent, false);

        return new GalleryEditToolsAdapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final GalleryEditToolsAdapter.ViewHolder holder, final int position) {
        GalleryEditToolsModel item = mGalleryEditToolsList.get(position);

        holder.galleryEditToolsText.setText(item.toolName);
        holder.galleryEditToolsImgView.setImageResource(item.toolIcon);

        if(mPosition >=0 && mPosition ==  position){
            holder.galleryEditItemViewLayout.setBackgroundColor(Color.parseColor("#fcf5df"));
        }
        else
        {
            //holder.collageEditItemViewLayout.setBackgroundColor(Color.parseColor("#000000"));
            holder.galleryEditItemViewLayout.setBackgroundColor(Color.parseColor("#f7d775"));
        }

    }

    @Override
    public int getItemCount() {
        return mGalleryEditToolsList.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout galleryEditItemViewLayout;
        ImageView galleryEditToolsImgView;
        TextView galleryEditToolsText;

        ViewHolder(View itemView) {
            super(itemView);

            galleryEditItemViewLayout = itemView.findViewById(R.id.galleryEditItemViewLayout);
            galleryEditToolsImgView = itemView.findViewById(R.id.galleryEditToolsImgView);
            galleryEditToolsText = itemView.findViewById(R.id.galleryEditToolsText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mPosition = getAdapterPosition();

                    notifyDataSetChanged();

                    // Log.e( "mPosition "+mPosition , "position "+getAdapterPosition() );

                    mOnGalleryEditTollsSelected.onGalleryEditTollsSelected(mGalleryEditToolsList.get(getLayoutPosition()).galleryEditToolsType);



                }
            });
        }
    }
}

