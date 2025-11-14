package io.github.catizard.jlr2arenaex.client;

import io.github.catizard.jlr2arenaex.enums.ServerToClient;

public record Message(ServerToClient id, byte[] data) {
}
