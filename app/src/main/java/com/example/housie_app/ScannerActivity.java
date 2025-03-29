package com.example.housie_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScannerActivity extends AppCompatActivity {
    private static final String TAG = "ScannerActivity";
    
    private PreviewView previewView;
    private Button captureButton;
    
    private ExecutorService cameraExecutor;
    private ImageCapture imageCapture;
    private TextRecognizer textRecognizer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        
        previewView = findViewById(R.id.previewView);
        captureButton = findViewById(R.id.captureButton);
        
        // Initialize the ML Kit text recognizer
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        
        // Set up the camera executor
        cameraExecutor = Executors.newSingleThreadExecutor();
        
        // Start the camera
        startCamera();
        
        captureButton.setOnClickListener(v -> captureImage());
    }
    
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = 
                ProcessCameraProvider.getInstance(this);
        
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                
                // Set up the preview use case
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                
                // Set up the image capture use case
                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();
                
                // Select the back camera
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();
                
                // Unbind any bound use cases before rebinding
                cameraProvider.unbindAll();
                
                // Bind use cases to camera
                Camera camera = cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture);
                
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera: " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(this));
    }
    
    private void captureImage() {
        if (imageCapture == null) {
            Toast.makeText(this, "Camera not initialized", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show a toast to indicate capturing
        Toast.makeText(this, "Capturing image...", Toast.LENGTH_SHORT).show();
        
        // Take a picture
        imageCapture.takePicture(
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        // Process the captured image
                        processImage(image);
                    }
                    
                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Image capture failed: " + exception.getMessage());
                        Toast.makeText(ScannerActivity.this, 
                                "Failed to capture image", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void processImage(ImageProxy image) {
        InputImage inputImage = InputImage.fromMediaImage(
                image.getImage(), image.getImageInfo().getRotationDegrees());
        
        textRecognizer.process(inputImage)
                .addOnSuccessListener(text -> {
                    Ticket ticket = parseTicketFromText(text);
                    if (ticket != null) {
                        // Save the ticket and open it in the manual entry activity for verification
                        TicketManager.getInstance(this).addTicket(ticket);
                        
                        Intent intent = new Intent(this, ManualEntryActivity.class);
                        intent.putExtra("ticket_id", ticket.getTicketId());
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Could not detect a valid ticket. Try again or use manual entry.", 
                                Toast.LENGTH_LONG).show();
                    }
                    image.close();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Text recognition failed: " + e.getMessage());
                    Toast.makeText(this, "Text recognition failed", Toast.LENGTH_SHORT).show();
                    image.close();
                });
    }
    
    private Ticket parseTicketFromText(Text text) {
        // Create a new ticket
        Ticket ticket = new Ticket("TICKET-SCAN-" + System.currentTimeMillis());
        
        // Regular expression to match numbers (1-90)
        Pattern pattern = Pattern.compile("\\b([1-9]|[1-8][0-9]|90)\\b");
        
        // Process each text block
        List<Integer> numbers = new ArrayList<>();
        for (Text.TextBlock block : text.getTextBlocks()) {
            for (Text.Line line : block.getLines()) {
                String lineText = line.getText();
                Matcher matcher = pattern.matcher(lineText);
                
                while (matcher.find()) {
                    int number = Integer.parseInt(matcher.group());
                    numbers.add(number);
                }
            }
        }
        
        // If we have at least some numbers, consider it a valid ticket
        if (numbers.size() >= 5) {  // A housie ticket row has 5 numbers
            // Place numbers in the ticket grid - very simple algorithm for now
            // In a real app, you would need a more sophisticated algorithm to detect the exact layout
            int currentRow = 0;
            int currentCol = 0;
            
            for (Integer number : numbers) {
                // Skip if we've filled the ticket
                if (currentRow >= Ticket.ROWS) break;
                
                // Place the number
                ticket.setNumber(currentRow, currentCol, number);
                
                // Move to the next position
                currentCol++;
                if (currentCol >= Ticket.COLS) {
                    currentCol = 0;
                    currentRow++;
                }
            }
            
            return ticket;
        }
        
        return null;  // Could not extract a valid ticket
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
} 