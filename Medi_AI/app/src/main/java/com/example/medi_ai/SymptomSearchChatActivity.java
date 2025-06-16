package com.example.medi_ai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SymptomSearchChatActivity extends AppCompatActivity {

    EditText inputText;
    ImageButton btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symptom_chat_page);

        inputText = findViewById(R.id.inputText);
        btnNext = findViewById(R.id.sendButton);

        btnNext.setOnClickListener(v -> {
            String symptomsText = inputText.getText().toString().trim();
            if (symptomsText.isEmpty()) {
                Toast.makeText(this, "증상을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(SymptomSearchChatActivity.this, SymptomFirstFilter.class);
            intent.putExtra("symptoms_input", symptomsText);
            startActivity(intent);
        });
    }
}