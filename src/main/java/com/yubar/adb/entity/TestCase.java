package com.yubar.adb.entity;
/**
 * Author:Yu-Bar
 * Date:2023/12/10-18:28
 */

/**
 *@ClassName TestCase
 *@Description the test case entity for the trace file
 *@Author Yu-Bar
 *@Date 2023/12/10 18:28
 *@Version 1.0
 **/
public class TestCase {
    private int rw;
    private int pageId;

    public TestCase(int rw, int pageId) {
        this.rw = rw;
        this.pageId = pageId;
    }

    public int getRw() {
        return rw;
    }

    public int getPageId() {
        return pageId;
    }

    @Override
    public String toString() {
        return "(" + rw + ", " + pageId + ")";
    }
}
