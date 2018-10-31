package com.yunfeng.anysdk.tool;

import java.util.HashMap;
import java.util.Map;

class Config {
    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

public class IniConfig {
    private Map<String, Config> configs;

    public Map<String, Config> getConfigs() {
        return configs;
    }

    public void setConfigs(Map<String, Config> configs) {
        this.configs = configs;
    }
}
