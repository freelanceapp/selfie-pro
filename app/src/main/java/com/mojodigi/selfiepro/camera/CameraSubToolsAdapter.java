package com.mojodigi.selfiepro.camera;

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

public class CameraSubToolsAdapter extends RecyclerView.Adapter<CameraSubToolsAdapter.ViewHolder> {

    private List<CameraSubToolsModel> cameraSubToolsModelList  ;
    private OnCameraSubTollsSelected mOnCameraSubTollsSelected;
    private static int mPosition = -1;

    public CameraSubToolsAdapter(OnCameraSubTollsSelected onCameraSubTollsSelected) {

        mOnCameraSubTollsSelected = onCameraSubTollsSelected;
        cameraSubToolsModelList = new ArrayList<>();

        cameraSubToolsModelList.add(new CameraSubToolsModel("Crop", R.drawable.svg_crop, CameraSubToolsType.CAMERA_CROP));
        cameraSubToolsModelList.add(new CameraSubToolsModel("Rt Left", R.drawable.svg_rotate_left, CameraSubToolsType.CAMERA_RT_LEFT));
        cameraSubToolsModelList.add(new CameraSubToolsModel("Rt Right", R.drawable.svg_rotate_right, CameraSubToolsType.CAMERA_RT_RIGHT));
        cameraSubToolsModelList.add(new CameraSubToolsModel("Rt None", R.drawable.svg_rt_none, CameraSubToolsType.CAMERA_RT_NONE));
        cameraSubToolsModelList.add(new CameraSubToolsModel("Flip", R.drawable.svg_flip, CameraSubToolsType.CAMERA_FLIP));

    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_subtools_recycler, parent, false);

        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        CameraSubToolsModel item = cameraSubToolsModelList.get(position);

        holder.toolsRecyclerText.setText(item.cameraSubToolName);
        holder.toolsRecyclerImgView.setImageResource(item.cameraSubToolIcon);

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
        return cameraSubToolsModelList.size();
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

                    mOnCameraSubTollsSelected.onCameraSubTollsSelected(cameraSubToolsModelList.get(getLayoutPosition()).cameraSubToolsType);

                }
            });
        }
    }
}

