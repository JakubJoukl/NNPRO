package com.example.nnprorocnikovyprojekt.dtos.user;

import java.util.List;

public class PublicKeyDto {
    private String crv;
    private Boolean ext;
    private String kty;
    private List<String> keyOps;
    private String x;
    private String y;

    public String getCrv() {
        return crv;
    }

    public void setCrv(String crv) {
        this.crv = crv;
    }

    public Boolean getExt() {
        return ext;
    }

    public void setExt(Boolean ext) {
        this.ext = ext;
    }

    public String getKty() {
        return kty;
    }

    public void setKty(String kty) {
        this.kty = kty;
    }

    public List<String> getKeyOps() {
        return keyOps;
    }

    public void setKeyOps(List<String> keyOps) {
        this.keyOps = keyOps;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }
}
