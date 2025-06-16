package com.example.medi_ai;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HanbatInfectActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;

    // 기본 단과대학과 아이콘, 색상 미리 정의
    private final Map<String, Integer> collegeIcons = new HashMap<String, Integer>() {{
        put("공과대학", R.drawable.icon_engineering);
        put("정보기술대학", R.drawable.icon_it);
        put("건설환경디자인대학", R.drawable.icon_design);
        put("인문사회대학", R.drawable.icon_humanities);
        put("경상대학", R.drawable.icon_business);
        put("융합자율대학", R.drawable.icon_convergence);
        put("노마드칼리지", R.drawable.icon_nomad);
    }};

    private final Map<String, Integer> collegeColors = new HashMap<String, Integer>() {{
        put("공과대학", Color.parseColor("#0D47A1"));
        put("정보기술대학", Color.parseColor("#00BCD4"));
        put("건설환경디자인대학", Color.parseColor("#E65100"));
        put("인문사회대학", Color.parseColor("#8E244D"));
        put("경상대학", Color.parseColor("#FFC107"));
        put("융합자율대학", Color.parseColor("#7E57C2"));
        put("노마드칼리지", Color.parseColor("#4DB6AC"));
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hanbat_infect_count_view);

        recyclerView = findViewById(R.id.hanbat_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 기본 카운트를 0으로 초기화 (모든 단과대학 포함)
        Map<String, Integer> collegeCountMap = new HashMap<>();
        for (String college : collegeIcons.keySet()) {
            collegeCountMap.put(college, 0);
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserAccount");

        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, -1);
                String monthAgo = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());

                for (DataSnapshot userSnap : task.getResult().getChildren()) {
                    String diagnosisDate = userSnap.child("diagnosisDate").getValue(String.class);
                    String college = userSnap.child("college").getValue(String.class);

                    // diagnosisDate가 유효하고, 1달 이내인 경우만 카운트 증가
                    if (diagnosisDate != null && !diagnosisDate.equals("0000-00-00")
                            && diagnosisDate.compareTo(monthAgo) >= 0 && diagnosisDate.compareTo(today) <= 0) {
                        if (college != null && collegeCountMap.containsKey(college)) {
                            collegeCountMap.put(college, collegeCountMap.get(college) + 1);
                        }
                    }
                }

                // RecyclerView에 연결할 리스트 준비
                List<Integer> imageList = new ArrayList<>();
                List<String> collegeList = new ArrayList<>();
                List<String> countList = new ArrayList<>();
                List<Integer> colorList = new ArrayList<>();

                for (String college : collegeIcons.keySet()) {
                    collegeList.add(college);
                    countList.add(collegeCountMap.get(college) + "건");
                    imageList.add(collegeIcons.get(college));
                    colorList.add(collegeColors.get(college));
                }

                myAdapter = new MyAdapter(imageList, collegeList, countList, colorList);
                recyclerView.setAdapter(myAdapter);

            } else {
                Toast.makeText(this, "데이터베이스 로드 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }
}