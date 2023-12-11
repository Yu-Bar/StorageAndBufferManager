package com.yubar.adb.util;
/**
 * Author:Yu-Bar
 * Date:2023/12/10-17:39
 */

import com.yubar.adb.constant.Constant;

import java.text.DecimalFormat;

/**
 *@ClassName Indicator
 *@Description recording some statistical index
 *@Author Yu-Bar
 *@Date 2023/12/10 17:39
 *@Version 1.0
 **/
public class Indicator {
    /**
     * the total IO count
     */
    private static int ios = 0;
    /**
     * the hit count of the buffer
     */
    private static int hits = 0;
    private static int inputs = 0;
    private static int outputs = 0;
    private static int writeDirtys = 0;
    private static int operation = 0;
    private static Long startTime;


    public static void incHits(){
        ++hits;
    }

    public static void incInputs(){
        ++inputs;
        ++ios;
    }

    public static void incOutputs(){
        ++outputs;
        ++ios;
    }

    public static void incWriteDirtys(){
        ++writeDirtys;
    }

    public static int getIos() {
        return ios;
    }

    public static void setOperation(int operation) {
        Indicator.operation = operation;
    }

    public static void start(){
        startTime = System.currentTimeMillis();
    }

    public static void printAllIndex(){
        System.out.println("=== Buffer Size:" + Constant.BUFFER_SIZE + " ===");
        System.out.println("Inputs:" + inputs);
        System.out.println("Outputs:" + outputs);
        System.out.println("IOs:" + ios);
        System.out.println("IOs(Without Write Dirty):" + (ios - writeDirtys));
        System.out.println("Write Dirty:" + writeDirtys);
        System.out.println("Hits:" + hits);
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        String formattedNumber = decimalFormat.format( hits * 100.0 / operation);
        System.out.println("Hit Rate:" + formattedNumber + "%");
        System.out.println("Cost Time:" + (System.currentTimeMillis() - startTime) + "ms");
    }

}
