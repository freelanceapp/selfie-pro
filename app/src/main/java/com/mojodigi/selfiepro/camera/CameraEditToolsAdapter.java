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

public class CameraEditToolsAdapter  extends RecyclerView.Adapter<CameraEditToolsAdapter.ViewHolder> {

    private List<CameraEditToolsModel> mCameraEditToolsList  ;

    private OnCameraEditTollsSelected mOnCameraEditTollsSelected;

    private static int mPosition = -1;

    public CameraEditToolsAdapter(OnCameraEditTollsSelected onCollageEditTollsSelected) {

        mOnCameraEditTollsSelected = onCollageEditTollsSelected;
        mCameraEditToolsList = new ArrayList<>();

        mCameraEditToolsList.add(new CameraEditToolsModel("Camera", R.drawable.svg_camera_tool, CameraEditToolsType.CAMERA));
        mCameraEditToolsList.add(new CameraEditToolsModel("Tools", R.drawable.svg_tools, CameraEditToolsType.TOOLS));
        mCameraEditToolsList.add(new CameraEditToolsModel("Effects", R.drawable.svg_effects, CameraEditToolsType.EFFECTS));
        mCameraEditToolsList.add(new CameraEditToolsModel("Adjust", R.drawable.svg_adjust, CameraEditToolsType.ADJUST));
        mCameraEditToolsList.add(new CameraEditToolsModel("Edit", R.drawable.svg_send_edit, CameraEditToolsType.EDIT));

    }




    @NonNull
    @Override
    public CameraEditToolsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_camera_edittools, parent, false);

        return new CameraEditToolsAdapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final CameraEditToolsAdapter.ViewHolder holder, final int position) {
        CameraEditToolsModel item = mCameraEditToolsList.get(position);

        holder.cameraEditToolsText.setText(item.cameraToolName);
        holder.cameraEditToolsImgView.setImageResource(item.cameraToolIcon);

        if(mPosition >=0 && mPosition ==  position){
            holder.cameraEditItemViewLayout.setBackgroundColor(Color.parseColor("#fcf5df"));
        }
        else
        {
            //holder.collageEditItemViewLayout.setBackgroundColor(Color.parseColor("#000000"));
            holder.cameraEditItemViewLayout.setBackgroundColor(Color.parseColor("#f7d775"));
        }

    }

    @Override
    public int getItemCount() {
        return mCameraEditToolsList.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout cameraEditItemViewLayout;
        ImageView cameraEditToolsImgView;
        TextView cameraEditToolsText;

        ViewHolder(View itemView) {
            super(itemView);

            cameraEditItemViewLayout = itemView.findViewById(R.id.cameraEditItemViewLayout);
            cameraEditToolsImgView = itemView.findViewById(R.id.cameraEditToolsImgView);
            cameraEditToolsText = itemView.findViewById(R.id.cameraEditToolsText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mPosition = getAdapterPosition();

                    notifyDataSetChanged();

                    // Log.e( "mPosition "+mPosition , "position "+getAdapterPosition() );

                    mOnCameraEditTollsSelected.onCameraEditTollsSelected(mCameraEditToolsList.get(getLayoutPosition()).cameraEditToolsType);



                }
            });
        }
    }
}

