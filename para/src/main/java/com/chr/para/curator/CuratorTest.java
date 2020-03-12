package com.chr.para.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author RAY
 */
public class CuratorTest {

    private static final String PATH = "example/pathCache";

    private static CuratorFramework client =null;

    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("192.168.137.1:2181")
                .sessionTimeoutMs(5000)  // 会话超时时间
                .connectionTimeoutMs(5000000) // 连接超时时间
                .retryPolicy(retryPolicy)
                .namespace("base") // 包含隔离名称
                .build();
        client.start();
//        client.create().creatingParentContainersIfNeeded() // 递归创建所需父节点
//                .withMode(CreateMode.PERSISTENT) // 创建类型为持久节点
//                .forPath("/nodeA/nodeC", "data3".getBytes()); // 目录及内容

        boolean isZkCuratorStarted = client.getState() == CuratorFrameworkState.STARTED;
        System.out.println("当前客户的状态：" + (isZkCuratorStarted ? "连接中" : "已关闭"));

        //测试检查某个节点是否存在
        Stat stat1 = client.checkExists().forPath("/nodeA");

        System.out.println("'/nodeA'是否存在： " + (stat1 != null ? "是" : "否"));

        byte[] bytes = client.getData().forPath("/nodeA");
        System.out.println(new String(bytes));
        List<String> list = client.getChildren().forPath("/nodeA");
        list.forEach(n->{
            try {
                byte[] bytes1 = client.getData().forPath("/nodeA/"+n);
                System.out.println(new String(bytes1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
//        client.setData()
//                .forPath("/nodeA", "data".getBytes());
//
//        byte[] bytes2 = client.getData().forPath("/nodeA");
//        System.out.println(new String(bytes2));
    }

    @Before
    public void createClient(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString("192.168.137.1:2181")
                .sessionTimeoutMs(5000)  // 会话超时时间
                .connectionTimeoutMs(5000000) // 连接超时时间
                .retryPolicy(retryPolicy)
                .namespace(PATH) // 包含隔离名称
                .build();
    }

    @Test
    public void pathCache() throws Exception{
        client.start();
        PathChildrenCache cache = new PathChildrenCache(client, "/"+PATH, true);
        cache.start();
        PathChildrenCacheListener cacheListener = (client1, event) -> {
            System.out.println("事件类型：" + event.getType());
            if (null != event.getData()) {
                System.out.println("节点数据：" + event.getData().getPath() + " = " + new String(event.getData().getData()));
            }
        };
        //添加监听器
        cache.getListenable().addListener(cacheListener);

        client.create().creatingParentsIfNeeded().forPath("/example/pathCache/test01", "01".getBytes());
        Thread.sleep(10);
        client.create().creatingParentsIfNeeded().forPath("/example/pathCache/test02", "02".getBytes());
        Thread.sleep(10);
        //修改test01节点的内容
        client.setData().forPath("/example/pathCache/test01", "01_V2".getBytes());
        Thread.sleep(10);
        for (ChildData data : cache.getCurrentData()) {
            System.out.println("getCurrentData:" + data.getPath() + " = " + new String(data.getData()));
        }
        //删除节点
        client.delete().forPath("/example/pathCache/test01");
        Thread.sleep(10);
        client.delete().forPath("/example/pathCache/test02");
        Thread.sleep(1000 * 5);
        cache.close();
        client.close();
        System.out.println("OK!");

    }


    @Test
    public void demo2() throws Exception{
        CuratorApi.INSTANCE.setExpiredTime(5*1000).createNode("test","222");
        Thread.sleep(2*1000);
        String test = CuratorApi.INSTANCE.getTokenByNode("test");
        System.out.println(test);
        Thread.sleep(3*1000);
        String test1 = CuratorApi.INSTANCE.getTokenByNode("test");
        System.out.println(test1);
    }

    @Test
    public void demo4() throws Exception{
        List<String> list = Arrays.asList("a","b","c");
        List<User> list1 = new ArrayList<>();
        User user = null;
        for (String s : list) {
            user = new User();
            user.setX(s);
            list1.add(user);
        }
        System.out.println(list1);

        user = new User("d");
        User x = user;
        System.out.println(x==user);
    }

    public class User{
        private String x;

        public void setX(String x) {
            this.x = x;
        }

        public User(String x) {
            this.x = x;
        }

        public User() {
        }

        @Override
        public String toString() {
            return "User{" +
                    "x=" + x +
                    '}';
        }
    }


}
