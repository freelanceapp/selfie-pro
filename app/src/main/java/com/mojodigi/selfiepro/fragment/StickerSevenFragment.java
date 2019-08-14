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

public class StickerSevenFragment extends BottomSheetDialogFragment {

    public StickerSevenFragment() {
        // Required empty public constructor
    }

    private StickerSevenFragment.StickerSevenListener mStickerSevenListener;

    public void setStickerSevenListener(StickerSevenFragment.StickerSevenListener stickerSevenListener) {
        mStickerSevenListener = stickerSevenListener;
    }

    public interface StickerSevenListener {
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
        StickerSevenFragment.StickerAdapter stickerAdapter = new StickerSevenFragment.StickerAdapter();
        rvEmoji.setAdapter(stickerAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public class StickerAdapter extends RecyclerView.Adapter<StickerSevenFragment.StickerAdapter.ViewHolder> {

        int[] stickerSevenList = new int[]{ R.drawable.love_1};

        @NonNull
        @Override
        public StickerSevenFragment.StickerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sticker, parent, false);
            return new StickerSevenFragment.StickerAdapter.ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(StickerSevenFragment.StickerAdapter.ViewHolder holder, int position) {
            holder.imgSticker.setImageResource(stickerSevenList[position]);
        }

        @Override
        public int getItemCount() {
            return stickerSevenList.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgSticker;

            ViewHolder(View itemView) {
                super(itemView);
                imgSticker = itemView.findViewById(R.id.imgSticker);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mStickerSevenListener != null) {
                            mStickerSevenListener.onStickerOneClick(
                                    BitmapFactory.decodeResource(getResources(),
                                            stickerSevenList[getLayoutPosition()]));
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