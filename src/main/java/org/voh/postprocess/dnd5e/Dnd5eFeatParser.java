package org.voh.postprocess.dnd5e;

import org.voh.domain.dnd5e.Feat5e;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dnd5eFeatParser {

    public static final String PREREQ_START = "prerequisites:";

    public static List<Feat5e> parseFeats(String html) {
        List<Feat5e> feats = new ArrayList<>();
        Matcher matcher = Pattern.compile("<h2>(.+?)</h2>", Pattern.CASE_INSENSITIVE).matcher(html);

        List<Integer> indices = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        while (matcher.find()) {
            indices.add(matcher.start());
            titles.add(matcher.group(1).trim());
        }

        for (int i = 0; i < indices.size(); i++) {
            int end = (i + 1 < indices.size()) ? indices.get(i + 1) : html.length();
            feats.add(parseSingleFeat(titles.get(i), html.substring(indices.get(i), end)));
        }

        return feats;
    }

    private static Feat5e parseSingleFeat(String name, String block) {
        int prereqStart = block.toLowerCase().indexOf(PREREQ_START);
        if (prereqStart == -1)
            return new Feat5e(name, "", block.trim());

        String after = block.substring(prereqStart);
        Matcher matcher = Pattern.compile("<i>(.*?)</i>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(after);

        StringBuilder prereq = new StringBuilder();
        int lastEnd = -1;

        while (matcher.find()) {
            String text = matcher.group(1).replaceAll("\\s+", " ").trim();
            if (text.toLowerCase().startsWith(PREREQ_START))
                text = text.substring(PREREQ_START.length()).trim();
            if (!prereq.isEmpty())
                prereq.append(" ");

            prereq.append(text);
            lastEnd = matcher.end();
        }

        String body = after.substring(lastEnd).trim().replaceAll("\n", "");
        if (!body.startsWith("<p>"))
            body = "<p>" + body;

        return new Feat5e(name, prereq.toString(), body);
    }
}
