package com.example.housie_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Manages Housie tickets persistence and operations
 */
public class TicketManager {
    private static final String TAG = "TicketManager";
    private static final String PREF_NAME = "housie_tickets";
    private static final String TICKETS_KEY = "tickets";
    
    private static TicketManager instance;
    private List<Ticket> tickets;
    private SharedPreferences preferences;
    private Gson gson;
    
    private TicketManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        loadTickets();
    }
    
    public static synchronized TicketManager getInstance(Context context) {
        if (instance == null) {
            instance = new TicketManager(context.getApplicationContext());
        }
        return instance;
    }
    
    private void loadTickets() {
        String ticketsJson = preferences.getString(TICKETS_KEY, null);
        if (ticketsJson != null) {
            Type type = new TypeToken<ArrayList<Ticket>>(){}.getType();
            tickets = gson.fromJson(ticketsJson, type);
        } else {
            tickets = new ArrayList<>();
        }
    }
    
    private void saveTickets() {
        String ticketsJson = gson.toJson(tickets);
        preferences.edit().putString(TICKETS_KEY, ticketsJson).apply();
    }
    
    public List<Ticket> getTickets() {
        return tickets;
    }
    
    public Ticket getTicket(String ticketId) {
        for (Ticket ticket : tickets) {
            if (ticket.getTicketId().equals(ticketId)) {
                return ticket;
            }
        }
        return null;
    }
    
    public Ticket createTicket() {
        String ticketId = "TICKET-" + UUID.randomUUID().toString().substring(0, 8);
        Ticket ticket = new Ticket(ticketId);
        tickets.add(ticket);
        saveTickets();
        return ticket;
    }
    
    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
        saveTickets();
    }
    
    public void updateTicket(Ticket ticket) {
        for (int i = 0; i < tickets.size(); i++) {
            if (tickets.get(i).getTicketId().equals(ticket.getTicketId())) {
                tickets.set(i, ticket);
                saveTickets();
                return;
            }
        }
    }
    
    public void deleteTicket(String ticketId) {
        for (int i = 0; i < tickets.size(); i++) {
            if (tickets.get(i).getTicketId().equals(ticketId)) {
                tickets.remove(i);
                saveTickets();
                return;
            }
        }
    }
    
    public void deleteAllTickets() {
        tickets.clear();
        saveTickets();
    }
    
    public void markNumberInAllTickets(int number) {
        for (Ticket ticket : tickets) {
            ticket.markNumber(number);
        }
        saveTickets();
    }
    
    public List<Ticket> getWinningTickets() {
        List<Ticket> winningTickets = new ArrayList<>();
        
        for (Ticket ticket : tickets) {
            if (!ticket.checkWinningPatterns().isEmpty()) {
                winningTickets.add(ticket);
            }
        }
        
        return winningTickets;
    }
} 