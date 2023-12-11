package com.yubar.adb.util;
/**
 * Author:Yu-Bar
 * Date:2023/12/9-18:55
 */

import com.yubar.adb.constant.Constant;

import java.io.*;

/**
 *@ClassName DBFFactory
 *@Description init the data.dbf file
 *@Author Yu-Bar
 *@Date 2023/12/9 18:55
 *@Version 1.0
 **/
public class DBFFactory {

    public static void initDBF(String filePath){
        // 检查文件是否存在，如果不存在则创建并写入数据
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                RandomAccessFile dbFile = new RandomAccessFile(file, "rw");

                // 写入索引块
                writeIndexBlocks(dbFile);

                // 写入数据块
                writeDataBlocks(dbFile);

                dbFile.close();
                System.out.println("data.dbf 创建成功并写入数据。");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("data.dbf 文件已存在，无需创建。");
        }
    }

    private static void writeIndexBlocks(RandomAccessFile file) throws IOException {
        // 写入索引块，使用前10个块
        for (int i = 0; i < Constant.BLOCK_SIZE; i++) {
            byte[] indexBlock = new byte[Constant.BLOCK_SIZE];

            for (int j = 0; j < Constant.INDEX_BLOCK_ENTRIES; j++) {
                int pageNumber = i * Constant.INDEX_BLOCK_ENTRIES + j + 1;
                byte[] pageNumberBytes = intToByteArray(pageNumber);
                byte[] offsetBytes = intToByteArray((Constant.TOTAL_INDEX_BLOCKS + pageNumber - 1)* Constant.BLOCK_SIZE);

                System.arraycopy(pageNumberBytes, 0, indexBlock, j * 8, 4);
                System.arraycopy(offsetBytes, 0, indexBlock, j * 8 + 4, 4);
            }

            file.write(indexBlock);
        }
    }

    private static void writeDataBlocks(RandomAccessFile file) throws IOException {
        for (int i = 0; i < Constant.TOTAL_DATA_BLOCKS; i++) {
            byte[] dataBlock = generateRandomDataBlock();
            file.write(dataBlock);
        }
    }

    private static byte[] generateRandomDataBlock() {
        byte[] dataBlock = new byte[Constant.BLOCK_SIZE];
        // 在这里生成随机数据
        return dataBlock;
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
