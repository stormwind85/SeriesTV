package fr.eni.campus.series.seriestv;

import android.app.SearchManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class AccountActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout();
        enableLeftMenu();
        setNavigationViewListener();
    }

    private void initLayout() {
        setContentView(R.layout.activity_last_updates);
        drawerLayout = findViewById(R.id.leftHamburgerMenu);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
    }

    private void enableLeftMenu(){
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();
                        return true;
                    }
                }
        );
        navigationView.setCheckedItem(R.id.account);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        Intent intent = null;
        Class activityClass = null;

        switch (itemId){
            case R.id.home2:
                activityClass = MainActivity.class;
                intent = new Intent(this,activityClass);
                break;
            case R.id.connexion:
                activityClass = LoginActivity.class;
                intent = new Intent(this,activityClass);
                break;
            case R.id.account:
                activityClass = this.getClass();
                break;
            case R.id.lastUpdates:
                activityClass = LastUpdatesActivity.class;
                intent = new Intent(this,activityClass);
                break;
            case R.id.favorites:
                activityClass = FavoritesActivity.class;
                intent = new Intent(this, activityClass);
                break;
            default: return false;
        }

        if(activityClass != null && !this.equals(activityClass))
            if (intent != null)
                startActivity(intent);

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getResources().getString(R.string.searchSerie));
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }
}
