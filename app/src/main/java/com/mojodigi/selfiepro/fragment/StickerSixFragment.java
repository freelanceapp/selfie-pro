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

public class StickerSixFragment extends BottomSheetDialogFragment {

    public StickerSixFragment() {
        // Required empty public constructor
    }

    private StickerSixFragment.StickerSixListener mStickerSixListener;

    public void setStickerSixListener(StickerSixFragment.StickerSixListener stickerSixListener) {
        mStickerSixListener = stickerSixListener;
    }

    public interface StickerSixListener {
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
        StickerSixFragment.StickerAdapter stickerAdapter = new StickerSixFragment.StickerAdapter();
        rvEmoji.setAdapter(stickerAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public class StickerAdapter extends RecyclerView.Adapter<StickerSixFragment.StickerAdapter.ViewHolder> {


        int[] stickerSixList = new int[]{R.drawable.male_1, R.drawable.male_2, R.drawable.male_3, R.drawable.male_4,
                R.drawable.male_5, R.drawable.male_6, R.drawable.male_7, R.drawable.male_8,
                R.drawable.male_9, R.drawable.male_10 , R.drawable.male_11, R.drawable.male_12,
                R.drawable.male_13, R.drawable.male_14 , R.drawable.male_15, R.drawable.male_16,
                R.drawable.male_17, R.drawable.male_18};

        @NonNull
        @Override
        public StickerSixFragment.StickerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sticker, parent, false);
            return new StickerSixFragment.StickerAdapter.ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(StickerSixFragment.StickerAdapter.ViewHolder holder, int position) {
            holder.imgSticker.setImageResource(stickerSixList[position]);
        }

        @Override
        public int getItemCount() {
            return stickerSixList.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgSticker;

            ViewHolder(View itemView) {
                super(itemView);
                imgSticker = itemView.findViewById(R.id.imgSticker);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mStickerSixListener != null) {
                            mStickerSixListener.onStickerOneClick(
                                    BitmapFactory.decodeResource(getResources(),
                                            stickerSixList[getLayoutPosition()]));
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