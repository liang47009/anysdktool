package com.yunfeng.anysdk.tool;

public class GameConfig {
    private IniConfig iniConfig;
    private String gameDir;
    private String gameOutDir;
    private String channelDir;
    private String channelOutDir;
    private String unitDir;
    private String unitOutDir;

    public String getUnitDir() {
        return unitDir;
    }

    public void setUnitDir(String unitDir) {
        this.unitDir = unitDir;
    }

    public String getUnitOutDir() {
        return unitOutDir;
    }

    public void setUnitOutDir(String unitOutDir) {
        this.unitOutDir = unitOutDir;
    }

    public IniConfig getIniConfig() {
        return iniConfig;
    }

    public void setIniConfig(IniConfig iniConfig) {
        this.iniConfig = iniConfig;
    }

    public String getGameDir() {
        return gameDir;
    }

    public void setGameDir(String gameDir) {
        this.gameDir = gameDir;
    }

    public String getGameOutDir() {
        return gameOutDir;
    }

    public void setGameOutDir(String gameOutDir) {
        this.gameOutDir = gameOutDir;
    }

    public String getChannelDir() {
        return channelDir;
    }

    public void setChannelDir(String channelDir) {
        this.channelDir = channelDir;
    }

    public String getChannelOutDir() {
        return channelOutDir;
    }

    public void setChannelOutDir(String channelOutDir) {
        this.channelOutDir = channelOutDir;
    }
}
