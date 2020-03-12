package com.chr.mybatisplus.controller;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author RAY
 * @descriptions 多线程复制多个文件
 * @since 2020/1/4
 */
public class MultiThreadDown {

    //创建线程池
    private static ThreadPoolExecutor pool = new ThreadPoolExecutor(10, 10, 200,
            TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(10));

    //目标目录
    private static final String targetPath = "D:\\java\\test\\datatransfer\\testTrue\\";

    public static void main(String[] args) throws Exception {


    }

    public static void transfer()throws Exception{
        List<File> list = listYunFiles("D:\\java\\test\\datatransfer\\mount");
        System.out.println(list);
        List<String> stringList = list.stream()
                .map(File::getPath)
                .collect(Collectors.toList());

        stringList.forEach((sourcePath) -> {
            String currentFile = sourcePath.substring(sourcePath.lastIndexOf("\\") + 1);
            try {
                //44秒
                //new Thread(new copyFile(sourcePath,targetPath+currentFile),currentFile).start();
                //使用线程池的方式复制 25
                pool.execute(new copyFile(sourcePath, targetPath + currentFile));
                //35
                //new copyFile(sourcePath,targetPath+currentFile).fileCopy2();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //pool.shutdownNow();
    }

    /**
     * 得出目录下的所有文件
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static List listYunFiles(String path) throws IOException {
        List<File> fileList = new ArrayList<>();
        traverseYunDir(path, fileList);
        return fileList;
    }

    /**
     * 递归遍历目录
     *
     * @param dirPath
     * @throws IOException
     */
    private static void traverseYunDir(String dirPath, List list) throws IOException {
        File path = new File(dirPath);
        File[] files = path.listFiles();
        /**将遍历结果放到fileList中*/
        for (File file : files) {
            if (file.isFile()) {
                list.add(file);
            } else if (file.isDirectory()) {
                traverseYunDir(file.toString(), list);
            }
        }
    }

    /**
     * 复制文件
     */
    static class copyFile implements Runnable {

        private String sourcePath;
        private String targetPath;

        public copyFile(String sourcePath, String targetPath) {
            this.sourcePath = sourcePath;
            this.targetPath = targetPath;
        }

        public boolean fileCopy() throws Exception {
            boolean flag = false;
            long startTime = System.currentTimeMillis();
            synchronized (this) {
                try (InputStream in = new FileInputStream(new File(sourcePath))) {
                    try (OutputStream out = new FileOutputStream(new File(targetPath))) {
                        byte[] buffer = new byte[4096];
                        int bytesToRead;
                        while ((bytesToRead = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesToRead);
                        }
                    }
                }
                System.out.println(Thread.currentThread().getName() + "拷贝成功");
                flag = true;
                long endTime = System.currentTimeMillis();
                System.out.println("一共花了" + (endTime - startTime) + "毫秒");
            }
            return flag;
        }

        public boolean fileCopy2() throws Exception {
            synchronized (this) {
                try (FileInputStream in = new FileInputStream(this.sourcePath)) {
                    try (FileOutputStream out = new FileOutputStream(this.targetPath)) {
                        FileChannel inChannel = in.getChannel();
                        FileChannel outChannel = out.getChannel();
                        ByteBuffer buffer = ByteBuffer.allocate(4096);
                        while (inChannel.read(buffer) != -1) {
                            buffer.flip();
                            outChannel.write(buffer);
                            buffer.clear();
                        }
                    }
                }
            }

            System.out.println("拷贝成功");
            return true;
        }

        @Override
        public void run() {
            try {
                fileCopy();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("拷贝失败");
            }
        }

        public static  <T,R> R getList(int[] t,Function<int[],R> fun){
            return fun.apply(t);
        }

        public static void main(String[] args) {
            int[] t = new int[]{1,2,3};
            List<String> list1 = getList(t, a -> {
                List<String> list = new ArrayList<>();
                for (int i : a) {
                    list.add("-"+i+"-");
                }
                return list;
            });
            System.out.println(list1);
            Map<String, Integer> map1 = getList(t, a -> {
                Map<String, Integer> map = new HashMap<>();
                for (int i : a) {
                    map.put("x" + i, i);
                }
                return map;
            });
            System.out.println(map1);
        }
    }
}
