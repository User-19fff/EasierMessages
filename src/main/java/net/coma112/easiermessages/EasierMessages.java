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
 * Segédosztály interaktív üzenetek egyszerű létrehozásához Minecraft szervereken.
 * A kód támogatja a színeket, hover és kattintás eseményeket, és egyéb Minecraft-specifikus
 * szövegformázási lehetőségeket.
 *
 * @author coma112
 * @version 1.0.0
 */
public class EasierMessages {
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
     * Létrehoz egy új MessageBuilder példányt egy színkódokat tartalmazó szövegből.
     *
     * @param message A színkódokat és formázásokat tartalmazó szöveg
     * @return Új MessageBuilder példány
     */
    @NotNull
    @Contract("_ -> new")
    public static MessageBuilder translateMessage(String message) {
        return new MessageBuilder(LEGACY_SERIALIZER.deserialize(message));
    }

    /**
     * Létrehoz egy új MessageBuilder példányt egy már létező Component objektumból.
     *
     * @param component A forrás Component
     * @return Új MessageBuilder példány
     */
    @NotNull
    @Contract("_ -> new")
    public static MessageBuilder fromComponent(@NotNull Component component) {
        return new MessageBuilder(component);
    }

    /**
     * Létrehoz egy új, üres MessageBuilder példányt.
     *
     * @return Új MessageBuilder példány
     */
    @NotNull
    @Contract(" -> new")
    public static MessageBuilder empty() {
        return new MessageBuilder(Component.empty());
    }

    /**
     * Builder osztály interaktív szöveges üzenetek létrehozásához.
     */
    public static class MessageBuilder {
        private final TextComponent.Builder builder;

        /**
         * Létrehoz egy új MessageBuilder példányt.
         *
         * @param component Az alap komponens, amiből építkezünk
         */
        public MessageBuilder(Component component) {
            if (component instanceof TextComponent textComponent) this.builder = textComponent.toBuilder();
            else this.builder = Component.text().append(component);
        }

        /**
         * Hozzáfűz egy szöveget az üzenethez, feldolgozva az esetleges interaktív elemeket.
         *
         * @param text A hozzáfűzendő szöveg
         * @return A MessageBuilder példány a láncolt hívások támogatásához
         */
        public MessageBuilder append(String text) {
            Component processedComponent = processInteractiveElements(text);

            if (processedComponent != null) builder.append(processedComponent);
            else {
                Component translatedText = LEGACY_SERIALIZER.deserialize(text);
                builder.append(translatedText);
            }

            return this;
        }

        /**
         * Hozzáfűz egy Component-et az üzenethez.
         *
         * @param component A hozzáfűzendő komponens
         * @return A MessageBuilder példány a láncolt hívások támogatásához
         */
        public MessageBuilder append(Component component) {
            builder.append(component);
            return this;
        }

        /**
         * Hozzáfűz egy másik MessageBuilder által épített komponenst.
         *
         * @param messageBuilder A hozzáfűzendő MessageBuilder
         * @return A MessageBuilder példány a láncolt hívások támogatásához
         */
        public MessageBuilder append(@NotNull MessageBuilder messageBuilder) {
            builder.append(messageBuilder.build());
            return this;
        }

        /**
         * Hozzáfűz egy kattintható szöveget az üzenethez.
         *
         * @param text A megjelenítendő szöveg
         * @param command A futtatandó parancs kattintáskor
         * @return A MessageBuilder példány a láncolt hívások támogatásához
         */
        public MessageBuilder appendClickable(String text, @NotNull String command) {
            Component translatedText = LEGACY_SERIALIZER.deserialize(text);
            if (!command.startsWith("/")) command = "/" + command;

            builder.append(translatedText.clickEvent(ClickEvent.runCommand(command)));
            return this;
        }

        /**
         * Hozzáfűz egy fölé vihető (hover) szöveget az üzenethez.
         *
         * @param text A megjelenítendő szöveg
         * @param hoverText A hover szöveg
         * @return A MessageBuilder példány a láncolt hívások támogatásához
         */
        public MessageBuilder appendHoverable(String text, String hoverText) {
            Component translatedText = LEGACY_SERIALIZER.deserialize(text);
            Component hoverComponent = LEGACY_SERIALIZER.deserialize(hoverText);

            builder.append(translatedText.hoverEvent(HoverEvent.showText(hoverComponent)));
            return this;
        }

