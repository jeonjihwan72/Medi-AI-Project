package com.example.medi_ai;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medi_ai.data.DiseaseRepository;
import com.example.medi_ai.model.Disease;
import com.example.medi_ai.network.GeminiApi;
import com.example.medi_ai.util.SimilarityUtil;
import com.example.medi_ai.util.PriorityScorer;

import java.util.*;

public class SymptomFirstFilter extends AppCompatActivity {

    TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symptom_first_filtering_page);

        resultText = findViewById(R.id.textView2);
        String input = getIntent().getStringExtra("symptoms_input");

        GeminiApi.extractSymptoms(input, new GeminiApi.GeminiCallback() {
            @Override
            public void onSuccess(List<String> keywords) {
                runOnUiThread(() -> analyzeSymptoms(keywords));
                Log.d("추출된 키워드", keywords.toString());
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> resultText.setText("Gemini 오류: " + error));
            }
        });
    }

    private void analyzeSymptoms(List<String> keywords) {
        DiseaseRepository repo = new DiseaseRepository(this);
        List<Disease> diseases = repo.loadDiseases();
        Map<String, Double> resultMap = new HashMap<>();

        for (Disease d : diseases) {
            double sim = SimilarityUtil.calculateJaccard(keywords, d.symptoms);
            double score = PriorityScorer.applyPriority(sim, d.priority);
            if (score > 0) resultMap.put(d.name, score);
        }

        List<Map.Entry<String, Double>> sorted = new ArrayList<>(resultMap.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        // 유사도 차이가 확실한 경우 → 바로 결과 액티비티로 이동
        if (sorted.size() >= 2 && sorted.get(0).getValue() - sorted.get(1).getValue() > 0.3) {
            Intent intent = new Intent(this, SymptomResultActivity.class);
            intent.putStringArrayListExtra("updated_keywords", new ArrayList<>(keywords));
            intent.putExtra("confirmed_disease", sorted.get(0).getKey());
            startActivity(intent);
            return;
        }

        // 점수 차이가 애매한 경우 → 질문 루프로 이동
        ArrayList<String> topNames = new ArrayList<>();
        for (int i = 0; i < Math.min(3, sorted.size()); i++) {
            topNames.add(sorted.get(i).getKey());
        }

        Intent intent = new Intent(this, SymptomDecisionActivity.class);
        intent.putExtra("symptom_keywords", new ArrayList<>(keywords));
        intent.putExtra("top_diseases", topNames);
        startActivity(intent);
    }
}
