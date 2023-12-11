package com.yubar.adb.manager;

import com.yubar.adb.entity.Frame;

import java.io.RandomAccessFile;

/**
 * Author:Yu-Bar
 * Date:2023/12/10-13:51
 */

public interface DataStorage {

    /**
     * open file
     * @param fileName
     */
    void openFile(String fileName);

    /**
     * close file
     */
    void closeFile();

    /**
     * read a page
     * @param pageId
     * @return a frame of data
     */
    Frame readPage(int pageId);

    /**
     * write the data back to page whenever a page is taken out of the buffer
     * @param pageId
     * @param frame
     * @return
     */
    int writePage(int pageId,Frame frame);

    /**
     * moves the file pointer.
     * @param offset
     */
    void seek(int offset);

    /**
     * @return the current file
     */
    RandomAccessFile getFile();

    /**
     * increments the page counter.
     */
    void incNumPages();

    /**
     * @return the page counter
     */
    int getNumPages();

    /**
     * set the bit in the pages array
     * @param pageId
     * @param useBit
     */
    void setUse(int pageId,int useBit);

    /**
     * @param pageId
     * @return the current use_bit for the corresponding page_id
     */
    int getUse(int pageId);

}
