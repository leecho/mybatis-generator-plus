package com.github.leecho.idea.plugin.mybatis.generator.model;

/**
 * 保存数据库连接对应的用户名&密码
 * Created by kangtian on 2018/8/3.
 */
public class Credential {

    //数据链接
    private String url;
    //数据库用户名
    private String username;
    //数据库密码
    private String pwd;


    public Credential() {
    }

    public Credential(String url) {
        this.url = url;
    }

    public Credential(String url, String username) {
        this.url = url;
        this.username = username;
    }

    public Credential(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.pwd = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
