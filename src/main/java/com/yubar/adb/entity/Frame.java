package com.yubar.adb.entity;
/**
 * Author:Yu-Bar
 * Date:2023/12/9-18:35
 */

/**
 *@ClassName Frame
 *@Description the struct of a buffer frame
 *@Author Yu-Bar
 *@Date 2023/12/9 18:35
 *@Version 1.0
 **/
public class Frame {
    private byte[] field;

    public Frame(byte[] field) {
        this.field = field;
    }

    public byte[] getField() {
        return field;
    }

    public void setField(byte[] field) {
        this.field = field;
    }
}
