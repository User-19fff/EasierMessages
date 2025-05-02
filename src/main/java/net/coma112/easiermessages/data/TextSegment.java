package net.coma112.easiermessages.data;

import net.kyori.adventure.text.Component;

public record TextSegment(int start, int end, Component component) {}
