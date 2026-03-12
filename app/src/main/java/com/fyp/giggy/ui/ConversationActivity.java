// C21361681 – Michael Traynor
// ConversationActivity.java – In-app messaging between artist and venue for a booking
// Sprint 5: Messaging System

package com.fyp.giggy.ui;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.fyp.giggy.R;
import com.fyp.giggy.data.AppDatabase;
import com.fyp.giggy.data.Booking;
import com.fyp.giggy.data.Message;
import com.fyp.giggy.utils.NotificationHelper;
import com.fyp.giggy.utils.SessionManager;

import java.util.List;

public class ConversationActivity extends AppCompatActivity {

    private long bookingId;
    private long myUserId;
    private long otherUserId;
    private ListView listView;
    private EditText etMessage;
    private List<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        bookingId = getIntent().getLongExtra("bookingId", -1);
        if (bookingId == -1) { finish(); return; }

        SessionManager session = new SessionManager(this);
        myUserId = session.getUserId();

        listView  = findViewById(R.id.listMessages);
        etMessage = findViewById(R.id.etMessage);
        Button btnSend = findViewById(R.id.btnSend);
        Button btnBack = findViewById(R.id.btnBack);
        TextView tvTitle = findViewById(R.id.tvTitle);

        btnBack.setOnClickListener(v -> finish());

        // Load booking to get the other party's userId and set title
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Booking booking = db.bookingDao().getBookingById(bookingId);
            if (booking == null) { runOnUiThread(this::finish); return; }

            otherUserId = (myUserId == booking.artistUserId)
                    ? booking.venueUserId : booking.artistUserId;
            String otherName = (myUserId == booking.artistUserId)
                    ? booking.venueName : booking.artistName;

            // Mark existing messages as read
            db.messageDao().markBookingMessagesRead(bookingId, myUserId);

            runOnUiThread(() -> tvTitle.setText(otherName != null ? otherName : "Conversation"));

            loadMessages();
        }).start();

        btnSend.setOnClickListener(v -> sendMessage());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bookingId != -1) loadMessages();
    }

    private void loadMessages() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            messages = db.messageDao().getMessagesByBookingId(bookingId);
            db.messageDao().markBookingMessagesRead(bookingId, myUserId);

            runOnUiThread(() -> {
                String[] items = new String[messages.size()];
                for (int i = 0; i < messages.size(); i++) {
                    Message m = messages.get(i);
                    String prefix = (m.senderId == myUserId) ? "You: " : "Them: ";
                    items[i] = prefix + m.content;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_list_item_1, items);
                listView.setAdapter(adapter);
                // Scroll to bottom
                listView.post(() -> listView.setSelection(adapter.getCount() - 1));
            });
        }).start();
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        new Thread(() -> {
            Message msg = new Message();
            msg.bookingId  = bookingId;
            msg.senderId   = myUserId;
            msg.receiverId = otherUserId;
            msg.content    = text;
            msg.timestamp  = System.currentTimeMillis();
            msg.isRead     = false;

            AppDatabase.getInstance(this).messageDao().insertMessage(msg);

            // Fire a local notification for the receiver
            NotificationHelper.sendMessageNotification(this, otherUserId, text);

            runOnUiThread(() -> {
                etMessage.setText("");
                loadMessages();
            });
        }).start();
    }
}