package com.harvard;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.harvard.utils.Version.VersionChecker;

public class UpgradeAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade_app);

        TextView version = findViewById(R.id.version);
        AppCompatTextView upgrade = findViewById(R.id.upgrade);
        upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(VersionChecker.PLAY_STORE_URL)));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(UpgradeAppActivity.this, "Please update the app to continue using.", Toast.LENGTH_SHORT).show();
        finish();
    }
}
