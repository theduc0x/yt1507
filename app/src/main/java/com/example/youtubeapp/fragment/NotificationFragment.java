package com.example.youtubeapp.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.youtubeapp.R;
public class NotificationFragment extends Fragment {
    public static final String TAG = NotificationFragment.class.getName();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_notification, container, false);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("duc1", "onAttack4");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("duc1", "onResume4");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("duc1", "onStart4");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("duc1", "onPause4");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("duc1", "onPauseStop4");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("duc1", "onDestroy4");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("duc1", "onDetach4");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("duc1", "onDestroyview4");
    }
}