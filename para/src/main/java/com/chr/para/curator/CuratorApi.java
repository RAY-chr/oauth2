package com.chr.para.curator;

import com.chr.para.utils.PropertiesUtils;
import com.chr.para.utils.PropertyEnum;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author RAY
 * @since 2019/12/27
 */
public class CuratorApi {


    //声明隔离名称 存储令牌节点
    private static final String PATH = "TokenNode";

    //操作acl权限,ZooKeeper提供了几种验证模式,现采用Client端由用户名和密码验证，譬如user:password
    private static final String SCHEME = "digest";

    public static final CuratorApi INSTANCE = new CuratorApi();

    private static Logger logger = LoggerFactory.getLogger(CuratorApi.class);

    //存储节点的创造时间，以便设置过期时间
    private static final Map<String,Long> MAP = new HashMap<>();

    //默认过期时间
    private long EXPIREDTIME = 3600 * 1000;

    private static CuratorFramework client = null;

    //连接的zookeeper的url地址
    private static String connectString = null;

    //zookeeper的acl权限认证用户名密码
    private static String userPassword = null;

    /**
     * 连接zookeeper，得到Curator client
     */
    static {
        init();
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                //认证授权，登录用户
                .authorization(SCHEME, userPassword.getBytes())
                .connectString(connectString)
                .sessionTimeoutMs(5000)  // 会话超时时间
                .connectionTimeoutMs(5000) // 连接超时时间
                .retryPolicy(retryPolicy)
                .namespace(PATH) // 包含隔离名称
                .build();
        logger.info("Curator'client build succeessfully");
        client.start();
        logger.info("Curator client start ");
    }

    public static void init() {
        PropertiesUtils instance = PropertiesUtils.getInstance();
        connectString = instance.getValue(PropertyEnum.ZOOKEEPER_URL);
        userPassword = instance.getValue(PropertyEnum.ZOOKEEPER_USERPASSWORD);
    }


    /**
     * 设置节点的过期时间，时间单位是毫秒
     * @param time
     * @return
     */
    public CuratorApi setExpiredTime(long time){
        this.EXPIREDTIME = time;
        return this;
    }

    /**
     * 创建节点
     * @param nodeName 节点名
     * @param token 令牌
     * @throws Exception
     */
    public void createNode(String nodeName, String token) throws Exception {
        MAP.put(nodeName,System.currentTimeMillis()+EXPIREDTIME);
        if (userPassword.length() != 0) {
            client.create()
                    .withMode(CreateMode.PERSISTENT) // 创建类型为永久节点
                    .withACL(getAcl()) //使用Acl给节点增加权限
                    .forPath("/" + nodeName, token.getBytes(StandardCharsets.UTF_8)); // 目录及内容
        } else {
            client.create()
                    .withMode(CreateMode.PERSISTENT) // 创建类型为永久节点
                    .forPath("/" + nodeName, token.getBytes(StandardCharsets.UTF_8)); // 目录及内容
        }
        logger.info("That create node of '{}' successfully ", nodeName);
    }

    /**
     * 拿到令牌
     * @param nodeName
     * @return
     * @throws Exception
     */
    public String getTokenByNode(String nodeName) throws Exception {
        if (!checkNodeExists(nodeName)){
            return null;
        }
        if (checkExpired(nodeName)){
            logger.info("The node of {} has been expired",nodeName);
            return null;
        }
        byte[] bytes = client.getData().forPath("/" + nodeName);
        if (bytes != null) {
            logger.info("That read node of '{}' successfully ", nodeName);
            return new String(bytes);
        }
        return null;
    }


    /**
     * 根据节点名删除节点信息
     *
     * @param batchNo
     * @throws Exception
     */
    public void deleteBatchNoNode(String nodeName) throws Exception {
        if (checkNodeExists(nodeName)) {
            client.delete().forPath("/" + nodeName);
            logger.info("That delete node of '{}' successfully", nodeName);
        }else {
            logger.info("node not exist");
        }
    }


    /**
     * 检查节点是否存在
     *
     * @param nodeName
     * @return
     */
    public boolean checkNodeExists(String nodeName) {
        Stat stat = null;
        try {
            stat = client.checkExists().forPath("/" + nodeName);
            return stat != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查节点是否过期
     * @param nodeName
     * @return
     * @throws Exception
     */
    public boolean checkExpired(String nodeName)throws Exception{
        long currentTimeMillis = System.currentTimeMillis();
        boolean flag = currentTimeMillis > (MAP.get(nodeName));
        if (flag){
            deleteBatchNoNode(nodeName);
        }
        return flag;
    }

    /**
     * 给操作节点增加权限 (在客户端提示没权限 输入addauth digest 用户名:密码明文)
     *
     * @return
     * @throws Exception
     */
    public List<ACL> getAcl() throws Exception {
        List<ACL> acls = new ArrayList<ACL>();
        Id user = new Id(SCHEME, getDigestUserPwd(userPassword));
        acls.add(new ACL(ZooDefs.Perms.ALL, user));
        return acls;
    }

    /**
     * 给用户名和密码加密
     *
     * @param id 用户名：密码 （格式）
     * @return
     * @throws Exception
     */
    public static String getDigestUserPwd(String id) throws Exception {
        // 加密明文密码
        return DigestAuthenticationProvider.generateDigest(id);
    }
}
