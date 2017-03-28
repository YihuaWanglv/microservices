package com.iyihua.microservices.service.registry.core;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Component
public class ServiceRegistryImpl implements ServiceRegistry, Watcher {

	private static final int SESSION_TIMEOUT = 5000;
	private static final String REGISTRY_PATH = "/registry";

	private static Logger logger = LoggerFactory.getLogger(ServiceRegistryImpl.class);

	private static CountDownLatch latch = new CountDownLatch(1);

	private ZooKeeper zk;

	public ServiceRegistryImpl() {

	}

	public ServiceRegistryImpl(String zkServers) {
		try {
			// create zk client
			zk = new ZooKeeper(zkServers, SESSION_TIMEOUT, this);
			latch.await();
			logger.info("connected to zookeeper");
		} catch (Exception e) {
			logger.error("create zookeeper client failed.", e);
		}
	}

	@Override
	public void process(WatchedEvent event) {
		if (event.getState() == Event.KeeperState.SyncConnected) {
			latch.countDown();
		}
	}

	@Override
	public void register(String serviceName, String serviceAddress) {
		try {
			// create root node(PERSISTENT)
			String registryPath = REGISTRY_PATH;
			if (zk.exists(registryPath, false) == null) {
				zk.create(registryPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				logger.info("create registry node: {}", registryPath);
			}
			
			// create service node(PERSISTENT)
			String servicePath = registryPath + "/" + serviceName;
			if (zk.exists(servicePath, false) == null) {
				zk.create(servicePath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				logger.info("create service node: {}", servicePath);
			}
			
			// create address node()
			String addressPath = servicePath + "/address-";
			String addressNode = zk.create(addressPath, serviceAddress.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			logger.info("create address node: {} => {}", addressNode, serviceAddress);
			
		} catch (Exception e) {
			logger.error("create node failed.", e);
		}
	}

}
