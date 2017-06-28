package com.josecuentas.android_retrofit_cancel_request.rest.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jcuentas on 28/06/17.
 */

public class UserEntity {

    @SerializedName("name") public String name;
    @SerializedName("___class") public String Class;
    @SerializedName("lastname") public String lastname;
    @SerializedName("__meta") public String Meta;

    @Override public String toString() {
        return "UserEntity{" +
                "name='" + name + '\'' +
                ", Class='" + Class + '\'' +
                ", lastname='" + lastname + '\'' +
                ", Meta='" + Meta + '\'' +
                '}';
    }
}
