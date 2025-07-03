package org.voh.postprocess;

import org.voh.util.TitlecaseUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlainInputPostProcessor {
    public static String parseInput(String rawText) {
        List<String> lines = Arrays.stream(rawText.split("\\r?\\n")).toList();

        lines = parseHeaders(lines);
        lines = parseLists(lines);

        return String.join(" ", lines);
    }

    private static List<String> parseHeaders(List<String> fullText) {
        return fullText.stream()
                .map(String::trim)
                .map(t -> TitlecaseUtil.isTitleCase(t)
                        ? "<h2>" + escapeHtml(t) + "</h2>"
                        : escapeHtml(t))
                .toList();
    }

    private static List<String> parseLists(List<String> lines) {
        List<String> currentList = new ArrayList<>();
        boolean inList = false;
        List<String> result = new ArrayList<>();

        Pattern bulletPattern = Pattern.compile("^[•\\-*+]\\s+(.+)$");

        for (String line : lines) {
            Matcher matcher = bulletPattern.matcher(line);

            if (matcher.matches()) {
                if (!inList) {
                    currentList.clear();
                    currentList.add("<ul>");
                    inList = true;
                }
                else {
                    currentList.add("</li>");
                }
                currentList.add("<li>");
                currentList.add(matcher.group(1).trim());
            }
            else if (line.startsWith("<h")) {
                inList = endList(line, inList, currentList, result);
                if (!result.getLast().equals(line)) {
                    result.add(line);
                }
            }
            else if (inList && !line.isBlank()) {
                currentList.add(" " + line);
            }
            else {
                inList = endList(line, inList, currentList, result);
            }
        }

        inList = endList("", inList, currentList, result);
        return result;
    }

    private static List<String> parseParagraphs(List<String> lines) {
        List<String> result = new ArrayList<>();
        List<String> paragraph = new ArrayList<>();

        for (String raw : lines) {
            String trimmed = raw.trim();
            if (trimmed.startsWith("<h2>") || trimmed.startsWith("<ul>")) {
                if (!paragraph.isEmpty()) {
                    result.add(createParagraph(paragraph));
                    paragraph.clear();
                }
                result.add(trimmed);
            } else {
                paragraph.add(trimmed);
            }
        }

        if (!paragraph.isEmpty()) {
            result.add(createParagraph(paragraph));
        }
        return result;
    }

    private static String createParagraph(List<String> lines) {
        return "<p>" + String.join(" ", lines) + "</p>";
    }

    private static boolean endList(String line, boolean inList, List<String> currentList, List<String> result) {
        if (inList) {
            currentList.add("</li></ul>");
            result.add(String.join("", currentList));
            inList = false;
        }
        else {
            if (!line.isBlank())
                result.add(line);
        }
        return inList;
    }

    private static String escapeHtml(String text) {
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
