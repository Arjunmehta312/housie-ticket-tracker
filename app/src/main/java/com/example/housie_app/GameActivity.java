package com.example.housie_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameActivity extends AppCompatActivity {

    private EditText numberInputEditText;
    private Button markNumberButton;
    private TextView calledNumbersTextView;
    private RecyclerView ticketsRecyclerView;
    private TicketManager ticketManager;
    private GameTicketAdapter adapter;
    
    private Set<Integer> calledNumbers = new HashSet<>();
    private List<String> wonPatterns = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        
        ticketManager = TicketManager.getInstance(this);
        
        numberInputEditText = findViewById(R.id.numberInputEditText);
        markNumberButton = findViewById(R.id.markNumberButton);
        calledNumbersTextView = findViewById(R.id.calledNumbersTextView);
        ticketsRecyclerView = findViewById(R.id.ticketsRecyclerView);
        
        ticketsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GameTicketAdapter(this, ticketManager.getTickets());
        ticketsRecyclerView.setAdapter(adapter);
        
        markNumberButton.setOnClickListener(v -> markCalledNumber());
    }
    
    private void markCalledNumber() {
        String input = numberInputEditText.getText().toString().trim();
        
        if (input.isEmpty()) {
            Toast.makeText(this, "Please enter a number", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            int number = Integer.parseInt(input);
            
            if (number < 1 || number > 90) {
                Toast.makeText(this, "Please enter a number between 1 and 90", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (calledNumbers.contains(number)) {
                Toast.makeText(this, "Number " + number + " has already been called", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Mark the number in all tickets
            ticketManager.markNumberInAllTickets(number);
            
            // Update called numbers display
            calledNumbers.add(number);
            updateCalledNumbersDisplay();
            
            // Update tickets display
            adapter.notifyDataSetChanged();
            
            // Clear input field
            numberInputEditText.setText("");
            
            // Check for winning patterns
            checkWinningPatterns();
            
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void updateCalledNumbersDisplay() {
        StringBuilder sb = new StringBuilder("Called Numbers: ");
        
        for (Integer number : calledNumbers) {
            sb.append(number).append(", ");
        }
        
        // Remove the trailing comma and space
        if (!calledNumbers.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        
        calledNumbersTextView.setText(sb.toString());
    }
    
    private void checkWinningPatterns() {
        List<Ticket> tickets = ticketManager.getTickets();
        
        for (int i = 0; i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);
            List<String> patterns = ticket.checkWinningPatterns();
            
            for (String pattern : patterns) {
                String winKey = ticket.getTicketId() + "-" + pattern;
                
                if (!wonPatterns.contains(winKey)) {
                    wonPatterns.add(winKey);
                    showWinningPatternDialog(ticket.getTicketId(), pattern, i);
                }
            }
        }
    }
    
    private void showWinningPatternDialog(String ticketId, String pattern, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Winning Pattern!")
                .setMessage("Ticket " + ticketId + " has won: " + pattern)
                .setPositiveButton("View Ticket", (dialog, which) -> {
                    ticketsRecyclerView.scrollToPosition(position);
                })
                .setNegativeButton("Continue", null)
                .show();
    }
} 