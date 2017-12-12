package com.helpingiwthcode.mybakingapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.helpingiwthcode.mybakingapp.R;

import timber.log.Timber;

/**
 * Created by root on 12/12/17.
 */

public class InstructionsFragment extends Fragment {
    public InstructionsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.e("OnCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.e("OnCreateView");
        return inflater.inflate(R.layout.fragment_instructions, container, false);
    }
}
