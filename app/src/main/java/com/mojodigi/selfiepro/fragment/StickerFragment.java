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

public class StickerFragment extends BottomSheetDialogFragment {

    public StickerFragment() {
        // Required empty public constructor
    }

    private StickerListener mStickerListener;

    public void setStickerListener(StickerListener stickerListener) {
        mStickerListener = stickerListener;
    }

    public interface StickerListener {
        void onStickerClick(Bitmap bitmap);
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

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 5);
        rvEmoji.setLayoutManager(gridLayoutManager);
        StickerAdapter stickerAdapter = new StickerAdapter();
        rvEmoji.setAdapter(stickerAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {

        int[] stickerList = new int[]{R.drawable.sticker_01, R.drawable.sticker_1, R.drawable.sticker_02, R.drawable.sticker_2, R.drawable.sticker_03 ,R.drawable.sticker_3,
                R.drawable.sticker_04,R.drawable.sticker_4, R.drawable.sticker_05,R.drawable.sticker_5, R.drawable.sticker_06, R.drawable.sticker_07,
                R.drawable.sticker_08, R.drawable.sticker_09, R.drawable.sticker_10, R.drawable.sticker_11,
                R.drawable.sticker_12, R.drawable.sticker_13, R.drawable.sticker_14, R.drawable.sticker_15 ,
                R.drawable.sticker_16, R.drawable.sticker_17, R.drawable.sticker_18, R.drawable.sticker_19 ,
                R.drawable.sticker_20, R.drawable.sticker_21,

                R.drawable.sticker_22, R.drawable.sticker_23, R.drawable.sticker_24 ,R.drawable.sticker_25,
                R.drawable.sticker_26, R.drawable.sticker_27, R.drawable.sticker_28,R.drawable.sticker_29,
                R.drawable.sticker_30, R.drawable.sticker_31,

                R.drawable.sticker_32,R.drawable.sticker_33, R.drawable.sticker_34, R.drawable.sticker_35 , R.drawable.sticker_36, R.drawable.sticker_37,
                R.drawable.sticker_38, R.drawable.sticker_39 , R.drawable.sticker_40, R.drawable.sticker_41,
                R.drawable.sticker_42, R.drawable.sticker_43, R.drawable.sticker_44 ,R.drawable.sticker_45,

                R.drawable.sticker_46, R.drawable.sticker_47, R.drawable.sticker_48, R.drawable.sticker_49,
                R.drawable.sticker_50, R.drawable.sticker_51, R.drawable.sticker_52, R.drawable.sticker_53,
                R.drawable.sticker_54, R.drawable.sticker_55 , R.drawable.sticker_56, R.drawable.sticker_57,
                R.drawable.sticker_58, R.drawable.sticker_59 , R.drawable.sticker_60, R.drawable.sticker_61,
                R.drawable.sticker_62, R.drawable.sticker_63, R.drawable.sticker_64 };

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sticker, parent, false);
            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.imgSticker.setImageResource(stickerList[position]);
        }

        @Override
        public int getItemCount() {
            return stickerList.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgSticker;

            ViewHolder(View itemView) {
                super(itemView);
                imgSticker = itemView.findViewById(R.id.imgSticker);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mStickerListener != null) {
                            mStickerListener.onStickerClick(
                                    BitmapFactory.decodeResource(getResources(),
                                            stickerList[getLayoutPosition()]));
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




//public class StickerFragment extends BottomSheetDialogFragment implements StickerListener  {
//
//    private StickerListener mStickerListener;
//    private StickerAdapter mStickerAdapter ;
//    private int[] mStickerList  ;
//    private RecyclerView mRecycleViewEmoji ;
//
//    public StickerFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//    }
//
//    public void setStickerListener(StickerListener stickerListener) {
//        mStickerListener = stickerListener;
//    }
//
//
//    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
//        @Override
//        public void onStateChanged(@NonNull View bottomSheet, int newState) {
//            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
//                dismiss();
//            }
//        }
//        @Override
//        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//        }
//    };
//
//
//    @SuppressLint("RestrictedApi")
//    @Override
//    public void setupDialog(Dialog dialog, int style) {
//        super.setupDialog(dialog, style);
//
//        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sticker_emoji_dialog, null);
//        dialog.setContentView(contentView);
//
//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
//        CoordinatorLayout.Behavior behavior = params.getBehavior();
//
//        if (behavior != null && behavior instanceof BottomSheetBehavior) {
//            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
//        }
//        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
//
//        mRecycleViewEmoji = contentView.findViewById(R.id.rvEmoji);
//
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 5);
//        mRecycleViewEmoji.setLayoutManager(gridLayoutManager);
//
//
//        mStickerList = new int[]{R.drawable.sticker_01, R.drawable.sticker_02, R.drawable.sticker_03 ,R.drawable.sticker_04,
//                R.drawable.sticker_05, R.drawable.sticker_06, R.drawable.sticker_07, R.drawable.sticker_08,
//                R.drawable.sticker_09, R.drawable.sticker_10, R.drawable.sticker_11, R.drawable.sticker_12, R.drawable.sticker_13,
//                R.drawable.sticker_14, R.drawable.sticker_15 , R.drawable.sticker_16, R.drawable.sticker_17,
//                R.drawable.sticker_18, R.drawable.sticker_19 , R.drawable.sticker_20, R.drawable.sticker_21};
//
//
//
//        mStickerAdapter = new StickerAdapter(getActivity(), mStickerListener , mStickerList);
//        mRecycleViewEmoji.setAdapter(mStickerAdapter);
//
//    }
//
//    /*StickerListener Interface Method*/
//    @Override
//    public void onStickerClick(Bitmap bitmap) {
//        dismiss();
//    }
//
//
//
//
//    private String convertEmoji(String emoji) {
//        String returnedEmoji = "";
//        try {
//            int convertEmojiToInt = Integer.parseInt(emoji.substring(2), 16);
//            returnedEmoji = getEmojiByUnicode(convertEmojiToInt);
//        } catch (NumberFormatException e) {
//            returnedEmoji = "";
//        }
//        return returnedEmoji;
//    }
//
//    private String getEmojiByUnicode(int unicode) {
//        return new String(Character.toChars(unicode));
//    }
//
//}