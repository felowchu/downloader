package club.bestchu.downloader;

import java.util.concurrent.CountDownLatch;

public class Downloader {
    public static void main(String[] args){
        int threadSize = 4;
        String serverPath = "http://mirror.bit.edu.cn/apache/hadoop/common/hadoop-3.0.0-alpha4/hadoop-3.0.0-alpha4-src.tar.gz";
        String localPath = "h://hadoop.tar.gz";
        CountDownLatch latch = new CountDownLatch(threadSize);

        MultiThreadDownLoader downLoader = new MultiThreadDownLoader(threadSize, serverPath, localPath, latch);

        long startTime = System.currentTimeMillis();

        try{
            downLoader.download();
            latch.await();
        }catch (Exception e){
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("全部下载共耗时："+ (endTime - startTime));
    }
}
