package nl.ordina.test.zookeeper;

import com.google.gson.Gson;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;

import java.net.InetAddress;
import java.util.*;
import java.util.stream.Collectors;

import static spark.Spark.get;
import static spark.Spark.port;

/**
 * Created by steven on 02-05-16.
 */
public class ZookeeperDemo {


    public static void main(String[] args) {
        try {
            //simple zookeeper example
            int port = Integer.parseInt(args[0]);


            ZookeeperClient zookeeperClient = new ZookeeperClient(args[1]);
            zookeeperClient.register(InetAddress.getLocalHost().getHostAddress() + ":" + port);

            //watch all registered nodes
            ServicesWatcher services = new ServicesWatcher(zookeeperClient);
            services.watch();

            Gson gson = new Gson();
            port(port);
            get("/registered", (request, response) -> services.getServices(), gson::toJson);

            //close the client on exit
            Runtime.getRuntime().addShutdownHook(new Thread(() -> { zookeeperClient.close(); }));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
