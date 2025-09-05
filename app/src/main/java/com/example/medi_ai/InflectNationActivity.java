package com.example.medi_ai;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InflectNationActivity extends AppCompatActivity {

    private final String encode_api = "ENCODED_API_KEY";

    private Spinner spinnerRegion;
    private TextView text_static;
    private ImageView nation_map;

    private Map<String, String> regionCasesMap = new HashMap<>();

    private String[] regions = {"서울", "부산", "대구", "인천", "광주", "대전", "울산",
            "경기", "강원", "충북", "충남", "전북", "전남", "경북", "경남", "제주"};

    private Integer[] map_img = {
            R.drawable.nation_seoul, R.drawable.nation_busan, R.drawable.nation_daegu, R.drawable.nation_incheon,
            R.drawable.nation_gwangju, R.drawable.nation_daejeon, R.drawable.nation_ulsan,
            R.drawable.nation_gyungki, R.drawable.nation_gangwon, R.drawable.nation_chungbuk,
            R.drawable.nation_chungnam, R.drawable.nation_jeonbuk, R.drawable.nation_jeonnam,
            R.drawable.nation_gyungbuk, R.drawable.nation_gyungnam, R.drawable.nation_jeju};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nation_inflection_page);

        spinnerRegion = findViewById(R.id.nation_spinner);
        text_static = findViewById(R.id.nation_static_covid19);
        nation_map = findViewById(R.id.nation_map);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, regions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRegion.setAdapter(adapter);

        // 데이터 가져오기 (2025-01-01 ~ 2025-06-30)
        fetchCovidData();

        spinnerRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRegion = regions[position];
                String cases = regionCasesMap.get(selectedRegion);
                nation_map.setImageResource(map_img[position]);

                if (cases != null) {
                    text_static.setText(selectedRegion + "의 누적 확진자 수: " + cases + "명");
                } else {
                    text_static.setText("정보를 불러오는 중입니다...");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                text_static.setText("");
            }
        });
    }

    private void fetchCovidData() {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();

                String targetDate = "2021-05-31"; // 조회할 날짜
                String encodedKey = URLEncoder.encode(encode_api, "UTF-8");

                String apiUrl = "http://apis.data.go.kr/1352000/ODMS_COVID_04/callCovid04Api"
                        + "?serviceKey=" + encodedKey
                        + "&std_day=" + targetDate;

                Request request = new Request.Builder()
                        .url(apiUrl)
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String xml = response.body().string();
                    Log.d("API_RESPONSE", "XML for " + targetDate + ": " + xml);

                    // totalCount 확인
                    int totalCount = getTotalCountFromXml(xml);

                    if (totalCount == 0) {
                        // 데이터가 없으면 0명으로 초기화
                        for (String region : regions) {
                            regionCasesMap.put(region, "0");
                        }

                        runOnUiThread(() -> {
                            int pos = spinnerRegion.getSelectedItemPosition();
                            String selectedRegion = regions[pos];
                            text_static.setText(selectedRegion + "의 누적 확진자 수: 0명");
                        });

                    } else {
                        // 데이터가 있으면 파싱해서 저장
                        Map<String, Integer> casesMap = new HashMap<>();
                        parseAndUpdateCumulativeCases(xml, casesMap);

                        Map<String, String> latestCasesStrMap = new HashMap<>();
                        for (String region : regions) {
                            Integer defCnt = casesMap.get(region);
                            if (defCnt != null) {
                                latestCasesStrMap.put(region, String.valueOf(defCnt));
                            }
                        }

                        synchronized (regionCasesMap) {
                            regionCasesMap.clear();
                            regionCasesMap.putAll(latestCasesStrMap);
                        }

                        runOnUiThread(() -> {
                            int pos = spinnerRegion.getSelectedItemPosition();
                            String selectedRegion = regions[pos];
                            String cases = regionCasesMap.get(selectedRegion);
                            if (cases != null) {
                                text_static.setText(selectedRegion + "의 누적 확진자 수: " + cases + "명");
                            } else {
                                text_static.setText("해당 지역 정보 없음");
                            }
                        });
                    }
                } else {
                    Log.e("API", "API 호출 실패: " + response.code());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // totalCount 추출 함수
    private int getTotalCountFromXml(String xml) {
        int totalCount = 0;
        try {
            InputStream is = new ByteArrayInputStream(xml.getBytes());
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("totalCount");
            if (nodeList.getLength() > 0) {
                String countStr = nodeList.item(0).getTextContent();
                totalCount = Integer.parseInt(countStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalCount;
    }

    // 누적 확진자 수 파싱 함수
    private void parseAndUpdateCumulativeCases(String xml, Map<String, Integer> casesMap) {
        try {
            InputStream is = new ByteArrayInputStream(xml.getBytes());
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("item");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                String gubun = element.getElementsByTagName("gubun").item(0).getTextContent();
                String defCntStr = element.getElementsByTagName("defCnt").item(0).getTextContent();

                if (Arrays.asList(regions).contains(gubun)) {
                    try {
                        int defCnt = Integer.parseInt(defCntStr);
                        casesMap.put(gubun, defCnt);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
