package com.example.medi_ai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Fragment_1 extends Fragment {

    public Fragment_1() {
        super(R.layout.main_page_frame_1);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View root = view.findViewById(R.id.fragment_root_1);
        root.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SymptomSearchChatActivity.class);
            startActivity(intent);
        });
    }
}