package com.example.medi_ai;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UserAccount {

    public UserAccount() {}

    public String getIdToken() { return idToken; }
    public void setIdToken(String idToken) { this.idToken = idToken; }
    private String idToken;

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    private String studentId;

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    private String password;

    public String getDiagnosisDate() { return diagnosisDate; }
    public void setDiagnosisDate(String diagnosisDate) { this.diagnosisDate = diagnosisDate; }
    private String diagnosisDate;

    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }
    private String college;
}