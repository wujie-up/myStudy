package com.study.design.adapter;

import java.io.*;

public class IoAdapter {
    public static void main(String[] args) throws IOException {
        InputStream is = new FileInputStream("D://1.txt");
        // 字节流 转 字符流 的适配器
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        while (null != (line = br.readLine()) && !line.equals("")) {
            System.out.println(line);
        }
        br.close();
        is.close();
    }
}
