package com.example.housie_app;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GameTicketAdapter extends RecyclerView.Adapter<GameTicketAdapter.GameTicketViewHolder> {

    private Context context;
    private List<Ticket> tickets;

    public GameTicketAdapter(Context context, List<Ticket> tickets) {
        this.context = context;
        this.tickets = tickets;
    }

    @NonNull
    @Override
    public GameTicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_game_ticket, parent, false);
        return new GameTicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameTicketViewHolder holder, int position) {
        Ticket ticket = tickets.get(position);
        holder.bind(ticket);
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    class GameTicketViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView ticketIdTextView;
        GridLayout ticketGridLayout;
        TextView winningsTextView;

        public GameTicketViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.ticketCardView);
            ticketIdTextView = itemView.findViewById(R.id.ticketIdTextView);
            ticketGridLayout = itemView.findViewById(R.id.ticketGridLayout);
            winningsTextView = itemView.findViewById(R.id.winningsTextView);
        }

        public void bind(final Ticket ticket) {
            ticketIdTextView.setText(ticket.getTicketId());
            
            // Clear previous grid
            ticketGridLayout.removeAllViews();
            
            // Create the ticket grid
            for (int i = 0; i < Ticket.ROWS; i++) {
                for (int j = 0; j < Ticket.COLS; j++) {
                    Integer number = ticket.getNumber(i, j);
                    
                    TextView cell = new TextView(context);
                    cell.setTextSize(14);
                    cell.setPadding(8, 8, 8, 8);
                    cell.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    
                    if (number != null) {
                        cell.setText(String.valueOf(number));
                        
                        if (ticket.isMarked(i, j)) {
                            cell.setBackgroundResource(R.drawable.cell_marked_background);
                            cell.setTextColor(Color.BLACK);
                        } else {
                            cell.setBackgroundResource(R.drawable.cell_background);
                        }
                    } else {
                        cell.setText("");
                        cell.setBackgroundResource(R.drawable.cell_empty_background);
                    }
                    
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.width = 0;
                    params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                    params.rowSpec = GridLayout.spec(i, 1f);
                    params.columnSpec = GridLayout.spec(j, 1f);
                    params.setMargins(2, 2, 2, 2);
                    
                    ticketGridLayout.addView(cell, params);
                }
            }
            
            // Display winning patterns if any
            List<String> winningPatterns = ticket.checkWinningPatterns();
            if (winningPatterns.isEmpty()) {
                winningsTextView.setVisibility(View.GONE);
            } else {
                winningsTextView.setVisibility(View.VISIBLE);
                StringBuilder sb = new StringBuilder("Winner: ");
                for (String pattern : winningPatterns) {
                    sb.append(pattern).append(", ");
                }
                sb.setLength(sb.length() - 2);  // Remove trailing comma and space
                winningsTextView.setText(sb.toString());
            }
        }
    }
} 