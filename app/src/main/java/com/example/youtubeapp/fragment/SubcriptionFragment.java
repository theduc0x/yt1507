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

public class SubcriptionFragment extends Fragment {
    MainActivity mainActivity;
    public static final String TAG = SubcriptionFragment.class.getName();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subcription, container, false);
        mainActivity = (MainActivity) getActivity();
        return view;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("duc1", "onAttack3");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("duc1", "onResume3");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("duc1", "onStart3");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("duc1", "onPause3");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("duc1", "onPauseStop3");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("duc1", "onDestroy3");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("duc1", "onDetach3");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("duc1", "onDestroyview3");
    }
}