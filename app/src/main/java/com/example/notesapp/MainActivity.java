package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    //design
    NavigationView nav;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;

    RecyclerView recview;
    myadapter adapter;
    FloatingActionButton fab;

    //header
    TextView profile_name;
    ImageView profile_img;
    //searchView
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();


        //find ids
        recview = (RecyclerView) findViewById(R.id.recview);
        fab = (FloatingActionButton) findViewById(R.id.floating_Action_btn);
        toolbar = findViewById(R.id.toolbar);
        nav = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawer_layout);
        profile_name = (TextView) nav.getHeaderView(0).findViewById(R.id.header_name);
        profile_img = (ImageView) nav.getHeaderView(0).findViewById(R.id.header_pimage);
        searchView = (SearchView) findViewById(R.id.search_view);
        searchView.clearFocus();

        setSupportActionBar(toolbar);
        //drawer clodes and opened
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.opennav,R.string.closednav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        //firebase
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //header work
        profile_name.setText(account.getDisplayName());
        Glide.with(this).load(account.getPhotoUrl()).into(profile_img);

        //menu work
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.settings:
                        Log.d("TAG", "onNavigationItemSelected: ");
                        Toast.makeText(getApplicationContext(), "click settings", Toast.LENGTH_LONG).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.p_logout:
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(MainActivity.this,Login.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(getApplicationContext(), "click logout", Toast.LENGTH_LONG).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }
                return true;
            }
        });


        //recycler View
        recview.setLayoutManager(new GridLayoutManager(this,2));
        FirebaseRecyclerOptions<model> options =
                new FirebaseRecyclerOptions.Builder<model>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("notes").child(account.getId()), model.class)
                        .build();
        adapter = new myadapter(options);
        recview.setAdapter(adapter);


        //floating btn click another activity
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),DetailsHome.class));
            }
        });

        //searching work
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchtext(newText);
                return false;
            }
        });
    }

    private void searchtext(String newText) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        FirebaseRecyclerOptions<model> options =
                new FirebaseRecyclerOptions.Builder<model>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("notes")
                        .child(account.getId()).orderByChild("details")
                        .startAt(newText).endAt(newText+"\uf8ff"), model.class)
                        .build();

        adapter=new myadapter(options);
        adapter.startListening();
        recview.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }


}
