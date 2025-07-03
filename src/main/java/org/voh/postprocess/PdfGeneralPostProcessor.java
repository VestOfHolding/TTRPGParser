package org.voh.postprocess;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfGeneralPostProcessor {

    public static String fullProcess(String html) {
        html = cleanHtml(html);
        html = convertBulletsToLists(html);
        html = wrapConsecutiveListItems(html);
        html = removeRedundantStyleTags(html);
        html = mergeConsecutiveHeaders(html);

        return html;
    }

    /**
     * Removes empty HTML elements and collapses multiple blank lines.
     *
     * @param html the raw HTML
     * @return cleaned HTML with no empty tags and no consecutive blank lines
     */
    public static String cleanHtml(String html) {
        // 1) Remove empty tags like <tag>   </tag>, but keep the inner whitespace
        //    group(1) = tag name, group(2) = the whitespace inside
        String emptyTagPattern = "(?i)(?s)<([a-z][a-z0-9]*)\\b[^>]*>([\\s]*)</\\1>";
        String cleaned = html;
        String prev;
        do {
            prev    = cleaned;
            cleaned = cleaned.replaceAll(emptyTagPattern, "$2");
        } while (!cleaned.equals(prev));

        // 2) Collapse CRLF to LF, then two-or-more LFs into one
        cleaned = cleaned.replaceAll("\\r\\n", "\n")
                .replaceAll("\\n{2,}", "\n");

        // 3) Remove soft-hyphens splitting words at end-of-line
        cleaned = cleaned.replaceAll("(?m)(?<=\\p{L})-\\s*\\r?\\n\\s*(?=\\p{L})", "");

        return normalizeParagraphNewlines(cleaned);
    }

    private static String normalizeParagraphNewlines(String cleaned) {
        // 4) Normalize newlines *inside* each <p>…</p>
        Pattern para = Pattern.compile("(?s)<p>(.*?)</p>");
        Matcher m = para.matcher(cleaned);
        StringBuilder sb = new StringBuilder();

        while (m.find()) {
            String block = m.group(1);

            // a) pull off one leading newline if present
            String lead = "";
            if (block.startsWith("\n")) {
                lead = "\n";
                block = block.substring(1);
            }

            // b) pull off one trailing newline if present
            String tail = "";
            if (block.endsWith("\n")) {
                tail = "\n";
                block = block.substring(0, block.length() - 1);
            }

            // c) replace all remaining newlines with spaces
            String body = block.replaceAll("\\r?\\n", " ");

            // d) rebuild the <p> block
            String replacement = "<p>" + lead + body + tail + "</p>";
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(sb);

        return sb.toString().replaceAll(" {2}", " ");
    }

    /**
     * Replaces any <p>…</p> that contains &#8226;&#9; with a sequence
     * of individual <li>…</li> elements (no surrounding <ul>).
     */
    public static String convertBulletsToLists(String html) {
        // (?s) dot matches newline so we can capture multi‐line paragraphs
        // group(1) = the entire bullet paragraph contents
        // group(2) = an optional following paragraph's text (starting with lowercase)
        Pattern p = Pattern.compile("(?s)<p>(.*?)</p>\\s*(?:<p>([a-z].*?)</p>)?");
        Matcher m = p.matcher(html);
        StringBuilder sb = new StringBuilder();

        while (m.find()) {
            String bulletBlock = m.group(1);
            String continuation = m.group(2);  // may be null

            if (bulletBlock.contains("• ")) {
                // split into individual items
                String[] parts = bulletBlock.split("• ");
                StringBuilder rep = new StringBuilder();

                for (String part : parts) {
                    String item = part.trim();
                    if (item.isEmpty()) continue;
                    rep.append("<li>").append(item).append("</li>");
                }

                // if there was a continuation para, shove it into the last <li>
                if (continuation != null) {
                    String cont = continuation.trim();
                    // find the last </li> and insert before it
                    int idx = rep.lastIndexOf("</li>");
                    if (idx != -1) {
                        rep.insert(idx, " " + cont);
                    }
                }

                // replace the entire matched block (bullet para + optional cont para)
                m.appendReplacement(sb, Matcher.quoteReplacement(rep.toString()));
            } else {
                // no bullets here, leave the paragraph (and any following one) alone
                m.appendReplacement(sb, Matcher.quoteReplacement(m.group(0)));
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * Finds any sequence of two or more adjacent <li>…</li> blocks
     * and wraps them in a single <ul>…</ul>.
     */
    public static String wrapConsecutiveListItems(String html) {
        // (?s) dot matches newline. {2,} means at least two <li>…</li> in a row.
        Pattern p = Pattern.compile("(?s)((?:<li>.*?</li>\\s*)+)");
        Matcher m = p.matcher(html);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            // group(1) is e.g. "<li>…</li>\n<li>…</li>\n…"
            String items = m.group(1).trim();
            String replacement = "<ul>\n" + items + "\n</ul>";
            // quoteReplacement in case items contain $ or \
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * Collapse any adjacent </b><b> or </i><i> into nothing,
     * regardless of whether there’s whitespace between them.
     */
    public static String removeRedundantStyleTags(String html) {
        String cleaned = html;
        String prev;
        do {
            prev = cleaned;
            // 1) Remove </b>…<b> pairs if there's a line‐break anywhere between them
            cleaned = cleaned.replaceAll("(?i)</\\s*b\\s*>\\s*\\r?\\n\\s*<\\s*b\\s*>", "");
            // 2) Collapse </b>…<b> pairs separated only by spaces, preserving those spaces
            cleaned = cleaned.replaceAll("(?i)</\\s*b\\s*>([ ]*)<\\s*b\\s*>", "$1");

            // Repeat for <i> tags
            cleaned = cleaned.replaceAll("(?i)</\\s*i\\s*>\\s*\\r?\\n\\s*<\\s*i\\s*>", "");
            cleaned = cleaned.replaceAll("(?i)</\\s*i\\s*>([ ]*)<\\s*i\\s*>", "$1");

        } while (!cleaned.equals(prev));
        return cleaned;
    }

    public static String mergeConsecutiveHeaders(String html) {
        // (?s) so dot matches newlines
        // group(1) = header level (1–6)
        // group(2) = first header’s text
        // group(3) = second header’s text
        Pattern p = Pattern.compile("(?s)<h([1-6])>(.*?)</h\\1>\\s*<h\\1>(.*?)</h\\1>");
        String prev;
        do {
            prev = html;
            Matcher m = p.matcher(html);
            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                String level = m.group(1);
                String a     = m.group(2).trim();
                String b     = m.group(3).trim();
                String merged = "<h" + level + ">" + a + " " + b + "</h" + level + ">";
                m.appendReplacement(sb, Matcher.quoteReplacement(merged));
            }
            m.appendTail(sb);
            html = sb.toString();
        } while (!html.equals(prev));
        return html;
    }
}
