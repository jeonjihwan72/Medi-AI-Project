package com.example.medi_ai.network;

import android.util.Log;
import org.json.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import okhttp3.*;

public class GeminiApi {
    private static final String API_KEY = "GEMINI_API_KEY";
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    private static final List<String> SYMPTOM_MASTER = Arrays.asList(
            "가래", "가려움", "가스참", "가슴답답함", "간비대", "간종대", "감각소실", "감각이상", "건조한피부", "결막염",
            "경련", "경부강직", "경직", "고지혈증", "고칼슘혈증", "고혈압", "골다공증", "골반통", "골통증", "관절강직",
            "관절통", "구강궤양", "구강백태", "구토", "귀통증", "균형장애", "근력약화", "근육경련", "근육약화", "근육통",
            "기억력감퇴", "기억장애", "기침", "다갈", "다뇨", "단백뇨", "동맥경화", "두근거림", "두통", "떨림",
            "레이노현상", "림프절종대", "마비", "멍", "무기력", "무반응", "무증상", "무통성궤양", "반복감염", "발열",
            "발적", "발진", "발한", "배뇨장애", "배뇨통", "변비", "복부불쾌감", "복부팽만", "복수", "복시",
            "복통", "부종", "불면", "불안", "불임", "불쾌감", "비장종대", "빈뇨", "빈혈", "설사",
            "성격변화", "소화불량", "속쓰림", "손발비대", "수면무호흡", "수면장애", "수분공포증", "수포", "시력저하", "시야장애",
            "시야흐림", "식욕부진", "신부전", "실신", "심계항진", "심근경색", "심부전", "심비대", "심장비대", "안검하수",
            "야간기침", "야간발한", "야간호흡곤란", "야뇨", "어지럼증", "언어장애", "얼굴붓기", "열감", "오심", "오한",
            "요도분비물", "우울", "운동시호흡곤란", "운동완만", "운동장애", "운동제한", "의식소실", "이하선종대", "인후통", "잇몸출혈",
            "작열감", "저림", "저알부민혈증", "저혈압", "지남력장애", "질분비물", "집중력저하", "창백", "천명음", "청력저하",
            "체중감소", "체중증가", "출혈", "출혈경향", "침흘림", "코피", "콧물", "탈수", "턱경직", "통증",
            "편측마비", "피로감", "피부발진", "피부병변", "피부비후", "현기증", "혈뇨", "혈변", "호흡곤란", "혼란",
            "황달", "후각소실", "흉통"
    );

    public interface GeminiCallback {
        void onSuccess(List<String> symptoms);
        void onFailure(String error);
    }

    public static void extractSymptoms(String userInput, GeminiCallback callback) {
        OkHttpClient client = new OkHttpClient();
        String prompt = buildPrompt(userInput);

        try {
            JSONObject requestJson = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject content = new JSONObject();
            content.put("role", "user");

            JSONArray parts = new JSONArray();
            parts.put(new JSONObject().put("text", prompt));
            content.put("parts", parts);
            contents.put(content);

            requestJson.put("contents", contents);

            RequestBody body = RequestBody.create(
                    requestJson.toString(),
                    MediaType.get("application/json")
            );

            Request request = new Request.Builder()
                    .url(ENDPOINT)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        callback.onFailure("Gemini 응답 오류: " + response.message());
                        return;
                    }

                    try {
                        String resStr = response.body().string();
                        JSONObject json = new JSONObject(resStr);
                        JSONArray candidates = json.getJSONArray("candidates");
                        String resultText = candidates.getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text");

                        resultText = resultText.replaceAll("[\\[\\]\"]", "");
                        List<String> parsed = Arrays.asList(resultText.split(",\s*"));

                        List<String> matched = parsed.stream()
                                .filter(SYMPTOM_MASTER::contains)
                                .collect(Collectors.toList());

                        callback.onSuccess(matched);
                    } catch (Exception e) {
                        callback.onFailure("응답 파싱 오류: " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onFailure("네트워크 오류: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onFailure("요청 구성 오류: " + e.getMessage());
        }
    }

    private static String buildPrompt(String userInput) {
        return "다음은 증상 키워드 리스트입니다:\n" + SYMPTOM_MASTER +
                "\n\n사용자의 증상 입력: \"" + userInput + "\"" +
                "\n\n이 중 해당되는 키워드를 쉼표로 나열해서 출력하세요. 해당 없으면 '해당없음'이라고 해줘.";
    }

}
