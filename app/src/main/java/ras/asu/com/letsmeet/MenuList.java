package ras.asu.com.letsmeet;

/**
 * Created by Aditya on 3/19/2016.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MenuList extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initNavigationDrawer();

        Intent intent = getIntent();
        if(intent!=null && intent.getAction()!=null) {
            String action = intent.getAction();
           // String type = intent.getType();

            if (action.equals("acceptReq")) {
               //
                Toast.makeText(getApplicationContext(),"Request Accepted",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void initNavigationDrawer() {

        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id){
                    case R.id.asian:
                        Toast.makeText(getApplicationContext(),"Asian",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.bar:
                        Toast.makeText(getApplicationContext(),"Bar",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.buffet:
                        Toast.makeText(getApplicationContext(),"Buffet",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.coffee:
                        Toast.makeText(getApplicationContext(),"Coffee",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.fastfood:
                        Toast.makeText(getApplicationContext(),"FastFood",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.icecream:
                        Toast.makeText(getApplicationContext(),"Ice-Cream",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.italian:
                        Toast.makeText(getApplicationContext(),"Italian",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.pizza:
                        Toast.makeText(getApplicationContext(),"Pizza",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.sandwich:
                        Toast.makeText(getApplicationContext(),"Sandwich",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;

                    //case R.id.logout:
                      //  finish();

                }
                return true;
            }
        });
        View header = navigationView.getHeaderView(0);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerClosed(View v){
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

}
