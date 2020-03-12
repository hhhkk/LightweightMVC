package com.yzk.lightweightmvc.network;

import java.io.Serializable;
import java.util.Objects;

import okhttp3.Cookie;

public class BaseCookie implements Serializable {
    public String name;
    public String value;
    public long expiresAt;
    public String domain;
    public String path;
    public boolean secure;
    public boolean httpOnly;
    public boolean hostOnly;

    public BaseCookie(Cookie cookie) {
        this.name = cookie.name();
        this.value = cookie.value();
        this.expiresAt = cookie.expiresAt();
        this.domain = cookie.domain();
        this.path = cookie.path();
        this.secure = cookie.secure();
        this.httpOnly = cookie.httpOnly();
        this.hostOnly = cookie.hostOnly();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public boolean isHostOnly() {
        return hostOnly;
    }

    public void setHostOnly(boolean hostOnly) {
        this.hostOnly = hostOnly;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseCookie that = (BaseCookie) o;
        return expiresAt == that.expiresAt &&
                secure == that.secure &&
                httpOnly == that.httpOnly &&
                hostOnly == that.hostOnly &&
                Objects.equals(name, that.name) &&
                Objects.equals(value, that.value) &&
                Objects.equals(domain, that.domain) &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, expiresAt, domain, path, secure, httpOnly, hostOnly);
    }
}
