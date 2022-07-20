package com.example.youtubeapp.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.youtubeapp.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDialogUserFragment extends BottomSheetDialogFragment {
    public static final String TAG = BottomSheetDialogUserFragment.class.getName();
    ImageView ivClose;

    public static BottomSheetDialogUserFragment newInstance() {
        return new BottomSheetDialogUserFragment();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog =
                (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_bottom_sheet_dialog_user, null);
        ivClose = view.findViewById(R.id.iv_close_not_login_user);
        bottomSheetDialog.setContentView(view);

        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog dialogc = (BottomSheetDialog) dialog;
                // When using AndroidX the resource can be found at com.google.android.material.R.id.design_bottom_sheet
                FrameLayout bottomSheet =
                        dialogc.findViewById(com.google.android.material.R.id.design_bottom_sheet );

                BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
                bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

//        BottomSheetBehavior bottomSheetBehavior =
//                BottomSheetBehavior.from(((View) view.getParent()));
//        bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//        bottomSheetBehavior.setDraggable(false);


//        ConstraintLayout layout = bottomSheetDialog.findViewById(R.id.cl_user_not_login);
//        assert layout != null;
//        layout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        return bottomSheetDialog;
    }
}