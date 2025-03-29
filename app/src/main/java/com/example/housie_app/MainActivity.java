package com.example.housie_app;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
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
    
    public void showWinningPatterns(View view) {
        Spanned helpText = Html.fromHtml(
            "<h2>Winning Patterns</h2>" +
            "<p><b>Basic Patterns:</b></p>" +
            "<ul>" +
            "<li><b>Top Line</b>: All numbers in first row</li>" +
            "<li><b>Centre Line</b>: All numbers in second row</li>" +
            "<li><b>Bottom Line</b>: All numbers in third row</li>" +
            "<li><b>Four Corners</b>: The four corner numbers</li>" +
            "<li><b>Six Corners</b>: First and last number of each row</li>" +
            "<li><b>Full House</b>: All numbers on ticket</li>" +
            "</ul>" +
            
            "<p><b>Number Range Patterns:</b></p>" +
            "<ul>" +
            "<li><b>Breakfast</b>: All numbers 1-30</li>" +
            "<li><b>Lunch</b>: All numbers 31-60</li>" +
            "<li><b>Dinner</b>: All numbers 61-90</li>" +
            "<li><b>Straight Brunch</b>: All numbers 1-60</li>" +
            "<li><b>Reverse Brunch</b>: All numbers 31-90</li>" +
            "</ul>" +
            
            "<p><b>Position Patterns:</b></p>" +
            "<ul>" +
            "<li><b>Twins</b>: First two numbers in all rows</li>" +
            "<li><b>Reverse Twins</b>: Last two numbers in all rows</li>" +
            "<li><b>Triplets</b>: First three numbers in all rows</li>" +
            "<li><b>Reverse Triplets</b>: Last three numbers in all rows</li>" +
            "<li><b>Ladder</b>: First 1, 2, and 3 numbers in rows</li>" +
            "<li><b>Reverse Ladder</b>: Last 3, 2, and 1 numbers in rows</li>" +
            "<li><b>Lover's Lane</b>: First number in top and middle rows + all bottom row</li>" +
            "<li><b>Reverse Lover's Lane</b>: All top row + last number in middle and bottom rows</li>" +
            "<li><b>Tennis</b>: Middle number in top and bottom rows + all middle row</li>" +
            "<li><b>Stumps</b>: Middle three numbers of each row</li>" +
            "<li><b>Round the World</b>: All top/bottom rows + first/last number in middle row</li>" +
            "<li><b>Letter H</b>: First/last numbers in top/bottom rows + all middle row</li>" +
            "</ul>" +
            
            "<p><b>Quick Patterns:</b></p>" +
            "<ul>" +
            "<li><b>Quick-12</b>: Any 12 numbers</li>" +
            "<li><b>Quick-13</b>: Any 13 numbers</li>" +
            "<li><b>Quick-14</b>: Any 14 numbers</li>" +
            "</ul>" +
            
            "<p><b>Line Combinations:</b></p>" +
            "<ul>" +
            "<li><b>Top & Centre Line</b>: Both top and middle rows</li>" +
            "<li><b>Centre & Bottom Line</b>: Both middle and bottom rows</li>" +
            "<li><b>Top & Bottom Line</b>: Both top and bottom rows</li>" +
            "</ul>",
            Html.FROM_HTML_MODE_COMPACT
        );
        
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Housie Winning Patterns")
                .setMessage(helpText)
                .setPositiveButton("Got it!", null)
                .create();
        
        dialog.show();
    }
}