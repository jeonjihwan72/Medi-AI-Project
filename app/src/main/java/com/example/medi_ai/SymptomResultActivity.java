package com.example.medi_ai;

import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medi_ai.data.DiseaseRepository;
import com.example.medi_ai.model.Disease;
import com.example.medi_ai.util.PriorityScorer;
import com.example.medi_ai.util.SimilarityUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SymptomResultActivity extends AppCompatActivity {

    ScrollView scrollView;
    TextView disease_name, disease_percent;
    TextView disease_overview, disease_infectious, disease_treatment, disease_food;
    List<Disease> diseases;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symptom_final_page);

        disease_name = findViewById(R.id.diseaseName);
        disease_percent = findViewById(R.id.percentageOfDisease);

        scrollView = findViewById(R.id.symptom_result_scrollview);

        disease_overview = findViewById(R.id.diseaseOverview_content);
        disease_infectious = findViewById(R.id.infectiousDisease_content);
        disease_treatment = findViewById(R.id.treatment_content);
        disease_food = findViewById(R.id.recommendedFood_content);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        List<String> symptoms = getIntent().getStringArrayListExtra("updated_keywords");

        diseases = new DiseaseRepository(this).loadDiseases();

        Disease best = null;
        double bestScore = 0;

        for (Disease d : diseases) {
            double sim = SimilarityUtil.calculateJaccard(symptoms, d.symptoms);
            double score = PriorityScorer.applyPriority(sim, d.priority);
            if (score > bestScore) {
                bestScore = score;
                best = d;
            }
        }

        try {
            String jsonStr = readJsonFromAssets("disease_detail.json");
            JSONArray array = new JSONArray(jsonStr);

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                assert best != null;
                if (obj.getString("disease").equals(best.name)) {
                    String overview = obj.optString("overview", "정보 없음");
                    String treatment = obj.optString("treatment", "정보 없음");
                    String food = obj.optString("food", "정보 없음");
                    Boolean infectious = obj.optBoolean("infectious", false);
                    String infectiousText = infectious ? "전염성 있음" : "전염성 없음";

                    disease_overview.setText(overview);
                    disease_infectious.setText(infectiousText);
                    disease_treatment.setText(treatment);
                    disease_food.setText(food);

                    if (infectious) {
                        updateDiagnosisDateToToday();
                    }

                    break;
                }
            }

        } catch (Exception e) {
            Toast.makeText(this, "질병 상세내용 가져오기 실패", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        if (best != null) {
            scrollView.setVisibility(ScrollView.VISIBLE);
            disease_name.setText("✅ " + best.name );
            disease_percent.setText(String.valueOf(bestScore*100) + " %");
        } else {
            disease_name.setText("⚠️ 해당 증상에 대한 질병을 찾을 수 없습니다.");
            scrollView.setVisibility(ScrollView.INVISIBLE);
        }
    }

    private String readJsonFromAssets(String filename) throws IOException {
        InputStream is = getAssets().open(filename);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        return new String(buffer, StandardCharsets.UTF_8);
    }

    private void updateDiagnosisDateToToday() {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "로그인된 사용자가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();

        // 현재 날짜를 문자열로 변환 (예: "2025-06-08")
        String today = java.time.LocalDate.now().toString();

        // DB 경로: UserAccount/{uid}/diagnosisDate
        mDatabaseRef.child("UserAccount").child(uid).child("diagnosisDate").setValue(today)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "진단일이 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "진단일 업데이트 실패", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

