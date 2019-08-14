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

public class StickerThreeFragment extends BottomSheetDialogFragment {

    public StickerThreeFragment() {
        // Required empty public constructor
    }

    private StickerThreeFragment.StickerThreeListener mStickerThreeListener;

    public void setStickerThreeListener(StickerThreeFragment.StickerThreeListener stickerThreeListener) {
        mStickerThreeListener = stickerThreeListener;
    }

    public interface StickerThreeListener {
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
        StickerThreeFragment.StickerAdapter stickerAdapter = new StickerThreeFragment.StickerAdapter();
        rvEmoji.setAdapter(stickerAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public class StickerAdapter extends RecyclerView.Adapter<StickerThreeFragment.StickerAdapter.ViewHolder> {

        int[] stickerThreeList = new int[]{R.drawable.loves_1, R.drawable.loves_2, R.drawable.loves_3, R.drawable.loves_4,
                R.drawable.loves_5, R.drawable.loves_6, R.drawable.loves_7, R.drawable.loves_8, R.drawable.loves_9,
                R.drawable.loves_10, R.drawable.loves_11, R.drawable.loves_12, R.drawable.loves_13, R.drawable.loves_14,
                R.drawable.loves_15, R.drawable.loves_16, R.drawable.loves_17, R.drawable.loves_18, R.drawable.loves_19, R.drawable.loves_20,
                R.drawable.loves_21, R.drawable.loves_22, R.drawable.loves_23, R.drawable.loves_24, R.drawable.loves_25,
                R.drawable.loves_26, R.drawable.loves_27};

        @NonNull
        @Override
        public StickerThreeFragment.StickerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sticker, parent, false);
            return new StickerThreeFragment.StickerAdapter.ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(StickerThreeFragment.StickerAdapter.ViewHolder holder, int position) {
            holder.imgSticker.setImageResource(stickerThreeList[position]);
        }

        @Override
        public int getItemCount() {
            return stickerThreeList.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgSticker;

            ViewHolder(View itemView) {
                super(itemView);
                imgSticker = itemView.findViewById(R.id.imgSticker);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mStickerThreeListener != null) {
                            mStickerThreeListener.onStickerOneClick(
                                    BitmapFactory.decodeResource(getResources(),
                                            stickerThreeList[getLayoutPosition()]));
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