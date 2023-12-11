package com.yubar.adb.manager;
/**
 * Author:Yu-Bar
 * Date:2023/12/9-18:10
 */

import com.yubar.adb.constant.Constant;
import com.yubar.adb.entity.Frame;
import com.yubar.adb.util.Indicator;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

/**
 *@ClassName DataStorageManager
 *@Author Yu-Bar
 *@Date 2023/12/9 18:10
 *@Version 1.0
 **/
public class DataStorageManager implements DataStorage{
    private RandomAccessFile dbFile;

    private int numPages = Constant.TOTAL_DATA_BLOCKS;

    private Map<Integer,Integer> pageIndex;

    @Override
    public void openFile(String fileName){
        try {
            dbFile = new RandomAccessFile(fileName, "rw");
            loadPageIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeFile(){
        try {
            dbFile.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Frame readPage(int pageId){
        Indicator.incInputs();
        Frame frame = null;
        // 通过索引表查找数据块偏移量
        int dataBlockOffset = 0;
        try {
            dataBlockOffset = pageIndex.get(pageId);
            System.out.println("NO."+ Indicator.getIos() + ":读页面" + pageId + "偏移量：" + dataBlockOffset);

        if (dataBlockOffset != -1) {
            // 读取数据块
            byte[] dataBlock = readDataBlock(dataBlockOffset);
            // 处理读取到的数据块
            frame = new Frame(dataBlock);
        } else {
            System.out.println("未找到页号为 " + pageId + " 的数据块。");
        }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return frame;
    }

    @Override
    public int writePage(int pageId, Frame frame){
        Indicator.incOutputs();
        try {
            int dataBlockOffset = pageIndex.get(pageId);
            System.out.println("NO."+ Indicator.getIos() +":写页面" + pageId + "偏移量：" + dataBlockOffset);
//            dbFile.write(frame.getField(), dataBlockOffset, Constant.BLOCK_SIZE);
            seek(dataBlockOffset);
            dbFile.write(frame.getField());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public void seek(int offset){
        try {
            dbFile.seek(offset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RandomAccessFile getFile(){
        return dbFile;
    }

    @Override
    public void incNumPages(){
        ++numPages;
    }

    @Override
    public int getNumPages(){
        return numPages;
    }

    @Override
    public void setUse(int pageId, int useBit) {
        try {
            if(useBit == 0){
                int indexBlockNumber = (pageId - 1) / Constant.INDEX_BLOCK_ENTRIES; // 索引块编号
                int entryIndex = (pageId - 1) % Constant.INDEX_BLOCK_ENTRIES; // 索引项在索引块中的位置
                seek(indexBlockNumber * Constant.BLOCK_SIZE + entryIndex * 8);

                byte[] pageNumberBytes = new byte[4];
                dbFile.read(pageNumberBytes);

                byte[] offsetBytes = new byte[4];
                dbFile.read(offsetBytes);

                int storedPageNumber = byteArrayToInt(pageNumberBytes);
                if (storedPageNumber == pageId) {
                    dbFile.write(intToByteArray(useBit), indexBlockNumber * Constant.BLOCK_SIZE + entryIndex * 8, Integer.BYTES);
                }
            }
        }catch (IOException exception){
            throw new RuntimeException();
        }
    }

    @Override
    public int getUse(int pageId) {
        return pageIndex.get(pageId);
    }

    /**
     * load the index of all pages
     * @throws IOException
     */
    private void loadPageIndex() throws IOException{
        pageIndex = new HashMap<>();
        seek(0);
        for (int i = 0; i < Constant.TOTAL_DATA_BLOCKS; i++) {
            byte[] pageIdBytes = new byte[4];
            dbFile.read(pageIdBytes);
            int pageId = byteArrayToInt(pageIdBytes);
            byte[] offsetBytes = new byte[4];
            dbFile.read(offsetBytes);
            int offset = byteArrayToInt(offsetBytes);
            pageIndex.put(pageId, offset);
        }
    }

//    /**
//     * 获取指定页号的偏移量
//     * @param pageNumber
//     * @return
//     * @throws IOException
//     */
//    private int getDataBlockOffset(int pageNumber) throws IOException {
//        int indexBlockNumber = (pageNumber - 1) / Constant.INDEX_BLOCK_ENTRIES; // 索引块编号
//        int entryIndex = (pageNumber - 1) % Constant.INDEX_BLOCK_ENTRIES; // 索引项在索引块中的位置
//        seek(indexBlockNumber * Constant.BLOCK_SIZE + entryIndex * 8);
//
//        byte[] pageNumberBytes = new byte[4];
//        dbFile.read(pageNumberBytes);
//
//        byte[] offsetBytes = new byte[4];
//        dbFile.read(offsetBytes);
//
//        int storedPageNumber = byteArrayToInt(pageNumberBytes);
//        if (storedPageNumber == pageNumber) {
//            return byteArrayToInt(offsetBytes);
//        } else {
//            return -1;
//        }
//    }

    /**
     * read the data block
     * @param offset
     * @return
     * @throws IOException
     */
    private byte[] readDataBlock(int offset) throws IOException {
        seek(offset);
        byte[] dataBlock = new byte[Constant.BLOCK_SIZE];
        dbFile.read(dataBlock);
        return dataBlock;
    }

    private int byteArrayToInt(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) |
               ((bytes[1] & 0xFF) << 16) |
               ((bytes[2] & 0xFF) << 8) |
               (bytes[3] & 0xFF);
    }

    private static byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) ((value >> 24) & 0xFF),
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) (value & 0xFF)
        };
    }
}
