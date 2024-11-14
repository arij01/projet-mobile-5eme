package tn.esprit.eventsphere;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;
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

        // Display event details
        holder.eventNameTextView.setText(event.getName());
        holder.eventDateTextView.setText(event.getDate());
        holder.eventLocationTextView.setText(event.getLocation());
        holder.eventDescriptionTextView.setText(event.getDescription());

        // Display the event image if available
        if (event.getImage() != null && event.getImage().length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(event.getImage(), 0, event.getImage().length);
            holder.eventImageView.setImageBitmap(bitmap);
        } else {
            holder.eventImageView.setImageResource(R.drawable.image);  // Default image if no image is provided
        }

        // Generate QR code for the event URL or other information
        Bitmap qrCodeBitmap = generateQRCode(event.getName());  // Use event name as QR content or modify it as needed
        holder.qrCodeImageView.setImageBitmap(qrCodeBitmap);

        // Edit button setup
        holder.editButton.setOnClickListener(v -> listener.onEditEvent(event, position));

        // Delete button setup
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteEvent(event, position));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    // Method to generate QR code bitmap
    private Bitmap generateQRCode(String content) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Hashtable<EncodeHintType, String> hintMap = new Hashtable<>();
            hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            Bitmap bitmap = toBitmap(qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 200, 200, hintMap));
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Convert a Matrix to Bitmap
    private Bitmap toBitmap(com.google.zxing.common.BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return bitmap;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView, eventDateTextView, eventLocationTextView, eventDescriptionTextView;
        Button editButton, deleteButton;
        ImageView eventImageView, qrCodeImageView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            // Bind views
            eventNameTextView = itemView.findViewById(R.id.eventNameTextView);
            eventDateTextView = itemView.findViewById(R.id.eventDateTextView);
            eventLocationTextView = itemView.findViewById(R.id.eventLocationTextView);
            eventDescriptionTextView = itemView.findViewById(R.id.eventDescriptionTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            eventImageView = itemView.findViewById(R.id.eventImageView);
            qrCodeImageView = itemView.findViewById(R.id.qrCodeImageView);  // Reference to the QR code ImageView
        }
    }
}
