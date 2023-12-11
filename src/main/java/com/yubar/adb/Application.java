package com.yubar.adb;
/**
 * Author:Yu-Bar
 * Date:2023/12/9-17:38
 */

import com.yubar.adb.entity.BCB;
import com.yubar.adb.entity.TestCase;
import com.yubar.adb.manager.Buffer;
import com.yubar.adb.manager.BufferManager;
import com.yubar.adb.manager.DataStorage;
import com.yubar.adb.manager.DataStorageManager;
import com.yubar.adb.util.DBFFactory;
import com.yubar.adb.util.Indicator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *@ClassName Application
 *@Description Application Entry Point
 *@Author Yu-Bar
 *@Date 2023/12/9 17:38
 *@Version 1.0
 **/
public class Application {
    private static String filePath = "src/main/resources/data.dbf"; // 路径根据实际情况修改
    private static String testCaseFilePath = "/data-5w-50w-zipf.txt"; // 路径根据实际情况修改
    private static List<TestCase> testCaseList = new ArrayList<>();
    public static void main(String[] args) {
        // load the dbf
        DBFFactory.initDBF(filePath);
        // load the dataStorageManager and the bufferManager
        DataStorage dataStorageManager = new DataStorageManager();
        dataStorageManager.openFile(filePath);
        Buffer bufferManager = new BufferManager(dataStorageManager);
        // load the test case
        loadTestCase();
        Indicator.setOperation(testCaseList.size());
        // start the test
        Indicator.start();
        for (TestCase testCase : testCaseList) {
            if(testCase.getRw() == 1)
                bufferManager.writePage(testCase.getPageId());
            else
                bufferManager.readPage(testCase.getPageId());
        }
        // write dirtys
        bufferManager.writeDirtys();
        // indicator print all index
        Indicator.printAllIndex();
        // finish
        dataStorageManager.closeFile();
    }

    private static void loadTestCase(){
        InputStream inputStream = Application.class.getResourceAsStream(testCaseFilePath);
        if(inputStream != null){
            try(Scanner scanner = new Scanner(inputStream)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] tokens = line.split(",");

                    if (tokens.length == 2) {
                        try {
                            int rw = Integer.parseInt(tokens[0]);
                            int pageId = Integer.parseInt(tokens[1]);
                            TestCase testCase = new TestCase(rw, pageId);
                            testCaseList.add(testCase);
                        } catch (NumberFormatException e) {
                            // Handle parsing errors if necessary
                            System.err.println("Error parsing line: " + line);
                        }
                    } else {
                        System.err.println("Invalid line format: " + line);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error reading file!");
                e.printStackTrace();
            }
        }
    }
}
