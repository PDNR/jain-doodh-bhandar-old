package com.jaindoodhbhandaaran.model;

import java.io.Serializable;

public class UserModel implements Serializable {
    String id;
    String type;

    public String getId() {
        return this.id;
    }

    public void setId(String str) {
        this.id = str;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String str) {
        this.type = str;
    }
}
