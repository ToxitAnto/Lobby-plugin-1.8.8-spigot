package it.lumapvp.lobby.managers;

import it.lumapvp.lobby.LumaPvPLobby;

public class BuildModeManager {

    private final LumaPvPLobby plugin;
    private boolean buildEnabled = false;

    public BuildModeManager(LumaPvPLobby plugin) {
        this.plugin = plugin;
    }

    public boolean isBuildEnabled() {
        return buildEnabled;
    }

    public void setBuildEnabled(boolean enabled) {
        this.buildEnabled = enabled;
    }

    public void toggle() {
        buildEnabled = !buildEnabled;
    }
}
