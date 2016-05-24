package com.example.ulim.stock6;

/**
 * Created by ulim on 5/3/2016.
 */
public class NewsEntry {
    private final String title;
    private final String content;
    private final String publisher;
    private final String date;
    private final String url;


    public NewsEntry(final String title, final String content,
                     final String publisher, final String date, final String url) {
        this.title = title;
        this.content = content;
        this.publisher = publisher;
        this.date = date;
        this.url = url;
    }
    /**
     * @return Title of news entry
     */
    public String getNewsTitle() {
        return title;
    }

    /**
     * @return Value of news entry
     */
    public String getNewsContent() { return content; }

    /**
     * @return Value of news entry
     */
    public String getNewsPublisher() { return publisher; }

    /**
     * @return Value of news entry
     */
    public String getNewsDate() { return date; }

    public String getNewsUrl() { return url; }
}
