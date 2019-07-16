package com.mojodigi.selfiepro.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mojodigi.selfiepro.R;

public class StickerNineFragment extends BottomSheetDialogFragment {

    public StickerNineFragment() {
        // Required empty public constructor
    }

    private StickerNineFragment.StickerNineListener mStickerNineListener;

    public void setStickerNineListener(StickerNineFragment.StickerNineListener stickerNineListener) {
        mStickerNineListener = stickerNineListener;
    }

    public interface StickerNineListener {
        void onStickerOneClick(Bitmap bitmap);
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback =
            new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        dismiss();
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            };


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sticker_emoji_dialog, null);
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        RecyclerView rvEmoji = contentView.findViewById(R.id.rvEmoji);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        rvEmoji.setLayoutManager(gridLayoutManager);
        StickerNineFragment.StickerAdapter stickerAdapter = new StickerNineFragment.StickerAdapter();
        rvEmoji.setAdapter(stickerAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public class StickerAdapter extends RecyclerView.Adapter<StickerNineFragment.StickerAdapter.ViewHolder> {

        int[] stickerNineList = new int[]{ R.drawable.shin_chan_1, R.drawable.shin_chan_2 ,
                R.drawable.shin_chan_3, R.drawable.shin_chan_4 , R.drawable.shin_chan_5, R.drawable.shin_chan_6 ,
                R.drawable.shin_chan_7, R.drawable.shin_chan_8 , R.drawable.shin_chan_9, R.drawable.shin_chan_10 ,
                R.drawable.shin_chan_11, R.drawable.shin_chan_12 , R.drawable.shin_chan_13, R.drawable.shin_chan_14 ,
                R.drawable.shin_chan_15, R.drawable.shin_chan_16 , R.drawable.shin_chan_17, R.drawable.shin_chan_18 ,
                R.drawable.shin_chan_19, R.drawable.shin_chan_20 , R.drawable.shin_chan_21, R.drawable.shin_chan_22 ,
                R.drawable.shin_chan_23, R.drawable.shin_chan_24 , R.drawable.shin_chan_25, R.drawable.shin_chan_26 ,
                R.drawable.shin_chan_27, R.drawable.shin_chan_28 , R.drawable.shin_chan_29, R.drawable.shin_chan_30 ,
                R.drawable.shin_chan_31, R.drawable.shin_chan_32 , R.drawable.shin_chan_33, R.drawable.shin_chan_34 ,
                R.drawable.shin_chan_35, R.drawable.shin_chan_36 , R.drawable.shin_chan_37, R.drawable.shin_chan_38 };

        @NonNull
        @Override
        public StickerNineFragment.StickerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sticker, parent, false);
            return new StickerNineFragment.StickerAdapter.ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(StickerNineFragment.StickerAdapter.ViewHolder holder, int position) {
            holder.imgSticker.setImageResource(stickerNineList[position]);
        }

        @Override
        public int getItemCount() {
            return stickerNineList.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgSticker;

            ViewHolder(View itemView) {
                super(itemView);
                imgSticker = itemView.findViewById(R.id.imgSticker);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mStickerNineListener != null) {
                            mStickerNineListener.onStickerOneClick(
                                    BitmapFactory.decodeResource(getResources(),
                                            stickerNineList[getLayoutPosition()]));
                        }
                        dismiss();
                    }
                });
            }
        }
    }

    private String convertEmoji(String emoji) {
        String returnedEmoji = "";
        try {
            int convertEmojiToInt = Integer.parseInt(emoji.substring(2), 16);
            returnedEmoji = getEmojiByUnicode(convertEmojiToInt);
        } catch (NumberFormatException e) {
            returnedEmoji = "";
        }
        return returnedEmoji;
    }

    private String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

}