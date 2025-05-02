package net.coma112.easiermessages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class to process multiple interactive elements in a message
 */
public class EnchantedMessageProcessor {
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    // Enhanced parsing pattern - matches all tags with their properties
    private static final Pattern TAG_PATTERN = Pattern.compile("<([a-zA-Z]+):([^>]+)>");

    /**
     * Process all interactive elements using the new enhanced syntax
     *
     * @param text The input text to process
     * @return A component with all interactive elements processed
     */
    public static @NotNull Component process(String text) {
        TextComponent.Builder resultBuilder = Component.text();
        int lastEnd = 0;

        Matcher matcher = TAG_PATTERN.matcher(text);
        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                String plainText = text.substring(lastEnd, matcher.start());
                resultBuilder.append(LEGACY_SERIALIZER.deserialize(plainText));
            }

            String tagType = matcher.group(1);
            String tagContent = matcher.group(2);

            String[] parts = tagContent.split("\\|");
            String displayText = parts[0];
            ConcurrentHashMap<String, String> properties = new ConcurrentHashMap<>();

            for (int i = 1; i < parts.length; i++) {
                String[] property = parts[i].split("=", 2);
                if (property.length == 2) properties.put(property[0], property[1]);
            }

            Component component = createComponent(tagType, displayText, properties);
            resultBuilder.append(component);

            lastEnd = matcher.end();
        }

        if (lastEnd < text.length()) {
            String remaining = text.substring(lastEnd);
            resultBuilder.append(LEGACY_SERIALIZER.deserialize(remaining));
        }

        return resultBuilder.build();
    }

    /**
     * Create a component based on tag type and properties
     */
    private static Component createComponent(@NotNull String tagType, @NotNull String displayText, Map<String, String> properties) {
        Component baseComponent = LEGACY_SERIALIZER.deserialize(displayText);

        switch (tagType.toLowerCase()) {
            case "click":
                String command = properties.getOrDefault("command", "");
                if (!command.isEmpty()) {
                    if (!command.startsWith("/")) command = "/" + command;
                    baseComponent = baseComponent.clickEvent(ClickEvent.runCommand(command));
                }
                break;

            case "hover":
                String hoverText = properties.getOrDefault("text", "");
                if (!hoverText.isEmpty()) {
                    baseComponent = baseComponent.hoverEvent(HoverEvent.showText(LEGACY_SERIALIZER.deserialize(hoverText)));
                }
                break;

            case "suggest":
                String suggestion = properties.getOrDefault("command", "");
                if (!suggestion.isEmpty()) baseComponent = baseComponent.clickEvent(ClickEvent.suggestCommand(suggestion));
                break;

            case "url":
                String url = properties.getOrDefault("link", "");
                if (!url.isEmpty()) baseComponent = baseComponent.clickEvent(ClickEvent.openUrl(url));
                break;

            case "interactive":
                String clickCommand = properties.getOrDefault("command", "");
                String hoverTextContent = properties.getOrDefault("hover", "");
                String urlLink = properties.getOrDefault("url", "");
                String suggestCommand = properties.getOrDefault("suggest", "");

                if (!hoverTextContent.isEmpty()) {
                    baseComponent = baseComponent.hoverEvent(HoverEvent.showText(LEGACY_SERIALIZER.deserialize(hoverTextContent)));
                }

                if (!clickCommand.isEmpty()) {
                    if (!clickCommand.startsWith("/")) clickCommand = "/" + clickCommand;
                    baseComponent = baseComponent.clickEvent(ClickEvent.runCommand(clickCommand));
                } else if (!urlLink.isEmpty()) baseComponent = baseComponent.clickEvent(ClickEvent.openUrl(urlLink));
                else if (!suggestCommand.isEmpty()) baseComponent = baseComponent.clickEvent(ClickEvent.suggestCommand(suggestCommand));

                break;
        }

        return baseComponent;
    }
}
