package com.yubar.adb.constant;
/**
 * Author:Yu-Bar
 * Date:2023/12/9-21:39
 */

/**
 *@ClassName Constant
 *@Description some constant
 *@Author Yu-Bar
 *@Date 2023/12/9 21:39
 *@Version 1.0
 **/
public class Constant {
    /**
     * 块大小
     */
    public static final int BLOCK_SIZE = 4096;

    /**
     * 缓冲区大小
     */
    public static final int BUFFER_SIZE = 1024;

    /**
     * 索引块中索引的条目数
     */
    public static final int INDEX_BLOCK_ENTRIES = BLOCK_SIZE / 8;

    /**
     * 总数据块数
     */
    public static final int TOTAL_DATA_BLOCKS = 50000;

    /**
     * 总索引块数
     */
    public static final int TOTAL_INDEX_BLOCKS = TOTAL_DATA_BLOCKS / INDEX_BLOCK_ENTRIES + (TOTAL_DATA_BLOCKS % INDEX_BLOCK_ENTRIES  == 0 ? 0 : 1);
}
