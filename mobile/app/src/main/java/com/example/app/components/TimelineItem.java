package com.example.app.components; // 请替换为您的包名

public class TimelineItem {
    private String title;
    private String content;
    private String time;

    public TimelineItem(String title, String content, String time) {
        this.title = title;
        this.content = content;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }
}
