package com.zhengxunw.colorfuldays.database;

class Pair {

    String fieldName;
    String val;

    Pair(String fieldName, String val) {
        this.fieldName = fieldName;
        this.val = val;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getVal() {
        return val;
    }
}