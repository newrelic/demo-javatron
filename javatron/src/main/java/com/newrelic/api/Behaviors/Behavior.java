package com.newrelic.api.behaviors;

import java.io.Serializable;

import com.newrelic.lib.Logger;

public class Behavior implements Serializable {
    private String name;
    private String value;

    public Behavior() {}

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    public void Execute()
    {
        Logger.GetOrCreate().Info("Executing behavior " +this.name);
    }

}
