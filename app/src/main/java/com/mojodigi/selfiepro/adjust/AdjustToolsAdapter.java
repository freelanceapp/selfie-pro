package com.mojodigi.selfiepro.adjust;

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
import com.mojodigi.selfiepro.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class AdjustToolsAdapter  extends RecyclerView.Adapter<AdjustToolsAdapter.ViewHolder> {

    private List<AdjustToolsModel> mAdjustToolsList  ;

    private OnAdjustTollsSelected mOnAdjustTollsSelected;

    private static int mPosition = -1;

    public AdjustToolsAdapter(OnAdjustTollsSelected onGalleryAdjustTollsSelected) {

        mOnAdjustTollsSelected = onGalleryAdjustTollsSelected;
        mAdjustToolsList = new ArrayList<>();

        //mAdjustToolsList.add(new AdjustToolsModel("Reset", R.drawable.svg_reset, AdjustToolsType.RESET));
        mAdjustToolsList.add(new AdjustToolsModel("Brightness", R.drawable.svg_brightness, AdjustToolsType.BRIGHTNESS));
        mAdjustToolsList.add(new AdjustToolsModel("Contrast", R.drawable.svg_contrast, AdjustToolsType.CONTRAST));
        mAdjustToolsList.add(new AdjustToolsModel("Saturation", R.drawable.svg_situration, AdjustToolsType.SATURATION));
        //mAdjustToolsList.add(new GalleryAdjustToolsModel("Sharpness", R.drawable.svg_sharpness, GalleryAdjustToolsType.SHARPNESS));
        mAdjustToolsList.add(new AdjustToolsModel("Vignette", R.drawable.svg_vignette, AdjustToolsType.VIGNETTE));
        mAdjustToolsList.add(new AdjustToolsModel("Shadow", R.drawable.svg_shadow, AdjustToolsType.SHADOW));
        mAdjustToolsList.add(new AdjustToolsModel("Highlight", R.drawable.svg_highlight, AdjustToolsType.HIGH_LIGHT));
        mAdjustToolsList.add(new AdjustToolsModel("Temp", R.drawable.svg_temp, AdjustToolsType.TEMP));
        mAdjustToolsList.add(new AdjustToolsModel("Tint", R.drawable.svg_tint, AdjustToolsType.TINT));
        mAdjustToolsList.add(new AdjustToolsModel("Noise", R.drawable.svg_denoise, AdjustToolsType.DENOISE));
        //mAdjustToolsList.add(new GalleryAdjustToolsModel("Curve", R.drawable.svg_curves, GalleryAdjustToolsType.CURVE));
        mAdjustToolsList.add(new AdjustToolsModel("Color Balance", R.drawable.svg_color_balance, AdjustToolsType.COLOR_BALANCE));

    }


    //row_gallery_adjusttools

    @NonNull
    @Override
    public AdjustToolsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_adjusttools, parent, false);

        return new AdjustToolsAdapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final AdjustToolsAdapter.ViewHolder holder, final int position) {
        AdjustToolsModel item = mAdjustToolsList.get(position);
        holder.adjustItemViewLayout.setBackgroundColor(Color.parseColor("#f7d775"));
        holder.adjustToolsText.setText(item.toolName);
        holder.adjustToolsImgView.setImageResource(item.toolIcon);

        if(Constants.isAdjustSelected){
            mPosition=0;
            Constants.isAdjustSelected=false;
        }

        if(mPosition >=0 && mPosition ==  position){
            holder.adjustItemViewLayout.setBackgroundColor(Color.parseColor("#fcf5df"));
        }
        else
        {
            holder.adjustItemViewLayout.setBackgroundColor(Color.parseColor("#f7d775"));
        }
    }


    @Override
    public int getItemCount() {
        return mAdjustToolsList.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout adjustItemViewLayout;
        ImageView adjustToolsImgView;
        TextView adjustToolsText;

        ViewHolder(View itemView) {
            super(itemView);

            adjustItemViewLayout = itemView.findViewById(R.id.adjustItemViewLayout);
            adjustToolsImgView = itemView.findViewById(R.id.adjustToolsImgView);
            adjustToolsText = itemView.findViewById(R.id.adjustToolsText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPosition = getAdapterPosition();
                    notifyDataSetChanged();
                    // Log.e( "mPosition "+mPosition , "position "+getAdapterPosition() );
                    mOnAdjustTollsSelected.onAdjustTollsSelected(mAdjustToolsList.get(getLayoutPosition()).galleryAdjustToolsType);

                }
            });
        }
    }
}

