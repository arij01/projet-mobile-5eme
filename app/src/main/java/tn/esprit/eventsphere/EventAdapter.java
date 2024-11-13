package tn.esprit.eventsphere;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tn.esprit.eventsphere.entity.Event;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    public interface EventActionListener {
        void onEditEvent(Event event, int position);
        void onDeleteEvent(Event event, int position);
    }

    private List<Event> eventList;
    private EventActionListener listener;

    public EventAdapter(List<Event> eventList, EventActionListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        // Affichage des détails de l'événement
        holder.eventNameTextView.setText(event.getName());
        holder.eventDateTextView.setText(event.getDate());
        holder.eventLocationTextView.setText(event.getLocation());
        holder.eventDescriptionTextView.setText(event.getDescription());

        // Configuration du bouton d'édition
        holder.editButton.setOnClickListener(v -> listener.onEditEvent(event, position));

        // Configuration du bouton de suppression
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteEvent(event, position));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView, eventDateTextView, eventLocationTextView, eventDescriptionTextView;
        Button editButton, deleteButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            // Liaison des vues avec les IDs définis dans event_item.xml
            eventNameTextView = itemView.findViewById(R.id.eventNameTextView);
            eventDateTextView = itemView.findViewById(R.id.eventDateTextView);
            eventLocationTextView = itemView.findViewById(R.id.eventLocationTextView);
            eventDescriptionTextView = itemView.findViewById(R.id.eventDescriptionTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
