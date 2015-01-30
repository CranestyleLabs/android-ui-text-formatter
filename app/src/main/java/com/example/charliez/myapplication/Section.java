package com.example.charliez.myapplication;

public class Section {
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public int getWordCount() {
        if (contents == null) {
            return 0;
        }
        else {
            // TODO: Need to calculate word length without HTML markup
            return contents.length() / 6;
        }
    }

    private String title;
    private String contents;
}
