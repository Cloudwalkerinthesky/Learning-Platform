package com.phantom.common;

public enum CourseStatus {
//    OPEN("OPEN", "开放报名"),
    PUBLISHED("PUBLISHED","已发布"),
//    CLOSED("CLOSED","已关闭"),
    DELETED("DELETED","已删除");

    private final String code;
    private final String description;

    CourseStatus(String code,String description){
        this.code=code;
        this.description=description;
    }
    public String getCode(){
        return code;
    }
    public String getDescription(){
        return description;
    }
}
