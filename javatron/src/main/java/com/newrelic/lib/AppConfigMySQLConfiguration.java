package com.newrelic.lib;

import java.io.Serializable;

public class AppConfigMySQLConfiguration implements Serializable
{
    public AppConfigMySQLConfiguration()
    {
    }

    public String getHost() {
        return this.host;
    }
    public void setHost(String host) {
        this.host = host;
    }

    public String getUser() {
        return this.user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    public String getPort() {
        return this.port;
    }
    public void setPort(String port) {
        this.port = port;
    }

    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public boolean isConfigured() {
        if (this.host != null && !this.host.isEmpty() &&
            this.port != null && !this.port.isEmpty() &&
            this.user != null && !this.user.isEmpty() &&
            this.name != null && !this.name.isEmpty() &&
            this.password != null && !this.password.isEmpty()) {
            return true;
        }

        return false;
    }

    private String host;
    private String port;
    private String user;
    private String password;
    private String name;
}
