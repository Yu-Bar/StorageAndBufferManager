package com.yubar.adb.manager;
/**
 * Author:Yu-Bar
 * Date:2023/12/9-18:11
 */

import com.yubar.adb.constant.Constant;
import com.yubar.adb.entity.BCB;
import com.yubar.adb.entity.Frame;
import com.yubar.adb.entity.LRUNode;
import com.yubar.adb.util.Indicator;

/**
 *@ClassName BufferManager
 *@Author Yu-Bar
 *@Date 2023/12/9 18:11
 *@Version 1.0
 **/
public class BufferManager implements Buffer{
    private Frame[] frames;
    private LRUNode head;
    private LRUNode tail;
    private int capacity = Constant.BUFFER_SIZE;
    private int size;
    private BCB[] pageToFrame;
    private int[] frameToPage;
    private LRUNode[] frameToLRU;

    private DataStorage dataStorage;

    public BufferManager(DataStorage dataStorage) {
        size = 0;
        head = new LRUNode();
        tail = new LRUNode();
        head.next = tail;
        tail.pre = head;
        frames = new Frame[capacity];
        pageToFrame = new BCB[capacity];
        for (int i = 0; i < pageToFrame.length; i++) {
            pageToFrame[i] = new BCB();
        }
        frameToPage = new int[capacity];
        frameToLRU = new LRUNode[capacity];
        this.dataStorage = dataStorage;
    }

    public void readPage(int pageId){
        fixPage(pageId, 1);
        // read the page
        unFixPage(pageId);
    }

    public void writePage(int pageId){
        fixPage(pageId, 0);
        // write the page
        unFixPage(pageId);
    }

    @Override
    public int fixPage(int pageId, int prot) {
        int dirty = prot == 1 ? 0 : 1;
        int framePos = hash(pageId);
        BCB pre = pageToFrame[framePos];;
        BCB bcb = pre.next;
        BCB prePre = pre;
        while(bcb != null && bcb.pageId != pageId){
            prePre = pre;
            pre = bcb;
            bcb = bcb.next;
        }
        if(bcb == null){
            if(size < capacity){
                bcb = new BCB();
                bcb.frameId = size++;
            }else {
                int frameId = -1;
                while(frameId == -1){
                    frameId = selectVictim();
                }
                bcb = new BCB();
                bcb.frameId = frameId;
            }
            bcb.pageId = pageId;
            bcb.count = 0;
            bcb.dirty = 0;
            LRUNode lruNode = new LRUNode();
            lruNode.curBCB = bcb;
            frameToLRU[bcb.frameId] = lruNode;
            frameToPage[bcb.frameId] = pageId;
            if(pre.frameId != bcb.frameId)
                pre.next = bcb;
            else
                prePre.next = bcb;
            Frame frame = dataStorage.readPage(pageId);
            frames[bcb.frameId] = frame;
        }else{
            Indicator.incHits();
        }
        bcb.count++;
        bcb.dirty =  bcb.dirty == 1 ? 1 : dirty;
        moveLRUNodeToHead(bcb.frameId);
        return bcb.frameId;
    }

    @Override
    public int[] fixNewPage() {
        dataStorage.incNumPages();
        int pageId = dataStorage.getNumPages();
        int frameId = fixPage(pageId, 0);
        return new int[]{pageId,frameId};
    }

    @Override
    public int unFixPage(int pageId) {
        int framePos = hash(pageId);
        BCB bcb = pageToFrame[framePos].next;
        while(bcb != null && bcb.pageId != pageId){
            bcb = bcb.next;
        }
        if(bcb != null){
            --bcb.count;
            return bcb.frameId;
        }
        return -1;
    }

    @Override
    public int numFreeFrames() {
        return capacity - size;
    }

    @Override
    public int selectVictim() {
        LRUNode curNode = tail.pre;
        while(curNode != head && curNode.curBCB.count != 0){
            curNode = curNode.pre;
        }
        if(curNode == head)
            return -1;
        BCB bcb = curNode.curBCB;
        int frameId = bcb.frameId;
        if(bcb.dirty == 1){
            dataStorage.writePage(bcb.pageId, frames[bcb.frameId]);
        }
        removeLRUEle(bcb.frameId);
        removeBCB(bcb, bcb.pageId);
        return frameId;
    }

    @Override
    public int hash(int pageId) {
        return pageId % capacity;
    }

    @Override
    public void removeBCB(BCB bcb, int pageId) {
        int framePos = hash(pageId);
        BCB pre = pageToFrame[framePos];
        BCB curBCB = pre.next;
        while(curBCB != null && curBCB != bcb){
            pre = curBCB;
            curBCB = pre.next;
        }
        if(curBCB != null)
            pre.next = bcb.next;
    }

    @Override
    public void removeLRUEle(int frameId) {
        LRUNode node = frameToLRU[frameId];
        if(node.pre != null && node.next != null){
            node.pre.next = node.next;
            node.next.pre = node.pre;
        }
    }

    @Override
    public void setDirty(int frameId) {
        frameToLRU[frameId].curBCB.dirty = 1;
    }

    @Override
    public void unSetDirty(int frameId) {
        frameToLRU[frameId].curBCB.dirty = 0;
    }

    @Override
    public void writeDirtys() {
        LRUNode curNode = head.next;
        while (curNode != tail){
            BCB bcb = curNode.curBCB;
            if(bcb.dirty == 1){
                Indicator.incWriteDirtys();
                dataStorage.writePage(bcb.pageId, frames[bcb.frameId]);
            }
            curNode = curNode.next;
        }
    }

    @Override
    public void printFrame(int frameId) {
        System.out.println(frames[frameId]);
    }

    private void moveLRUNodeToHead(int frameId){
        removeLRUEle(frameId);
        LRUNode node = frameToLRU[frameId];
        node.next = head.next;
        node.pre = head;
        head.next.pre = node;
        head.next = node;
    }

}
