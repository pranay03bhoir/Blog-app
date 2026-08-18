package com.substring.blogapp.utils;

public class ReadingTimeUtils {

    private static final int WORDS_PER_MINUTE = 200;

    public static int calculateReadingMinutes(String content) {
        if (content == null || content.isBlank()) {
            return 1;
        }
        // Strip basic markdown/HTML tags if present
        String plainText = content.replaceAll("<[^>]*>", " ").replaceAll("[#*_`\\[\\]()]", " ");
        String[] words = plainText.trim().split("\\s+");
        int wordCount = words.length;
        int minutes = (int) Math.ceil((double) wordCount / WORDS_PER_MINUTE);
        return Math.max(1, minutes);
    }
}
