package com.example.housie_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ManualEntryActivity extends AppCompatActivity {

    private GridLayout ticketGridLayout;
    private TextView ticketIdTextView;
    private Button saveButton;
    private TicketManager ticketManager;
    private Ticket currentTicket;
    private EditText[][] cellInputs;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);
        
        ticketManager = TicketManager.getInstance(this);
        ticketGridLayout = findViewById(R.id.ticketGridLayout);
        ticketIdTextView = findViewById(R.id.ticketIdTextView);
        saveButton = findViewById(R.id.saveButton);
        
        // Check if we're editing an existing ticket
        String ticketId = getIntent().getStringExtra("ticket_id");
        if (ticketId != null) {
            currentTicket = ticketManager.getTicket(ticketId);
            if (currentTicket == null) {
                // Fallback, create new ticket
                currentTicket = ticketManager.createTicket();
            }
        } else {
            // Create a new ticket
            currentTicket = ticketManager.createTicket();
        }
        
        ticketIdTextView.setText(currentTicket.getTicketId());
        setupTicketGrid();
        
        saveButton.setOnClickListener(v -> saveTicket());
    }
    
    private void setupTicketGrid() {
        cellInputs = new EditText[Ticket.ROWS][Ticket.COLS];
        
        // Clear any existing views
        ticketGridLayout.removeAllViews();
        
        for (int i = 0; i < Ticket.ROWS; i++) {
            for (int j = 0; j < Ticket.COLS; j++) {
                EditText cell = new EditText(this);
                cell.setTextSize(14);
                cell.setPadding(8, 8, 8, 8);
                cell.setBackgroundResource(R.drawable.cell_background);
                cell.setHint("");
                cell.setMaxLines(1);
                
                // If there's a value, set it
                Integer value = currentTicket.getNumber(i, j);
                if (value != null) {
                    cell.setText(String.valueOf(value));
                }
                
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                params.rowSpec = GridLayout.spec(i, 1f);
                params.columnSpec = GridLayout.spec(j, 1f);
                params.setMargins(2, 2, 2, 2);
                
                ticketGridLayout.addView(cell, params);
                cellInputs[i][j] = cell;
            }
        }
    }
    
    private void saveTicket() {
        boolean hasError = false;
        
        // Validate and save input values
        for (int i = 0; i < Ticket.ROWS; i++) {
            for (int j = 0; j < Ticket.COLS; j++) {
                String text = cellInputs[i][j].getText().toString().trim();
                
                if (text.isEmpty()) {
                    // Empty cell
                    currentTicket.setNumber(i, j, null);
                } else {
                    try {
                        int value = Integer.parseInt(text);
                        if (value > 0 && value <= 90) {  // Valid range for housie
                            currentTicket.setNumber(i, j, value);
                        } else {
                            hasError = true;
                            cellInputs[i][j].setError("Invalid number");
                        }
                    } catch (NumberFormatException e) {
                        hasError = true;
                        cellInputs[i][j].setError("Invalid input");
                    }
                }
            }
        }
        
        if (hasError) {
            Toast.makeText(this, "Please fix the errors before saving", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Save the ticket
        ticketManager.updateTicket(currentTicket);
        Toast.makeText(this, "Ticket saved", Toast.LENGTH_SHORT).show();
        finish();
    }
    
    @Override
    public void onBackPressed() {
        // Show confirmation dialog if changes were made
        new AlertDialog.Builder(this)
                .setTitle("Discard Changes?")
                .setMessage("Do you want to discard your changes?")
                .setPositiveButton("Discard", (dialog, which) -> super.onBackPressed())
                .setNegativeButton("Keep Editing", null)
                .show();
    }
} 