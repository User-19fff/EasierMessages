# EasierMessages

EasierMessages is a Java library for Minecraft servers that makes it easy to create interactive chat messages. The library uses the Paper API and supports colors, clickable, hover, and other interactive message elements.
## Installation

### Gradle

```groovy
repositories {
    mavenCentral()
    maven {
        maven("https://jitpack.io")
    }
}

dependencies {
    implementation("com.github.User-19fff:EasierMessages:e4d7964ebc")
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


<dependency>
<groupId>com.github.User-19fff</groupId>
<artifactId>EasierMessages</artifactId>
<version>e4d7964ebc</version>
</dependency>
```

## Usage

### Basic Message

```java
import net.coma112.easiermessages.EasierMessages;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class Example {
    public void sendMessage(Player player) {
        Component message = EasierMessages.translateMessage("&aHello, &b&lworld&r!").build();
        player.sendMessage(message);
    }
}
```

### Interactive Messages

```java
// Hover effect
Component hoverMessage = EasierMessages.empty()
    .append("&eHover ")
    .appendHoverable("&bAbove me", "&athere is a message")
    .append("&e.")
    .build();

// Clickable message
Component clickableMessage = EasierMessages.empty()
    .append("Click ")
    .appendClickable("&chere", "/kit")
    .append(" to run a command.")
    .build();

// Combinated message
Component combined = EasierMessages.empty()
    .append("&6Nézd: ")
    .appendClickableAndHoverable("&b&lClick here", "/kit", "&eHover information")
    .build();

// URL message
Component urlMessage = EasierMessages.empty()
    .append("Web")
    .appendUrl("&9PAGE", "https://www.minecraft.net")
    .build();
```

### Formatting with strings and not with methods

```java
Component patternMessage = EasierMessages.translateMessage(
    "&aHello! <hover>Hover</hover>(&6More Info " +
    "és <click>Click here</click>(/kit) command"
).build();

// Combinated example
Component complexPattern = EasierMessages.translateMessage(
    "&eEz egy <hover><click>komplex</click>(/say Complex!)</hover>(&dCombinated hover) example."
).build();
```

### Formats

- <url>Message</url>(url)
- <click>Message</click>(command with /)
- <hover>Message</hover>(hover message)
- <suggest>Message</suggest>(suggest)

## License

[MIT](LICENSE)
