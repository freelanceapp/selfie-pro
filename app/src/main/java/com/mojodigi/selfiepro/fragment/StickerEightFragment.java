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

public class StickerEightFragment extends BottomSheetDialogFragment {

    public StickerEightFragment() {
        // Required empty public constructor
    }

    private StickerEightFragment.StickerEightListener mStickerEightListener;

    public void setStickerEightListener(StickerEightFragment.StickerEightListener stickerEightListener) {
        mStickerEightListener = stickerEightListener;
    }

    public interface StickerEightListener {
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
        StickerEightFragment.StickerAdapter stickerAdapter = new StickerEightFragment.StickerAdapter();
        rvEmoji.setAdapter(stickerAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public class StickerAdapter extends RecyclerView.Adapter<StickerEightFragment.StickerAdapter.ViewHolder> {

        int[] stickerEightList = new int[]{ R.drawable.doraemon_1, R.drawable.doraemon_2 ,
                R.drawable.doraemon_3, R.drawable.doraemon_4 , R.drawable.doraemon_5, R.drawable.doraemon_6 ,
                R.drawable.doraemon_7, R.drawable.doraemon_8 , R.drawable.doraemon_9, R.drawable.doraemon_10 ,
                R.drawable.doraemon_11, R.drawable.doraemon_12 , R.drawable.doraemon_13, R.drawable.doraemon_14 ,
                R.drawable.doraemon_15, R.drawable.doraemon_16 , R.drawable.doraemon_17, R.drawable.doraemon_18 ,
                R.drawable.doraemon_19, R.drawable.doraemon_20 , R.drawable.doraemon_21, R.drawable.doraemon_22 ,
                R.drawable.doraemon_23, R.drawable.doraemon_24 , R.drawable.doraemon_25, R.drawable.doraemon_26 ,
                R.drawable.doraemon_27, R.drawable.doraemon_28 , R.drawable.doraemon_29, R.drawable.doraemon_30 ,
                R.drawable.doraemon_31, R.drawable.doraemon_32 , R.drawable.doraemon_33, R.drawable.doraemon_34 ,
                R.drawable.doraemon_35, R.drawable.doraemon_36 , R.drawable.doraemon_37, R.drawable.doraemon_38 ,
                R.drawable.doraemon_39, R.drawable.doraemon_40 , R.drawable.doraemon_41, R.drawable.doraemon_42};

        @NonNull
        @Override
        public StickerEightFragment.StickerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sticker, parent, false);
            return new StickerEightFragment.StickerAdapter.ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(StickerEightFragment.StickerAdapter.ViewHolder holder, int position) {
            holder.imgSticker.setImageResource(stickerEightList[position]);
        }

        @Override
        public int getItemCount() {
            return stickerEightList.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgSticker;

            ViewHolder(View itemView) {
                super(itemView);
                imgSticker = itemView.findViewById(R.id.imgSticker);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mStickerEightListener != null) {
                            mStickerEightListener.onStickerOneClick(
                                    BitmapFactory.decodeResource(getResources(),
                                            stickerEightList[getLayoutPosition()]));
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