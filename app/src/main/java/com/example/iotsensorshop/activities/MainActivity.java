package com.example.iotsensorshop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iotsensorshop.R;
import com.example.iotsensorshop.fragments.ChatRoomFragment;
import com.example.iotsensorshop.fragments.ChatbotFragment;
import com.example.iotsensorshop.fragments.DashboardFragment;
import com.example.iotsensorshop.fragments.HomeFragment;
import com.example.iotsensorshop.fragments.ManageProductFragment;
import com.example.iotsensorshop.fragments.OrderFragment;
import com.example.iotsensorshop.fragments.ProfileFragment;
import com.example.iotsensorshop.fragments.PurchaseHistoryFragment;
import com.example.iotsensorshop.models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Fragment homeFragment;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    String userId;
    DocumentReference documentReference;

    Toolbar toolbar;
    NavigationView navigationView;
    TextView tv_userName;
    TextView tv_userType;

    private DrawerLayout drawer;

    String name;
    String email;
    String userType;
    UserModel user;
    Bundle args = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = auth.getCurrentUser().getUid();
        documentReference = fStore.collection("users").document(userId);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        tv_userName = (TextView)view.findViewById(R.id.userName);
        tv_userType = (TextView)view.findViewById(R.id.userType);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    name = documentSnapshot.getString("Name");
                    email = documentSnapshot.getString("Email");
                    userType = documentSnapshot.getString("userType");
                    UserModel user = new UserModel(userId, name, email, userType);

                    if (user.getUserType().equals("Admin"))
                    {
                        navigationView.getMenu().clear();
                        navigationView.inflateMenu(R.menu.admin_menu);
                    }

                    args.putString("username", user.getName());

                    tv_userName.setText(user.getName());
                    tv_userType.setText(user.getUserType());

                    getIntent().putExtra("user", user);
                }else {
                    Toast.makeText(getApplicationContext(), "Failed to Retrieve User Data!", Toast.LENGTH_SHORT).show();
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to Retrieve User Data!", Toast.LENGTH_SHORT).show();
            }
        });

        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) //show profile fragment when opening
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.home_container,
                    new HomeFragment()).commit(); //open profile fragment after user login
            navigationView.setCheckedItem(R.id.nav_home);
        }

        homeFragment = new HomeFragment();
        loadFragment(homeFragment);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        setTitle("IoTSensorShop");

        switch (item.getItemId())
        {
            case R.id.nav_home:

                getSupportFragmentManager().beginTransaction().replace(R.id.home_container,
                        new HomeFragment()).commit();
                break;

            case R.id.nav_profile:

                getSupportFragmentManager().beginTransaction().replace(R.id.home_container,
                        new ProfileFragment()).commit();
                break;

            case R.id.nav_products:

                startActivity(new Intent(MainActivity.this, ShowAllActivity.class));
                finish();
                break;

            case R.id.nav_manage_products:

                getSupportFragmentManager().beginTransaction().replace(R.id.home_container,
                        new ManageProductFragment()).commit();
                break;

            case R.id.nav_purchase_history:

                getSupportFragmentManager().beginTransaction().replace(R.id.home_container,
                        new PurchaseHistoryFragment()).commit();
                break;

            case R.id.nav_chat_room:
                getSupportFragmentManager().beginTransaction().replace(R.id.home_container,
                        new ChatRoomFragment()).commit();
                break;

            case R.id.nav_logout:

                auth.signOut();
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
                finish();
                break;

            case R.id.nav_order:

                getSupportFragmentManager().beginTransaction().replace(R.id.home_container,
                        new OrderFragment()).commit();
                break;

            case R.id.nav_chat_bot:

                getSupportFragmentManager().beginTransaction().replace(R.id.home_container,
                        new ChatbotFragment()).commit();
                break;

            case R.id.nav_video_call:

                getSupportFragmentManager().beginTransaction().replace(R.id.home_container,
                        new DashboardFragment()).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        /*
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (f instanceof ChatFragment)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ChatRoomFragment()).commit();

            setTitle("LibraryManagementApp");
        }

         */


        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }

        else
        {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Quit the app...")
                    .setMessage("Do you want to quit the app?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity(); //close the app
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();

        }

    }



    private void loadFragment(Fragment homeFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_container, homeFragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    userType = documentSnapshot.getString("userType");

                    if (userType.equals("Admin")) {
                        getMenuInflater().inflate(R.menu.admin_main_menu, menu);
                    } else {
                        getMenuInflater().inflate(R.menu.main_menu, menu);
                    }
                }
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_logout) {
            auth.signOut();
            startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
            finish();

        } else if (id == R.id.menu_my_cart){
            startActivity(new Intent(MainActivity.this, CartActivity.class));
        }
        return true;
    }
}