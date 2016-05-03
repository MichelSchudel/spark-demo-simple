package nl.ordina.test.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by steven on 03-05-16.
 */
class ServicesWatcher implements Watcher {

    private ZookeeperClient zookeeperClient;
    private List<Service> services;

    public ServicesWatcher(ZookeeperClient zookeeperClient) throws Exception {
        this.zookeeperClient = zookeeperClient;
    }

    public void watch() throws Exception {
        this.services = zookeeperClient.getAllRegisteredServices().stream().map(Service::new).collect(Collectors.toList());
        this.zookeeperClient.watch(this);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            this.services = zookeeperClient.getAllRegisteredServices().stream().map(Service::new).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        zookeeperClient.watch(this);
    }

    public List<Service> getServices() {
        return services;
    }
}
