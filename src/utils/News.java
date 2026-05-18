package utils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class News implements Serializable {
    private static final long serialVersionUID = 1L;
    private String newsId;
    private String title;
    private String content;
    private Date date;

    public News(String newsId, String title, String content) {
        this.newsId = newsId;
        this.title = title;
        this.content = content;
        this.date = new Date();
    }

    public String getTitle()    { return title; }

    public static void printAll(List<News> allNews) {
        System.out.println("\n=== UNIVERSITY NEWS ===");
        if (allNews.isEmpty()) {
            System.out.println("No news available.");
            return;
        }
        for (News news : allNews) {
            System.out.println(news);
        }
    }

    @Override
    public String toString() {
        return "[NEWS] " + title + " (" + date + "): " + content;
    }
}
