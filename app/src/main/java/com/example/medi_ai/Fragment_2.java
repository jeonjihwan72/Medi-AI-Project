package com.example.medi_ai;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Fragment_2 extends Fragment {

    public Fragment_2() {
        super(R.layout.main_page_frame_2);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View root = view.findViewById(R.id.fragment_root_2);
        root.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), InflectNationActivity.class);
            startActivity(intent);
        });
    }
}