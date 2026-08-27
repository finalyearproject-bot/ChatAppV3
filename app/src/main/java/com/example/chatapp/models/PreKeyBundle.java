package com.example.chatapp.models;

import com.google.gson.annotations.SerializedName;

public class PreKeyBundle {

    @SerializedName("phone")
    public String phone;

    @SerializedName("identity_public")
    public String identityPublic;

    @SerializedName("signed_prekey_public")
    public String signedPreKeyPublic;

    @SerializedName("signature")
    public String signature;

    @SerializedName("one_time_prekey_public")
    public String oneTimePreKeyPublic;

    // 🔥 NEW: Post-Quantum PreKey
    @SerializedName("pqPreKeyPublic")
    public String pqPreKeyPublic;
}