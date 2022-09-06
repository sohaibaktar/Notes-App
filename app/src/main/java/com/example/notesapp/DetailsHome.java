package com.example.notesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DetailsHome extends AppCompatActivity {

    EditText ed1,ed2;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_home);

        ed1 = findViewById(R.id.texttitle);
        ed2 = findViewById(R.id.textdetails);
        btn = findViewById(R.id.save_btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proseccinsert();
            }
        });
    }

    private void proseccinsert() {
        String title = ed1.getText().toString().trim();
        String details = ed2.getText().toString().trim();

        model md = new model(title,details);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference node = db.getReference("notes");

        int no=4;
        node.child("s").setValue(md);

        startActivity(new Intent(getApplicationContext(),MainActivity.class));

    }
}