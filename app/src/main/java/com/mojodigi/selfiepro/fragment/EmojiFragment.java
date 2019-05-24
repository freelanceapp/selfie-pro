package com.mojodigi.selfiepro.fragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mojodigi.selfiepro.R;

import java.util.ArrayList;

import ja.burhanrashid52.photoeditor.PhotoEditor;

public class EmojiFragment extends BottomSheetDialogFragment {

    public EmojiFragment() {
        // Required empty public constructor
    }

    private EmojiListener mEmojiListener;

    public interface EmojiListener {
        void onEmojiClick(String emojiUnicode);
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

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

        EmojiAdapter emojiAdapter = new EmojiAdapter();
        rvEmoji.setAdapter(emojiAdapter);
    }

    public void setEmojiListener(EmojiListener emojiListener) {
        mEmojiListener = emojiListener;
    }


    public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.ViewHolder> {

        ArrayList<String> emojisList = PhotoEditor.getEmojis(getActivity());

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_emoji, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.txtEmoji.setText(emojisList.get(position));
        }

        @Override
        public int getItemCount() {
            return emojisList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtEmoji;

            ViewHolder(View itemView) {
                super(itemView);
                txtEmoji = itemView.findViewById(R.id.txtEmoji);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mEmojiListener != null) {
                            mEmojiListener.onEmojiClick(emojisList.get(getLayoutPosition()));
                        }
                        dismiss();
                    }
                });
            }
        }
    }
}



//public class EmojiFragment extends BottomSheetDialogFragment implements EmojiListener {
//
//    private ArrayList<String> mEmojisList ;
//    private EmojiAdapter mEmojiAdapter ;
//    private EmojiListener mEmojiListener;
//    private RecyclerView mRecyclerViewEmoji ;
//
//
//    public EmojiFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//    }
//
//
//    public void setEmojiListener(EmojiListener emojiListener) {
//        mEmojiListener = emojiListener;
//    }
//
////    public interface EmojiListener {
////        void onEmojiClick(String emojiUnicode);
////    }
//
//    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
//
//        @Override
//        public void onStateChanged(@NonNull View bottomSheet, int newState) {
//            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
//                dismiss();
//            }
//        }
//
//        @Override
//        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//        }
//    };
//
//    @SuppressLint("RestrictedApi")
//    @Override
//    public void setupDialog(final Dialog dialog, int style) {
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
//        mRecyclerViewEmoji = contentView.findViewById(R.id.rvEmoji);
//
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 5);
//        mRecyclerViewEmoji.setLayoutManager(gridLayoutManager);
//
//        //mEmojisList = PhotoEditor.getEmojis(getActivity());
//
//         mEmojiAdapter = new EmojiAdapter(getActivity() , mEmojiListener , mEmojisList);
//         mRecyclerViewEmoji.setAdapter(mEmojiAdapter);
//
//
//    }
//
//
//
//    @Override
//    public void onEmojiClick(String emojiUnicode) {
//         dismiss();
//    }
//}
