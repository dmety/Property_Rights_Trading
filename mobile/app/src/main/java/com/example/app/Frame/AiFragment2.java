//package com.example.app.Frame;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.app.Adapter.MessageAdapter;
//import com.example.app.Utils.NewHttpRequests;
//import com.example.app.components.Message;
//import com.example.testforenv.R;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class AiFragment2 extends Fragment {
////    private HttpRequests httpRequests;
//    private NewHttpRequests httpRequests;
//    ExecutorService executor = Executors.newSingleThreadExecutor();
//    private RecyclerView recyclerView;
//    private MessageAdapter adapter;
//    private List<Message> messageList;
//    private EditText inputMessage;
//    private Button sendButton;
//
//    public AiFragment2() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_agent, container, false);
////        httpRequests = new HttpRequests();
//        httpRequests = new NewHttpRequests();
//        recyclerView = view.findViewById(R.id.recyclerView);
//        inputMessage = view.findViewById(R.id.inputMessage);
//        sendButton = view.findViewById(R.id.sendButton);
//
//        messageList = new ArrayList<>();
//        adapter = new MessageAdapter(messageList);
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setAdapter(adapter);
//
//        sendButton.setOnClickListener(v -> {
//            String message = inputMessage.getText().toString().trim();
//            if (!message.isEmpty()) {
//                // 添加用户消息
//                messageList.add(new Message(message, true));
//                Log.d("Activity","到这了");
//                // 执行获取 AI 回复
//                httpRequests.getAiResponse(message, aiResponse -> {
//                    Log.d("AIResponse" , aiResponse);
//                    // 添加 AI 回复消息
//                    messageList.add(new Message(aiResponse, false));
//                    // 更新适配器和滚动到最新消息
//                    getActivity().runOnUiThread(() -> {
//                        adapter.notifyDataSetChanged();
//                        recyclerView.smoothScrollToPosition(messageList.size() - 1);
//                    });
//                });
//
//                adapter.notifyDataSetChanged();
//                recyclerView.smoothScrollToPosition(messageList.size() - 1);
//                inputMessage.setText("");
//            }
//        });
//
//        return view;
//    }
//}
