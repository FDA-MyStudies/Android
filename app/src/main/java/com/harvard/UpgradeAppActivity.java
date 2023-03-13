package com.harvard;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.harvard.utils.AppController;
import com.harvard.utils.Version.VersionChecker;

public class UpgradeAppActivity extends AppCompatActivity {
    Context mContext;
    private boolean forceUpgrade;
    private  SplashActivity splashActivity = new SplashActivity();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade_app);
        mContext = this;
        if(getIntent().hasExtra("forceUpgrade")){
            forceUpgrade = getIntent().getBooleanExtra("forceUpgrade",false);
        }
        TextView version = findViewById(R.id.version);
        TextView message = findViewById(R.id.message);
        AppCompatTextView upgrade = findViewById(R.id.upgrade);
        AppCompatTextView upgrade1 = findViewById(R.id.upgrade1);
        AppCompatTextView upgradeCancel = findViewById(R.id.upgradeCancel);
        LinearLayoutCompat noForceUpgrade = findViewById(R.id.noForceUpgradeLayout);

        version.setText(mContext.getResources().getString(R.string.upgrade_app_activity_upgrade_message1));
        if(forceUpgrade){
            upgrade.setVisibility(View.VISIBLE);
            noForceUpgrade.setVisibility(View.GONE);
            message.setText(mContext.getResources().getString(R.string.upgrade_app_activity_upgrade_message2));
        }else {
            message.setText(mContext.getResources().getString(R.string.upgrade_app_activity_upgrade_message4));
            upgrade.setVisibility(View.GONE);
            noForceUpgrade.setVisibility(View.VISIBLE);
        }
        upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(VersionChecker.PLAY_STORE_URL)));
                finish();
            }
        });
        upgrade1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(VersionChecker.PLAY_STORE_URL)));
                finish();
            }
        });
        upgradeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppController.getLocalePreferenceHelper().writeLocaleBoolPreference(UpgradeAppActivity.this,AppController.isUpdateCancelledByUser,true);
                // splashActivity.startmain();
                finish();
            }
        });
        //((SplashActivity) mContext ).loadsplash();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(UpgradeAppActivity.this, "Please update the app to continue using.", Toast.LENGTH_SHORT).show();
        finish();
    }
}