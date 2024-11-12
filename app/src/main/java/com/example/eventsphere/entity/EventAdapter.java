package com.example.eventsphere.entity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventsphere.R;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> eventList;
    private Context context;

    public EventAdapter(List<Event> eventList, Context context) {
        this.eventList = eventList;
        this.context = context;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.textViewName.setText(event.getName());
        holder.textViewDate.setText(event.getDate());
        holder.textViewLocation.setText(event.getLocation());
        holder.textViewDescription.setText(event.getDescription());

        if (event.getImageUri() != null) {
            Uri imageUri = Uri.parse(event.getImageUri());
            loadImage(holder.imageView, imageUri);
        } else {
            holder.imageView.setImageResource(R.drawable.placeholder); // Placeholder if no image URI is available
        }
    }

    private void loadImage(ImageView imageView, Uri imageUri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap); // Set the image in ImageView
                inputStream.close(); // Close the stream
            } else {
                imageView.setImageResource(R.drawable.placeholder); // Set placeholder if inputStream is null
            }
        } catch (IOException e) {
            e.printStackTrace();
            imageView.setImageResource(R.drawable.placeholder); // Set placeholder on error
        } catch (SecurityException e) {
            e.printStackTrace();
            imageView.setImageResource(R.drawable.placeholder); // Handle permission denial
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewDate, textViewLocation, textViewDescription;
        ImageView imageView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewLocation = itemView.findViewById(R.id.textViewLocation);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
