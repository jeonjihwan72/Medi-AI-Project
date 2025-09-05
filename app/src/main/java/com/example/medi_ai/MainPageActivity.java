package com.example.medi_ai;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import me.relex.circleindicator.CircleIndicator3;

public class MainPageActivity extends AppCompatActivity {

    // 오늘의 소식 객체
    TextView newsTextView;
    private String URL = "https://news.naver.com/breakingnews/section/103/241";

    // main_page 슬라이드 뷰 객체
    ViewPager2 main_viewpager;
    FragmentStateAdapter pagerAdapter;
    CircleIndicator3 main_indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.main_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                Bundle bundle = msg.getData();  // 데이터 받기
                String newsTitle = bundle.getString("news_title");
                String newsHref = bundle.getString("news_link");

                if (newsTitle != null && newsHref != null) {
                    SpannableString spannableString = new SpannableString(newsTitle);
                    spannableString.setSpan(new URLSpan(newsHref), 0, newsTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    newsTextView.setText(spannableString);
                    newsTextView.setMovementMethod(LinkMovementMethod.getInstance()); // 클릭 가능하게 설정
                }
            }
        };
        // 오늘의 소식에 뉴스 가져오기
        newsTextView = findViewById(R.id.news_title);
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect(URL).get();

                // <a> 태그 전체를 선택 (클래스 기준)
                Elements links = doc.select("a.sa_text_title._NLOG_IMPRESSION");
                if (!links.isEmpty()) {
                    Element firstLink = links.first(); // 첫 번째 뉴스 가져오기
                    String newsTitle = firstLink.text(); // <strong> 안의 텍스트도 같이 포함됨
                    String newsHref = firstLink.absUrl("href"); // 절대 URL로 링크 추출
                    // 메인 스레드로 전송
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("news_title", newsTitle);
                    bundle.putString("news_link", newsHref);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // ViewPager2 설정
        main_viewpager = findViewById(R.id.main_page_viewpager);
        main_indicator = findViewById(R.id.indicatior);

        pagerAdapter = new MainFragmentAdapter(this);
        main_viewpager.setAdapter(pagerAdapter);
        main_indicator.setViewPager(main_viewpager);
        
        // CircleIndicator 갱신용
        main_viewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                main_indicator.animatePageSelected(position);
            }
        });
    }
}
