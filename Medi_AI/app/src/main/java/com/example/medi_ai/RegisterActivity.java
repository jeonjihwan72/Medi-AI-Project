package com.example.medi_ai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;     // 파이어베이스 인증
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스

    private EditText mID, mPWD, mRPWD;
    private Button buttonRegister, buttonCancel;
    private Spinner registerSpinner;

    String[] items = {"공과대학", "정보기술대학", "건설환경디자인대학", "인문사회대학", "경상대학", "융합자율대학", "노마드칼리지"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup_page),(v,insets)->{
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(RegisterActivity.this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, items);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        mID = findViewById(R.id.signup_id);
        mPWD = findViewById(R.id.signup_password);
        mRPWD = findViewById(R.id.signup_password_check);

        registerSpinner = findViewById(R.id.signup_spinner);
        registerSpinner.setAdapter(adapter);

        buttonCancel = findViewById(R.id.signup_cancel);
        buttonCancel.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        buttonRegister = findViewById(R.id.signup_button);
        buttonRegister.setOnClickListener(v -> {
            // 회원가입 처리 시작
            String strID = mID.getText().toString();
            String strPWD = mPWD.getText().toString();
            String emailLikeId = strID + "@medi-ai.com";
            String strCollege = registerSpinner.getSelectedItem().toString();

            // Firebase Auth 진행
            mFirebaseAuth.createUserWithEmailAndPassword(emailLikeId, strPWD).
                    addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                        UserAccount account = new UserAccount();
                        account.setIdToken(firebaseUser.getUid());
                        account.setStudentId(firebaseUser.getEmail());
                        account.setPassword(strPWD);
                        account.setCollege(strCollege);
                        account.setDiagnosisDate("0000-00-00");

                        mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);
                        Toast.makeText(RegisterActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }
}