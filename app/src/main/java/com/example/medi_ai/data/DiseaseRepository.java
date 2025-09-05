package com.example.medi_ai.data;

import android.content.Context;
import com.example.medi_ai.model.Disease;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.*;

public class DiseaseRepository {
    private Context context;

    public DiseaseRepository(Context context) {
        this.context = context;
    }

    public List<Disease> loadDiseases() {
        List<Disease> list = new ArrayList<>();
        try {
            InputStream is = context.getAssets().open("disease.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String json = new String(buffer);

            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String name = obj.getString("disease");

                List<String> symptoms = new ArrayList<>();
                JSONArray sArr = obj.getJSONArray("symptoms");
                for (int j = 0; j < sArr.length(); j++) {
                    symptoms.add(sArr.getString(j));
                }

                int priority = obj.getInt("priority");
                list.add(new Disease(name, symptoms, priority));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
