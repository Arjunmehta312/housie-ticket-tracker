package com.example.housie_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private Context context;
    private List<Ticket> tickets;
    private OnTicketClickListener listener;

    public interface OnTicketClickListener {
        void onTicketClick(Ticket ticket);
        void onTicketDelete(Ticket ticket);
    }

    public TicketAdapter(Context context, List<Ticket> tickets, OnTicketClickListener listener) {
        this.context = context;
        this.tickets = tickets;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = tickets.get(position);
        holder.bind(ticket);
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    class TicketViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView ticketIdTextView;
        GridLayout ticketGridLayout;
        Button deleteButton;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.ticketCardView);
            ticketIdTextView = itemView.findViewById(R.id.ticketIdTextView);
            ticketGridLayout = itemView.findViewById(R.id.ticketGridLayout);
            deleteButton = itemView.findViewById(R.id.deleteTicketButton);
        }

        public void bind(final Ticket ticket) {
            ticketIdTextView.setText(ticket.getTicketId());
            
            // Clear previous grid
            ticketGridLayout.removeAllViews();
            
            // Create a preview of the ticket grid
            for (int i = 0; i < Ticket.ROWS; i++) {
                for (int j = 0; j < Ticket.COLS; j++) {
                    Integer number = ticket.getNumber(i, j);
                    
                    TextView cell = new TextView(context);
                    cell.setTextSize(12);
                    cell.setPadding(8, 8, 8, 8);
                    
                    if (number != null) {
                        cell.setText(String.valueOf(number));
                        if (ticket.isMarked(i, j)) {
                            cell.setBackgroundResource(R.drawable.cell_marked_background);
                        } else {
                            cell.setBackgroundResource(R.drawable.cell_background);
                        }
                    } else {
                        cell.setText("");
                        cell.setBackgroundResource(R.drawable.cell_empty_background);
                    }
                    
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.width = GridLayout.LayoutParams.WRAP_CONTENT;
                    params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                    params.rowSpec = GridLayout.spec(i);
                    params.columnSpec = GridLayout.spec(j);
                    params.setMargins(2, 2, 2, 2);
                    
                    ticketGridLayout.addView(cell, params);
                }
            }
            
            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTicketClick(ticket);
                }
            });
            
            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTicketDelete(ticket);
                }
            });
        }
    }
} 