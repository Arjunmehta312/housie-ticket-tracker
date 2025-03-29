package com.example.housie_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TicketManagementActivity extends AppCompatActivity implements TicketAdapter.OnTicketClickListener {

    private RecyclerView recyclerView;
    private TicketAdapter adapter;
    private TicketManager ticketManager;
    private TextView emptyView;
    private Button addTicketButton;
    private Button startGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_management);

        ticketManager = TicketManager.getInstance(this);

        recyclerView = findViewById(R.id.ticketsRecyclerView);
        emptyView = findViewById(R.id.emptyTicketsView);
        addTicketButton = findViewById(R.id.addTicketButton);
        startGameButton = findViewById(R.id.startGameButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        addTicketButton.setOnClickListener(v -> {
            Intent intent = new Intent(TicketManagementActivity.this, ManualEntryActivity.class);
            startActivity(intent);
        });
        
        startGameButton.setOnClickListener(v -> {
            if (ticketManager.getTickets().size() > 0) {
                Intent intent = new Intent(TicketManagementActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTicketsList();
    }

    private void updateTicketsList() {
        List<Ticket> tickets = ticketManager.getTickets();
        
        if (tickets.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            startGameButton.setEnabled(false);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            startGameButton.setEnabled(true);
            
            adapter = new TicketAdapter(this, tickets, this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onTicketClick(Ticket ticket) {
        // Open ticket editing
        Intent intent = new Intent(this, ManualEntryActivity.class);
        intent.putExtra("ticket_id", ticket.getTicketId());
        startActivity(intent);
    }

    @Override
    public void onTicketDelete(Ticket ticket) {
        ticketManager.deleteTicket(ticket.getTicketId());
        updateTicketsList();
    }
} 