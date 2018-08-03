package indy.hyperledger.org.droidlibindy;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.system.ErrnoException;
import android.system.Os;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.sun.jna.Library;

import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.LibIndy;
import org.hyperledger.indy.sdk.pool.Pool;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class DroidLibIndy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        File dataDir = getApplicationContext().getDataDir();
        System.out.println("datadir=" + dataDir.getAbsolutePath());
        File externalFilesDir = getExternalFilesDir(null);
        String path = externalFilesDir.getAbsolutePath();
        System.out.println("axel externalFilesDir=" + path);

        try {
            Os.setenv("EXTERNAL_STORAGE", path, true);
        } catch (ErrnoException e) {
            e.printStackTrace();
        }

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

        LibIndy.init();

        setContentView(R.layout.activity_droid_lib_indy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Wallet wallet = null;

                try {
                    final String WALLET = "Wallet1";
                    final String TYPE = "default";
                    final String WALLET_CREDENTIALS =
                            new JSONObject()
                                    .put("key", "key")
                                    .toString();
                    final String WALLET_CONFIG =
                            new JSONObject()
                                    .put("id", WALLET)
                                    .put("storage_type", TYPE)
                                    .toString();
                    try {
                        Wallet.createWallet(WALLET_CONFIG, WALLET_CREDENTIALS).get();
                    } catch (ExecutionException e) {
                        System.out.println( e.getMessage() );
                        if (e.getMessage().indexOf("WalletExistsException") >= 0) {
                            // ignore
                        } else {
                            throw new RuntimeException(e);
                        }
                    }
                    wallet = Wallet.openWallet(WALLET_CONFIG, WALLET_CREDENTIALS).get();
                    System.out.println("===================> wallet:" + wallet);
                    Snackbar.make(view, "===================> wallet:" + wallet, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                } catch (IndyException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } finally {
                    if (wallet != null) {
                        try {
                            wallet.closeWallet().get();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

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
