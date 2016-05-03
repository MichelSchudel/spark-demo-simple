package nl.ordina.test.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by steven on 03-05-16.
 */
public class ZookeeperClient {
    public static final String SPARK_SIMPLE_DEMO = "/spark-simple-demo";

    private final CuratorFramework curatorFramework;

    public ZookeeperClient(String zookeeperHost) {
        try {
            curatorFramework = CuratorFrameworkFactory.newClient(zookeeperHost, new RetryNTimes(3, 100));
            curatorFramework.start();
            curatorFramework.blockUntilConnected();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void register(String url) throws Exception {
        String znode = SPARK_SIMPLE_DEMO + "/_";
        String znodePath = curatorFramework
                .create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath(znode, url.getBytes());
    }

    public List<String> getAllRegisteredServices() throws Exception {
        String znode = SPARK_SIMPLE_DEMO;
        return curatorFramework.getChildren().forPath(znode).stream().map(path -> {
            try {
                return new String(curatorFramework.getData().forPath(ZKPaths.makePath(znode, path)));
            } catch (Exception e) {
                return null;
            }
        })
                .filter(uri -> uri != null)
                .collect(Collectors.toList());
    }

    public void watch(Watcher watcher) {
        try {
            curatorFramework.getChildren().usingWatcher(watcher).forPath(SPARK_SIMPLE_DEMO);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
