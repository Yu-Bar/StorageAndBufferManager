package com.yubar.adb.manager;

import com.yubar.adb.entity.BCB;

/**
 * Author:Yu-Bar
 * Date:2023/12/10-13:51
 */

public interface Buffer {

    void readPage(int pageId);

    void writePage(int pageId);


    /**
     * fix a page
     * @param pageId
     * @param prot
     * @return frameId
     */
    int fixPage(int pageId,int prot);

    /**
     * fix a new page
     * @return a page_id and a frame_id
     */
    int[] fixNewPage();

    /**
     * unfix a page
     * @param pageId
     * @return frameId
     */
    int unFixPage(int pageId);

    /**
     *
     * @return the number of buffer pages that are free
     */
    int numFreeFrames();

    /**
     * select a frame to replace,If the dirty bit of the selected frame is set then the page needs to be written on to the disk
     * @return framId
     */
    int selectVictim();

    /**
     *
     * @param pageId
     * @return frame id
     */
    int hash(int pageId);

    /**
     * remove the Buffer Control Block
     * @param bcb
     * @param pageId
     */
    void removeBCB(BCB bcb,int pageId);

    /**
     * remove the LRU element from the list.
     * @param frameId
     */
    void removeLRUEle(int frameId);

    /**
     * set the dirty bit for the frame_id
     * @param frameId
     */
    void setDirty(int frameId);

    /**
     * unset the dirty bit for the frame_id
     * @param frameId
     */
    void unSetDirty(int frameId);

    /**
     * write all the dirty pages when the system is shut down
     */
    void writeDirtys();

    /**
     * print the content of the frame
     * @param frameId
     */
    void printFrame(int frameId);
}
