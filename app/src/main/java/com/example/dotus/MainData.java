package com.example.dotus;

public class MainData {

    private String iv_profile;
    private String tv_name;
    private String tv_id;

    public MainData(String iv_profile, String tv_name, String tv_id){
        this.iv_profile=iv_profile;
        this.tv_name=tv_name;
        this.tv_id=tv_id;
    }

    public String getIv_profile() {
        return iv_profile;
    }

    public void setIv_profile(String iv_profile) {
        this.iv_profile = iv_profile;
    }

    public String getTv_name() {
        return tv_name;
    }

    public void setTv_name(String tv_name) {
        this.tv_name = tv_name;
    }

    public String getTv_id() {
        return tv_id;
    }

    public void setTv_id(String tv_id) {
        this.tv_id = tv_id;
    }
}
