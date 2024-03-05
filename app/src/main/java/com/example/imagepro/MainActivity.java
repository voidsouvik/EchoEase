package com.example.imagepro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.imagepro.activities.AboutSignLanguages;
import com.example.imagepro.activities.HowToUse;
import com.example.imagepro.activities.LoginActivity;
import com.example.imagepro.activities.SettingsActivity;
import com.example.imagepro.activities.TranslatorActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.opencv.android.OpenCVLoader;
import org.w3c.dom.Text;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    static {
        if(OpenCVLoader.initDebug()){
            Log.d("MainActivity: ","Opencv is loaded");
        }
        else {
            Log.d("MainActivity: ","Opencv failed to load");
        }
    }

    private CardView camera_button;
    private CardView combine_letter_button;
    private TextView userProfileName;
    static final float END_SCALE = 0.7f;
    LinearLayout contentView;
    ImageView menuIccon;
    //Drawer Menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menuIccon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.content_view);

        // ini
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userId = mAuth.getCurrentUser().getUid();

        //drawer hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        userProfileName = findViewById(R.id.userName);


        //navigation drawer profile image
        View headerView = navigationView.getHeaderView(0);
        CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);
        TextView userName = headerView.findViewById(R.id.user_name);
        TextView userEmailId = headerView.findViewById(R.id.user_emailID);

        if (currentUser.getPhotoUrl() != null)
        {
            userName.setText(currentUser.getDisplayName());
            userEmailId.setText(currentUser.getEmail());
            userProfileName.setText(currentUser.getDisplayName());
            Glide.with(MainActivity.this).load(currentUser.getPhotoUrl()).into(profileImageView);

        }
        else {
            userName.setText(currentUser.getDisplayName());
            userEmailId.setText(currentUser.getEmail());
            userProfileName.setText(currentUser.getDisplayName());
            Glide.with(MainActivity.this).load(R.drawable.profile_avatar).into(profileImageView);

        }

        navigationDrawer();


        camera_button=findViewById(R.id.camera_button);
        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        combine_letter_button=findViewById(R.id.combine_letter_button);
        combine_letter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,LetterCombineActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

    }

    //Navigation Drawer Function
    private void navigationDrawer() {

        //Navigation Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        menuIccon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        animateNavigationDrawer();
    }

    private void animateNavigationDrawer() {
        // drawerLayout.setScrimColor(getResources().getColor(R.color.colorPrimary));
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                break;
            case R.id.nav_signLanguages:
                startActivity(new Intent(getApplicationContext(), AboutSignLanguages.class));
                break;
            case R.id.nav_translator:
                startActivity(new Intent(getApplicationContext(), TranslatorActivity.class));
                break;
            case R.id.nav_how_to_use:
                startActivity(new Intent(getApplicationContext(), HowToUse.class));
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent2 = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.nav_profile:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;
        }
        return true;
    }
}