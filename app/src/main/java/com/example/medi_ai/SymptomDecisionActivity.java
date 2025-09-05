package com.example.medi_ai;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medi_ai.data.DiseaseRepository;
import com.example.medi_ai.model.Disease;

import java.util.*;

public class SymptomDecisionActivity extends AppCompatActivity {

    TextView questionText;
    Button yesBtn, noBtn;

    List<String> keywords;
    List<Disease> diseasePool;
    String nextQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symptom_decision_page);

        questionText = findViewById(R.id.questionText);
        yesBtn = findViewById(R.id.yesButton);
        noBtn = findViewById(R.id.noButton);

        keywords = getIntent().getStringArrayListExtra("symptom_keywords");
        List<String> topDiseases = getIntent().getStringArrayListExtra("top_diseases");

        diseasePool = new ArrayList<>();
        for (Disease d : new DiseaseRepository(this).loadDiseases()) {
            if (topDiseases.contains(d.name)) {
                diseasePool.add(d);
            }
        }

        // 첫 질문 선택
        nextQuestion = selectNextQuestion(diseasePool, keywords);
        if (nextQuestion == null) {
            moveToFinalResult();
        } else {
            showQuestion();
        }

        yesBtn.setOnClickListener(v -> {
            keywords.add(nextQuestion);  // 증상 있음 → 추가
            updateAfterAnswer(true);
        });

        noBtn.setOnClickListener(v -> {
            updateAfterAnswer(false);   // 증상 없음 → 제거 반영
        });
    }

    private void updateAfterAnswer(boolean userSaidYes) {
        if (!userSaidYes) {
            // 증상이 없으면, 해당 증상을 가진 질병 제거
            diseasePool.removeIf(d -> d.symptoms.contains(nextQuestion));
        }
        if (diseasePool.size() <= 1) {
            moveToFinalResult();  // 더 이상 추론 불필요
        } else {
            nextQuestion = selectNextQuestion(diseasePool, keywords);
            if (nextQuestion == null) {
                moveToFinalResult();  // 질문할 증상 없음
            } else {
                showQuestion();
            }
        }
    }

    private void showQuestion() {
        questionText.setText("다음 증상이 있나요? → " + nextQuestion);
    }
    private String selectNextQuestion(List<Disease> diseases, List<String> userKeywords) {
        Map<String, Integer> symptomCount = new HashMap<>();

        for (Disease d : diseases) {
            for (String s : d.symptoms) {
                if (!userKeywords.contains(s)) {
                    symptomCount.put(s, symptomCount.getOrDefault(s, 0) + 1);
                }
            }
        }
        return symptomCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private void moveToFinalResult() {
        Intent intent = new Intent(this, SymptomResultActivity.class);
        intent.putStringArrayListExtra("updated_keywords", new ArrayList<>(keywords));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
