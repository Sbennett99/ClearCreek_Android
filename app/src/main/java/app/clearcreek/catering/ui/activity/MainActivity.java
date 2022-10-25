package app.clearcreek.catering.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import app.clearcreek.catering.AppController;
import app.clearcreek.catering.R;
import app.clearcreek.catering.data.model.Product;
import app.clearcreek.catering.databinding.ActivityMainBinding;
import app.clearcreek.catering.ui.fragment.AboutUsFragment;
import app.clearcreek.catering.ui.fragment.ContactUsFragment;
import app.clearcreek.catering.ui.fragment.MainFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;

    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.content.toolbar);

        drawerToggle = new ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.content.toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        drawerToggle.syncState();

        binding.drawerLayout.addDrawerListener(drawerToggle);
        binding.navigation.setNavigationItemSelectedListener(this);

        Menu navigationMenu = binding.navigation.getMenu();
        MenuItem homeMenu = navigationMenu.findItem(R.id.action_home);
        if (homeMenu != null) {
            onNavigationItemSelected(homeMenu);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem myAccountMenu = menu.findItem(R.id.action_my_account);
        if (myAccountMenu != null) {
            View view = myAccountMenu.getActionView();
            if (view != null) {
                view.setOnClickListener(v -> {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if(currentUser != null && currentUser.isAnonymous()){
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(this, MyAccountActivity.class);
                        startActivity(intent);
                    }
                });
            }
        }

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        Fragment fragment = null;
        String title = getString(R.string.app_name);

        switch (id) {
            case R.id.action_home:
                fragment = new MainFragment();
                title = getString(R.string.app_name);
                break;
            case R.id.action_about_us:
                fragment = new AboutUsFragment();
                title = getString(R.string.about_us_label);
                break;
            case R.id.action_contact_us:
                fragment = new ContactUsFragment();
                title = getString(R.string.contact_us_label);
                break;
        }

        if (fragment != null) {
            replaceFragment(fragment, title);
            item.setChecked(true);
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment fragment, String title) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .commit();

        binding.content.toolbar.setTitle(title);
    }
}
