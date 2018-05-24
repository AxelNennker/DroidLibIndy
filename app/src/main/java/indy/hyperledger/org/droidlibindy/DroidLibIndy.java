package indy.hyperledger.org.droidlibindy;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

public class DroidLibIndy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        File dataDir = getApplicationContext().getDataDir();
        System.out.println("datadir=" + dataDir.getAbsolutePath());
        File externalFilesDir = getExternalFilesDir(null);
        System.out.println("externalFilesDir=" + externalFilesDir.getAbsolutePath());


        File[] files = externalFilesDir.listFiles();
        for (int i = 0; i < files.length; ++i) {
            File file = files[i];
            if (file.isDirectory()) {
                System.out.println("axel directory:" + file.getName());
                if (".indy_client".equals(file.getName())) {
                    String[] children = file.list();
                    for (int j = 0; j < children.length; j++)
                    {
                        System.out.println("axel deleting:" + children[j]);
                        new File(file, children[j]).delete();
                    }
                }
            } else {
                System.out.println("axel file     :" + file.getName());
            }
        }

        System.loadLibrary( "indy");

        setContentView(R.layout.activity_droid_lib_indy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_droid_lib_indy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
