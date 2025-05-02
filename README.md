# EasierMessages

EasierMessages is a powerful Java library for Minecraft servers that simplifies the creation of interactive chat messages. Built on top of the Paper API's Adventure components, it provides an intuitive builder pattern for creating rich text with colors, clickable elements, hover effects, and more.

## Features

- 🎨 **Color formatting** with hex and legacy color codes
- 👆 **Click events** (run commands, suggest commands, open URLs)
- 💬 **Hover effects** with custom text
- 🔗 **Combined interactions** (click + hover in one element)
- 🏗️ **Builder pattern** for clean, readable code
- 📝 **Two tag syntaxes** for flexible message creation

## Installation

### Gradle

```groovy
repositories {
    mavenCentral()
    maven {
        url "https://jitpack.io"
    }
}

dependencies {
    implementation 'com.github.User-19fff:EasierMessages:81355a2cb6'
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.User-19fff</groupId>
        <artifactId>EasierMessages</artifactId>
        <version>81355a2cb6</version>
    </dependency>
</dependencies>
```

## Usage

### Basic Usage

```java
import net.coma112.easiermessages.EasierMessages;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class Example {
    public void sendMessage(Player player) {
        // Simple colored message
        Component message = EasierMessages.translateMessage("&aHello, &b&lworld&r!").build();
        player.sendMessage(message);
        
        // Empty builder
        Component custom = EasierMessages.empty()
            .append("&6Welcome to the server!")
            .build();
        player.sendMessage(custom);
    }
}
```

### Interactive Elements

#### Click Events

```java
// Run a command when clicked
Component commandMessage = EasierMessages.empty()
    .append("Run ")
    .appendClickable("&c/kit starter", "/kit starter")
    .build();
    
// Suggest a command
Component suggestMessage = EasierMessages.empty()
    .append("Type: ")
    .appendSuggest("&e/msg admin", "/msg admin ")
    .build();
    
// Open a URL
Component urlMessage = EasierMessages.empty()
    .append("Visit our ")
    .appendUrl("&9website", "https://example.com")
    .build();
```

#### Hover Effects

```java
// Show text on hover
Component hoverMessage = EasierMessages.empty()
    .append("Hover ")
    .appendHoverable("&bover me", "&aThis is a tooltip")
    .append("&e to see more information.")
    .build();
```

#### Combined Effects

```java
// Clickable with hover effect
Component combined = EasierMessages.empty()
    .append("&6Check this out: ")
    .appendClickableAndHoverable("&b&lCLICK", "/spawn", "&eClick to teleport to spawn")
    .build();
    
// URL with hover effect
Component urlWithHover = EasierMessages.empty()
    .append("Visit our ")
    .appendUrlWithHover("&9forums", "https://example.com/forums", "&7https://example.com/forums")
    .build();
    
// Suggestion with hover effect
Component suggestWithHover = EasierMessages.empty()
    .append("Need help? ")
    .appendSuggestWithHover("&eAsk staff", "/msg staff ", "&7Click to message the staff team")
    .build();
```

### Text Styling

```java
// Setting color
Component coloredText = EasierMessages.empty()
    .append("Default text ")
    .color("#FF5555")
    .append("Colored text")
    .build();
    
// Adding decorations
import net.kyori.adventure.text.format.TextDecoration;

Component boldText = EasierMessages.empty()
    .append("This will be ")
    .append("bold")
    .decorate(TextDecoration.BOLD)
    .build();
```

### Tag-Based Formatting

EasierMessages supports two tag syntaxes for creating interactive elements:

#### Legacy Tag Format

```java
// Simple hover
Component hover = EasierMessages.translateMessage(
    "This has a <hover>hover effect</hover>(&7Additional information here)"
).build();

// Simple click
Component click = EasierMessages.translateMessage(
    "This has a <click>clickable element</click>(/command)"
).build();

// Simple URL
Component url = EasierMessages.translateMessage(
    "Visit our <url>website</url>(https://example.com)"
).build();

// Simple suggestion
Component suggest = EasierMessages.translateMessage(
    "You can <suggest>click here</suggest>(/help) to get a command suggestion"
).build();

// Nested hover and click (in both orders)
Component hoverAndClick = EasierMessages.translateMessage(
    "This has <hover><click>hover and click</click>(/command)</hover>(&7Hover text)"
).build();

Component clickAndHover = EasierMessages.translateMessage(
    "This has <click><hover>click and hover</hover>(&7Hover text)</click>(/command)"
).build();
```

#### Enhanced Tag Format

```java
// Interactive with command
Component enhanced = EasierMessages.translateMessage(
    "An <interactive:Click me|command=/spawn|hover=&7Teleport to spawn>"
).build();

// Specific click event
Component click = EasierMessages.translateMessage(
    "A <click:Run command|command=kit starter>"
).build();

// Specific hover event
Component hover = EasierMessages.translateMessage(
    "A <hover:Hover me|text=&7This is hover text>"
).build();

// URL element
Component url = EasierMessages.translateMessage(
    "Visit our <url:website|link=https://example.com>"
).build();

// Suggest command
Component suggest = EasierMessages.translateMessage(
    "Try <suggest:this command|command=/help>"
).build();
```

### Working with Existing Components

```java
// Create from an existing component
import net.kyori.adventure.text.Component;

Component existing = Component.text("Existing");
Component modified = EasierMessages.fromComponent(existing)
    .append(" with additions")
    .build();
    
// Append components from other MessageBuilders
Component part1 = EasierMessages.translateMessage("&aPart one").build();
Component combined = EasierMessages.empty()
    .append(part1)
    .append(" and &cpart two")
    .build();
```

## Formatting Reference

### Legacy Color Codes

- `&0` - Black
- `&1` - Dark Blue
- `&2` - Dark Green
- `&3` - Dark Aqua
- `&4` - Dark Red
- `&5` - Dark Purple
- `&6` - Gold
- `&7` - Gray
- `&8` - Dark Gray
- `&9` - Blue
- `&a` - Green
- `&b` - Aqua
- `&c` - Red
- `&d` - Light Purple
- `&e` - Yellow
- `&f` - White

### Formatting Codes

- `&l` - **Bold**
- `&o` - *Italic*
- `&n` - <u>Underline</u>
- `&m` - ~~Strikethrough~~
- `&k` - Obfuscated
- `&r` - Reset

### Hex Colors

Hex colors can be used with the format `&#RRGGBB`.

## Tag Syntax Reference

### Legacy Tag Format

- `<click>Text</click>(/command)` - Clickable text that runs a command
- `<hover>Text</hover>(Hover message)` - Text with hover effect
- `<suggest>Text</suggest>(/command)` - Clickable text that suggests a command
- `<url>Text</url>(https://example.com)` - Clickable text that opens a URL
- `<hover><click>Text</click>(/command)</hover>(Hover message)` - Text with both hover and click
- `<click><hover>Text</hover>(Hover message)</click>(/command)` - Alternative nesting order

### Enhanced Tag Format

- `<interactive:Text|command=/command|hover=Hover text>` - All-in-one interactive element
- `<click:Text|command=command>` - Click-only element
- `<hover:Text|text=Hover text>` - Hover-only element
- `<url:Text|link=https://example.com>` - URL element
- `<suggest:Text|command=command>` - Suggestion element

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Credits

Created by coma112
