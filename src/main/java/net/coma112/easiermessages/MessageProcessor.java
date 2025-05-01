package net.coma112.easiermessages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class to process multiple interactive elements in a message
 */
public class MessageProcessor {
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    private static final Pattern CLICK_PATTERN = Pattern.compile("<click>(.*?)</click>\\((.+?)\\)");
    private static final Pattern HOVER_PATTERN = Pattern.compile("<hover>(.*?)</hover>\\((.+?)\\)");
    private static final Pattern SUGGEST_PATTERN = Pattern.compile("<suggest>(.*?)</suggest>\\((.+?)\\)");
    private static final Pattern URL_PATTERN = Pattern.compile("<url>(.*?)</url>\\((.+?)\\)");
    private static final Pattern HOVER_CLICK_PATTERN = Pattern.compile("<hover><click>(.*?)</click>\\((.+?)\\)</hover>\\((.+?)\\)");
    private static final Pattern CLICK_HOVER_PATTERN = Pattern.compile("<click><hover>(.*?)</hover>\\((.+?)\\)</click>\\((.+?)\\)");

    /**
     * Class to represent a text segment with its start and end indices
     */
    private static class TextSegment {
        int start;
        int end;
        Component component;

        TextSegment(int start, int end, Component component) {
            this.start = start;
            this.end = end;
            this.component = component;
        }
    }

    /**
     * Process all interactive elements in the message
     *
     * @param text The input text to process
     * @return A component with all interactive elements processed
     */
    public static @NotNull Component processAllInteractiveElements(String text) {
        List<TextSegment> segments = new ArrayList<>();

        findPattern(text, HOVER_CLICK_PATTERN, segments, (matcher) -> {
            String displayText = matcher.group(1);
            String command = matcher.group(2);
            String hoverText = matcher.group(3);

            if (!command.startsWith("/")) command = "/" + command;

            return LEGACY_SERIALIZER.deserialize(displayText)
                    .clickEvent(ClickEvent.runCommand(command))
                    .hoverEvent(HoverEvent.showText(LEGACY_SERIALIZER.deserialize(hoverText)));
        });

        findPattern(text, CLICK_HOVER_PATTERN, segments, (matcher) -> {
            String displayText = matcher.group(1);
            String hoverText = matcher.group(2);
            String command = matcher.group(3);

            if (!command.startsWith("/")) command = "/" + command;

            return LEGACY_SERIALIZER.deserialize(displayText)
                    .hoverEvent(HoverEvent.showText(LEGACY_SERIALIZER.deserialize(hoverText)))
                    .clickEvent(ClickEvent.runCommand(command));
        });

        findPattern(text, CLICK_PATTERN, segments, (matcher) -> {
            String displayText = matcher.group(1);
            String command = matcher.group(2);

            if (!command.startsWith("/")) command = "/" + command;
            return LEGACY_SERIALIZER.deserialize(displayText)
                    .clickEvent(ClickEvent.runCommand(command));
        });

        findPattern(text, HOVER_PATTERN, segments, (matcher) -> {
            String displayText = matcher.group(1);
            String hoverText = matcher.group(2);
            return LEGACY_SERIALIZER.deserialize(displayText)
                    .hoverEvent(HoverEvent.showText(LEGACY_SERIALIZER.deserialize(hoverText)));
        });

        findPattern(text, SUGGEST_PATTERN, segments, (matcher) -> {
            String displayText = matcher.group(1);
            String command = matcher.group(2);
            return LEGACY_SERIALIZER.deserialize(displayText)
                    .clickEvent(ClickEvent.suggestCommand(command));
        });

        findPattern(text, URL_PATTERN, segments, (matcher) -> {
            String displayText = matcher.group(1);
            String url = matcher.group(2);
            return LEGACY_SERIALIZER.deserialize(displayText)
                    .clickEvent(ClickEvent.openUrl(url));
        });

        segments.sort(Comparator.comparingInt(a -> a.start));

        TextComponent.Builder resultBuilder = Component.text();
        int lastEnd = 0;

        for (TextSegment segment : segments) {
            if (segment.start > lastEnd) {
                String plainText = text.substring(lastEnd, segment.start);
                resultBuilder.append(LEGACY_SERIALIZER.deserialize(plainText));
            }

            resultBuilder.append(segment.component);
            lastEnd = segment.end;
        }

        if (lastEnd < text.length()) {
            String remaining = text.substring(lastEnd);
            resultBuilder.append(LEGACY_SERIALIZER.deserialize(remaining));
        }

        return resultBuilder.build();
    }

    /**
     * Find all occurrences of a pattern and create text segments
     */
    private static void findPattern(String text, @NotNull Pattern pattern, List<TextSegment> segments,
                                    Function<Matcher, Component> componentBuilder) {
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            Component component = componentBuilder.apply(matcher);
            segments.add(new TextSegment(matcher.start(), matcher.end(), component));
        }
    }
}