package net.coma112.easiermessages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class for simple creation of interactive messages on Minecraft servers.
 * The code supports colors, hover and click events, and other Minecraft-specific
 * text formatting options.
 *
 * @author coma112
 * @version 1.0.1
 */
public class EasierMessages {
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    /**
     * Creates a new MessageBuilder instance from text containing color codes.
     *
     * @param message The text containing color codes and formatting
     * @return New MessageBuilder instance
     */
    @NotNull
    @Contract("_ -> new")
    public static MessageBuilder translateMessage(String message) {
        MessageBuilder builder = empty();
        builder.append(message);
        return builder;
    }

    /**
     * Creates a new MessageBuilder instance from an existing Component object.
     *
     * @param component The source Component
     * @return New MessageBuilder instance
     */
    @NotNull
    @Contract("_ -> new")
    public static MessageBuilder fromComponent(@NotNull Component component) {
        return new MessageBuilder(component);
    }

    /**
     * Creates a new empty MessageBuilder instance.
     *
     * @return New MessageBuilder instance
     */
    @NotNull
    @Contract(" -> new")
    public static MessageBuilder empty() {
        return new MessageBuilder(Component.empty());
    }

    /**
     * Builder class for creating interactive text messages.
     */
    public static class MessageBuilder {
        private final TextComponent.Builder builder;

        /**
         * Creates a new MessageBuilder instance.
         *
         * @param component The base component to build from
         */
        public MessageBuilder(Component component) {
            if (component instanceof TextComponent textComponent) this.builder = textComponent.toBuilder();
            else this.builder = Component.text().append(component);
        }

        /**
         * Appends text to the message, processing any interactive elements.
         *
         * @param text The text to append
         * @return The MessageBuilder instance to support chained calls
         */
        public MessageBuilder append(String text) {
            Component processedComponent = MessageProcessor.processAllInteractiveElements(text);
            builder.append(processedComponent);
            return this;
        }

        /**
         * Appends a Component to the message.
         *
         * @param component The component to append
         * @return The MessageBuilder instance to support chained calls
         */
        public MessageBuilder append(Component component) {
            builder.append(component);
            return this;
        }

        /**
         * Appends a component built by another MessageBuilder.
         *
         * @param messageBuilder The MessageBuilder to append
         * @return The MessageBuilder instance to support chained calls
         */
        public MessageBuilder append(@NotNull MessageBuilder messageBuilder) {
            builder.append(messageBuilder.build());
            return this;
        }

        /**
         * Appends clickable text to the message.
         *
         * @param text The text to display
         * @param command The command to run when clicked
         * @return The MessageBuilder instance to support chained calls
         */
        public MessageBuilder appendClickable(String text, @NotNull String command) {
            Component translatedText = LEGACY_SERIALIZER.deserialize(text);
            if (!command.startsWith("/")) command = "/" + command;

            builder.append(translatedText.clickEvent(ClickEvent.runCommand(command)));
            return this;
        }

        /**
         * Appends hoverable text to the message.
         *
         * @param text The text to display
         * @param hoverText The hover text
         * @return The MessageBuilder instance to support chained calls
         */
        public MessageBuilder appendHoverable(String text, String hoverText) {
            Component translatedText = LEGACY_SERIALIZER.deserialize(text);
            Component hoverComponent = LEGACY_SERIALIZER.deserialize(hoverText);

            builder.append(translatedText.hoverEvent(HoverEvent.showText(hoverComponent)));
            return this;
        }

        /**
         * Appends clickable and hoverable text to the message.
         *
         * @param text The text to display
         * @param command The command to run when clicked
         * @param hoverText The hover text
         * @return The MessageBuilder instance to support chained calls
         */
        public MessageBuilder appendClickableAndHoverable(String text, @NotNull String command, String hoverText) {
            Component translatedText = LEGACY_SERIALIZER.deserialize(text);
            Component hoverComponent = LEGACY_SERIALIZER.deserialize(hoverText);

            if (!command.startsWith("/")) command = "/" + command;

            builder.append(translatedText
                    .clickEvent(ClickEvent.runCommand(command))
                    .hoverEvent(HoverEvent.showText(hoverComponent)));
            return this;
        }

        /**
         * Appends URL-linking text to the message.
         *
         * @param text The text to display
         * @param url The URL to open
         * @return The MessageBuilder instance to support chained calls
         */
        public MessageBuilder appendUrl(String text, String url) {
            Component translatedText = LEGACY_SERIALIZER.deserialize(text);
            builder.append(translatedText.clickEvent(ClickEvent.openUrl(url)));
            return this;
        }

        /**
         * Appends command-suggesting text to the message.
         *
         * @param text The text to display
         * @param command The suggested command
         * @return The MessageBuilder instance to support chained calls
         */
        public MessageBuilder appendSuggest(String text, String command) {
            Component translatedText = LEGACY_SERIALIZER.deserialize(text);
            builder.append(translatedText.clickEvent(ClickEvent.suggestCommand(command)));
            return this;
        }

        /**
         * Sets the color of the message.
         *
         * @param color The color in hexadecimal format
         * @return The MessageBuilder instance to support chained calls
         */
        public MessageBuilder color(String color) {
            builder.color(TextColor.fromHexString(color));
            return this;
        }

        /**
         * Adds a decoration to the message.
         *
         * @param decoration The decoration to add
         * @return The MessageBuilder instance to support chained calls
         */
        public MessageBuilder decorate(TextDecoration decoration) {
            builder.decoration(decoration, true);
            return this;
        }

        /**
         * Builds the final Component object.
         *
         * @return The built Component
         */
        public Component build() {
            return builder.build();
        }

        /**
         * Processes interactive elements in a text.
         * This method is kept for backward compatibility but redirects to MessageProcessor.
         *
         * @param text The text to process
         * @return The processed Component, or null if it doesn't contain interactive elements
         * @deprecated Use MessageProcessor.processAllInteractiveElements instead
         */
        @Deprecated
        private @NotNull Component processInteractiveElements(@NotNull String text) {
            return MessageProcessor.processAllInteractiveElements(text);
        }
    }
}