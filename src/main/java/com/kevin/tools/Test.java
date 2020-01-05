package com.kevin.tools;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * @author kevin
 * @ClassName
 * @Date 2020/1/510:00
 */
public class Test {

    public static void main(String[] args) throws Exception {
        String filepath = "C:\\Users\\kevin\\Desktop\\111.xlsx";
        File file = new File(filepath);
        FileInputStream inputStream = new FileInputStream(new File(filepath));
        List<List<Object>> list = ImportExcel.getBankListByExcel(inputStream, filepath);
        System.out.print(list);
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).size(); j++) {
                System.out.println(list.get(i).get(j) + "::" + j);
            }
        }
    }

}

