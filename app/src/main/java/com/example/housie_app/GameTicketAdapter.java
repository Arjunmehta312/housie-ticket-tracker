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
            
            // Check winning patterns
            List<String> winningPatterns = ticket.checkWinningPatterns();
            boolean hasWinnings = !winningPatterns.isEmpty();
            
            // If this ticket has winnings, make the card stand out
            if (hasWinnings) {
                cardView.setCardBackgroundColor(Color.rgb(255, 248, 225)); // Light yellow background
                cardView.setCardElevation(16f); // Increase elevation to make it stand out
            } else {
                cardView.setCardBackgroundColor(Color.WHITE);
                cardView.setCardElevation(4f);
            }
            
            // Create the ticket grid
            for (int i = 0; i < Ticket.ROWS; i++) {
                for (int j = 0; j < Ticket.COLS; j++) {
                    Integer number = ticket.getNumber(i, j);
                    
                    TextView cell = new TextView(context);
                    cell.setTextSize(16);
                    cell.setPadding(8, 8, 8, 8);
                    cell.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    
                    if (number != null) {
                        cell.setText(String.valueOf(number));
                        
                        if (ticket.isMarked(i, j)) {
                            // Use a brighter background for marked numbers in winning tickets
                            if (hasWinnings) {
                                cell.setBackgroundColor(Color.rgb(105, 240, 174)); // Bright green
                                cell.setTextColor(Color.BLACK);
                                cell.setTextSize(18); // Make marked numbers larger
                                cell.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
                            } else {
                                cell.setBackgroundResource(R.drawable.cell_marked_background);
                                cell.setTextColor(Color.BLACK);
                            }
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
            if (hasWinnings) {
                winningsTextView.setVisibility(View.VISIBLE);
                winningsTextView.setTextSize(18);
                winningsTextView.setTextColor(Color.rgb(0, 150, 136)); // Teal color
                
                StringBuilder sb = new StringBuilder("🎖️ WINNER: ");
                for (String pattern : winningPatterns) {
                    sb.append(pattern).append(", ");
                }
                sb.setLength(sb.length() - 2);  // Remove trailing comma and space
                winningsTextView.setText(sb.toString());
                
                // Add pulsating animation
                winningsTextView.animate()
                        .scaleX(1.1f)
                        .scaleY(1.1f)
                        .setDuration(500)
                        .withEndAction(() -> {
                            winningsTextView.animate()
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .setDuration(500)
                                    .start();
                        })
                        .start();
                
            } else {
                winningsTextView.setVisibility(View.GONE);
            }
        }
    }
} 