        /**
         * Hozzáfűz egy kattintható és fölé vihető szöveget az üzenethez.
         *
         * @param text A megjelenítendő szöveg
         * @param command A futtatandó parancs kattintáskor
         * @param hoverText A hover szöveg
         * @return A MessageBuilder példány a láncolt hívások támogatásához
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
         * Hozzáfűz egy URL-re mutató szöveget az üzenethez.
         *
         * @param text A megjelenítendő szöveg
         * @param url A megnyitandó URL
         * @return A MessageBuilder példány a láncolt hívások támogatásához
         */
        public MessageBuilder appendUrl(String text, String url) {
            Component translatedText = LEGACY_SERIALIZER.deserialize(text);
            builder.append(translatedText.clickEvent(ClickEvent.openUrl(url)));
            return this;
        }

        /**
         * Hozzáfűz egy parancssort javasló szöveget az üzenethez.
         *
         * @param text A megjelenítendő szöveg
         * @param command A javasolt parancs
         * @return A MessageBuilder példány a láncolt hívások támogatásához
         */
        public MessageBuilder appendSuggest(String text, String command) {
            Component translatedText = LEGACY_SERIALIZER.deserialize(text);
            builder.append(translatedText.clickEvent(ClickEvent.suggestCommand(command)));
            return this;
        }

        /**
         * Beállítja az üzenet színét.
         *
         * @param color A szín hexadecimális formátumban
         * @return A MessageBuilder példány a láncolt hívások támogatásához
         */
        public MessageBuilder color(String color) {
            builder.color(TextColor.fromHexString(color));
            return this;
        }

        /**
         * Hozzáad egy dekorációt az üzenethez.
         *
         * @param decoration A hozzáadandó dekoráció
         * @return A MessageBuilder példány a láncolt hívások támogatásához
         */
        public MessageBuilder decorate(TextDecoration decoration) {
            builder.decoration(decoration, true);
            return this;
        }

        /**
         * Felépíti a végső Component objektumot.
         *
         * @return A felépített Component
         */
        public Component build() {
            return builder.build();
        }

        /**
         * Feldolgozza az interaktív elemeket egy szövegben.
         *
         * @param text A feldolgozandó szöveg
         * @return A feldolgozott Component, vagy null ha nincs benne interaktív elem
         */
        @Nullable
        private Component processInteractiveElements(@NotNull String text) {
            Matcher hoverClickMatcher = HOVER_CLICK_PATTERN.matcher(text);
            if (hoverClickMatcher.find()) {
                String displayText = hoverClickMatcher.group(1);
                String command = hoverClickMatcher.group(2);
                String hoverText = hoverClickMatcher.group(3);

                if (!command.startsWith("/")) command = "/" + command;

                return LEGACY_SERIALIZER.deserialize(displayText)
                        .clickEvent(ClickEvent.runCommand(command))
                        .hoverEvent(HoverEvent.showText(LEGACY_SERIALIZER.deserialize(hoverText)));
            }

            Matcher clickHoverMatcher = CLICK_HOVER_PATTERN.matcher(text);
            if (clickHoverMatcher.find()) {
                String displayText = clickHoverMatcher.group(1);
                String hoverText = clickHoverMatcher.group(2);
                String command = clickHoverMatcher.group(3);

                if (!command.startsWith("/")) command = "/" + command;

                return LEGACY_SERIALIZER.deserialize(displayText)
                        .hoverEvent(HoverEvent.showText(LEGACY_SERIALIZER.deserialize(hoverText)))
                        .clickEvent(ClickEvent.runCommand(command));
            }

            Matcher clickMatcher = CLICK_PATTERN.matcher(text);
            if (clickMatcher.find()) {
                String displayText = clickMatcher.group(1);
                String command = clickMatcher.group(2);

                if (!command.startsWith("/")) command = "/" + command;
                return LEGACY_SERIALIZER.deserialize(displayText)
                        .clickEvent(ClickEvent.runCommand(command));
            }

            Matcher hoverMatcher = HOVER_PATTERN.matcher(text);
            if (hoverMatcher.find()) {
                String displayText = hoverMatcher.group(1);
                String hoverText = hoverMatcher.group(2);
                return LEGACY_SERIALIZER.deserialize(displayText)
                        .hoverEvent(HoverEvent.showText(LEGACY_SERIALIZER.deserialize(hoverText)));
            }

            Matcher suggestMatcher = SUGGEST_PATTERN.matcher(text);
            if (suggestMatcher.find()) {
                String displayText = suggestMatcher.group(1);
                String command = suggestMatcher.group(2);
                return LEGACY_SERIALIZER.deserialize(displayText)
                        .clickEvent(ClickEvent.suggestCommand(command));
            }

            Matcher urlMatcher = URL_PATTERN.matcher(text);
            if (urlMatcher.find()) {
                String displayText = urlMatcher.group(1);
                String url = urlMatcher.group(2);
                return LEGACY_SERIALIZER.deserialize(displayText)
                        .clickEvent(ClickEvent.openUrl(url));
            }

            return null;
        }
    }
}