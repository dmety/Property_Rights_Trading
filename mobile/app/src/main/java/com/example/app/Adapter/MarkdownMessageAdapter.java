package com.example.app.Adapter;

import android.content.Context;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.components.Message;
import com.example.testforenv.R;

import io.noties.markwon.Markwon;
import java.util.List;

public class MarkdownMessageAdapter extends RecyclerView.Adapter<MarkdownMessageAdapter.MessageViewHolder> {
    private final List<Message> messageList;
    private final Markwon markwon;

    // 修改构造函数以接收Context
    public MarkdownMessageAdapter(Context context, List<Message> messageList) {
        this.messageList = messageList;
        this.markwon = Markwon.create(context);  // 使用传入的Context初始化Markwon
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(viewType == 0 ? R.layout.item_message_user : R.layout.item_message_ai,
                        parent, false);
        return new MessageViewHolder(view);
    }

//    @Override
//    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
//        Message message = messageList.get(position);
//        String content = message.getContent();
//        if (content != null) {
//            // 替换 <think> 和 </think> 为友好提示
//            content = content
//                    .replace("<think>", "")  // 添加换行和中文提示
//                    .replace("</think>", ""); // 保持格式清晰
//            // 可选：修复因替换导致的额外空行（按需调整）
//            content = content.trim().replaceAll("\n{3,}", "\n\n");
//        }
//        markwon.setMarkdown(holder.messageTextView, content);
//    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        String content = message.getContent();
        if (content != null) {
            // 使用正则表达式彻底删除 <think>...</think> 及其内部内容（包括换行）
            content = content.replaceAll("(?s)<think>.*?</think>", "");
            // 可选：清理因删除导致的额外空行
            content = content.trim().replaceAll("\n{3,}", "\n\n");
        }
        markwon.setMarkdown(holder.messageTextView, content);
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).isUserMessage() ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }
}
