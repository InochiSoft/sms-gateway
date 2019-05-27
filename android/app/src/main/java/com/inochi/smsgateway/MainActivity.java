package com.inochi.smsgateway;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.Toast;

import com.inochi.smsgateway.helper.BundleSettings;
import com.inochi.smsgateway.helper.Constants;
import com.inochi.smsgateway.helper.Helper;
import com.inochi.smsgateway.listener.GetListener;
import com.inochi.smsgateway.service.SMSReceiver;

public class MainActivity extends AppCompatActivity implements GetListener {
    private BundleSettings bundleSettings;
    private Helper helper;

    private TableRow tblIPServer;
    private TableRow tblDuration;
    private EditText editIPServer;
    private EditText editDuration;

    private Button btnConnect;
    private Button btnSetting;

    private String ipServer;
    private int duration;
    private boolean isConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bundleSettings = new BundleSettings(this);
        helper = new Helper(this);

        ipServer = bundleSettings.getIPServer();
        duration = bundleSettings.getCheckDuration();

        tblIPServer = this.findViewById(R.id.tblIPServer);
        tblDuration = this.findViewById(R.id.tblDuration);
        editIPServer = this.findViewById(R.id.editIPServer);
        editDuration = this.findViewById(R.id.editDuration);
        btnConnect = this.findViewById(R.id.btnConnect);
        btnSetting = this.findViewById(R.id.btnSetting);

        editIPServer.setText(ipServer);
        editDuration.setText(String.valueOf(duration));

        btnConnect.setOnClickListener(onButtonClick);
        btnSetting.setOnClickListener(onButtonClick);

        setPermission();
    }

    private void connect(){
        isConnect = false;

        Editable editableIPServer = editIPServer.getText();
        Editable editableDuration = editDuration.getText();

        ipServer = editableIPServer.toString();
        String strDuration = editableDuration.toString();

        if (strDuration.isEmpty()){
            duration = 1;
        } else {
            duration = Integer.parseInt(strDuration);
        }

        if (!ipServer.isEmpty() && duration > 0){
            bundleSettings.setIPServer(ipServer);
            bundleSettings.setCheckDuration(duration);

            helper.ping();
        }

        updateUI();
    }

    private View.OnClickListener onButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnConnect:
                    connect();
                    break;
                case R.id.btnSetting:
                    isConnect = false;
                    removeService();
                    updateUI();
                    break;
            }
        }
    };

    public void updateUI(){
        if (isConnect){
            tblIPServer.setVisibility(View.GONE);
            tblDuration.setVisibility(View.GONE);
            btnConnect.setVisibility(View.GONE);
            btnSetting.setVisibility(View.VISIBLE);

            Toast.makeText(this, "Server connected", Toast.LENGTH_LONG).show();

            createService();
        } else {
            tblIPServer.setVisibility(View.VISIBLE);
            tblDuration.setVisibility(View.VISIBLE);
            btnConnect.setVisibility(View.VISIBLE);
            btnSetting.setVisibility(View.GONE);

            Toast.makeText(this, "Server disconnected", Toast.LENGTH_LONG).show();
        }
    }

    private void createService(){
        Intent intent = new Intent(this, SMSReceiver.class);
        intent.setAction(Constants.Action.CREATE_DAILY);
        sendBroadcast(intent);
    }

    private void removeService(){
        Intent intent = new Intent(this, SMSReceiver.class);
        intent.setAction(Constants.Action.REMOVE_DAILY);
        sendBroadcast(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        try {
            setPermission();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("InlinedApi")
    private void setPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED},
                    Constants.Permission.Type.RECEIVE_BOOT_COMPLETED);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WAKE_LOCK},
                    Constants.Permission.Type.WAKE_LOCK);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.VIBRATE},
                    Constants.Permission.Type.VIBRATE);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS},
                    Constants.Permission.Type.READ_SMS);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},
                    Constants.Permission.Type.SEND_SMS);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS},
                    Constants.Permission.Type.RECEIVE_SMS);
        } else {
            if (bundleSettings.getBootPremission() == 0){
                runAutoStartCustom();
            } else {
                connect();
            }
        }
    }

    private static final Intent[] AUTO_START_INTENTS = {
            new Intent().setComponent(new ComponentName("com.samsung.android.lool",
                    "com.samsung.android.sm.ui.battery.BatteryActivity")),
            new Intent("miui.intent.action.OP_AUTO_START").addCategory(Intent.CATEGORY_DEFAULT),
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity")).setData(
                    Uri.parse("mobilemanager://function/entry/AutoStart"))
    };

    private void runAutoStartCustom(){
        for (Intent intent : AUTO_START_INTENTS){
            if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                startActivity(intent);

                bundleSettings.setBootPremission(1);
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onGetResult(String result) {
        if (result != null){
            if (!result.isEmpty()){
                if (result.equals("1")){
                    isConnect = true;
                }
            }
        }
        updateUI();
    }
}
