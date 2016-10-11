package ve.com.willicab.radiognu;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class CatalogoActivity extends AppCompatActivity {

    public Intent iMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);

        iMain = new Intent(this, MainActivity.class);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabTextColors(Color.WHITE, Color.GRAY);
        tabLayout.addTab(tabLayout.newTab().setText("Reproductor"));
        tabLayout.addTab(tabLayout.newTab().setText("Catálogo"));
        tabLayout.addTab(tabLayout.newTab().setText("Ajustes"));

        TabLayout.Tab tab = tabLayout.getTabAt(1);
        tab.select();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i("RadioGNU", String.valueOf(tab.getPosition()));
                switch (tab.getPosition()) {
                    case 0:
                        startActivity(iMain);
                        break;
                    case 2:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
