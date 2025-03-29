package com.example.housie_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize permission request launcher
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openScanner();
                    } else {
                        Toast.makeText(this, "Camera permission is required to scan tickets", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    public void openManualEntry(View view) {
        Intent intent = new Intent(this, ManualEntryActivity.class);
        startActivity(intent);
    }

    public void requestCameraPermission(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openScanner();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openScanner() {
        Intent intent = new Intent(this, ScannerActivity.class);
        startActivity(intent);
    }

    public void openTicketManagement(View view) {
        Intent intent = new Intent(this, TicketManagementActivity.class);
        startActivity(intent);
    }
}