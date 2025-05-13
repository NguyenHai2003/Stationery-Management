package com.example.office_management.fragment.chatbot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.office_management.R;

import java.util.ArrayList;
import java.util.List;

public class ChatBotFragment extends Fragment {

    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;

    public ChatBotFragment() {
        // Required empty public constructor
    }

    public static ChatBotFragment newInstance() {
        return new ChatBotFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_bot, container, false);

        // Initialize views
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        messageEditText = view.findViewById(R.id.messageEditText);
        sendButton = view.findViewById(R.id.sendButton);

        // Setup RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);

        chatAdapter = new ChatAdapter(messageList);
        chatRecyclerView.setAdapter(chatAdapter);

        // Setup send button click listener
        sendButton.setOnClickListener(v -> sendMessage());

        // Setup enter key listener for EditText
        messageEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });

        // Add welcome message
        addBotMessage("Hello! How can I assist you today?");

        return view;
    }

    private void sendMessage() {
        String message = messageEditText.getText().toString().trim();
        if (!message.isEmpty()) {
            // Add user message
            addUserMessage(message);

            // Generate bot response
            generateBotResponse(message);

            // Clear input
            messageEditText.setText("");

            // Scroll to bottom
            chatRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
        }
    }

    private void addUserMessage(String message) {
        messageList.add(new ChatMessage(message, true));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
    }

    private void addBotMessage(String message) {
        messageList.add(new ChatMessage(message, false));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
    }

    private void generateBotResponse(String userMessage) {
        // Simple bot response logic (can be enhanced with AI or predefined responses)
        String response;
        userMessage = userMessage.toLowerCase();

        if (userMessage.contains("hello") || userMessage.contains("hi")) {
            response = "Hi there! How can I help you?";
        } else if (userMessage.contains("how are you")) {
            response = "I'm doing great, thanks for asking! How about you?";
        } else if (userMessage.contains("bye")) {
            response = "Goodbye! Have a great day!";
        } else {
            response = "Interesting! Could you tell me more?";
        }

        addBotMessage(response);
    }
}

// Message model class
class ChatMessage {
    private String message;
    private boolean isUser;

    public ChatMessage(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }
}

// RecyclerView Adapter
class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private List<ChatMessage> messageList;

    public ChatAdapter(List<ChatMessage> messageList) {
        this.messageList = messageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(viewType == 0 ? R.layout.item_message_user : R.layout.item_message_bot,
                        parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        holder.messageText.setText(message.getMessage());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).isUser() ? 0 : 1;
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
        }
    }
}