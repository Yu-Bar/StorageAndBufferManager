package com.yubar.adb.entity;
/**
 * Author:Yu-Bar
 * Date:2023/12/9-22:34
 */

/**
 *@ClassName BCB
 *@Description Buffer Control Block
 *@Author Yu-Bar
 *@Date 2023/12/9 22:34
 *@Version 1.0
 **/
public class BCB {
    public int pageId = -1;
    public int frameId = -1;
    public int count = 0;
    public int dirty = 0;

    public BCB next = null;
}
