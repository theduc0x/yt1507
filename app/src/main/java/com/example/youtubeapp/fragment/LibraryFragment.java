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
import com.example.youtubeapp.activitys.MainActivity;

public class LibraryFragment extends Fragment {
    MainActivity mainActivity;
    public static final String TAG = LibraryFragment.class.getName();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        mainActivity = (MainActivity) getActivity();
        mainActivity.setToolBarMainVisible();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("duc1", "onAttack5");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("duc1", "onResume5");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("duc1", "onStart5");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("duc1", "onPause5");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("duc1", "onPauseStop5");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("duc1", "onDestroy5");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("duc1", "onDetach5");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("duc1", "onDestroyview5");
    }
}