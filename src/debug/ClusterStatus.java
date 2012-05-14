package debug;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import com.google.protobuf.InvalidProtocolBufferException;

import coordination.Znode.ServerData;
import coordination.Znode.ServersGlobalView;
import coordination.ZookeeperClient;

import utility.Configuration;

public class ClusterStatus implements Watcher{
	public ZookeeperClient zk;	
	Configuration conf = new Configuration("applicationProperties");
	public ClusterStatus(){
		try {
			zk = new ZookeeperClient(this, conf);
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * @param args
	 * @throws InterruptedException 
	 */

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ClusterStatus cs = new ClusterStatus();
		while(true){
			try {
				Thread.currentThread().sleep(6000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
			cs.printServers();
		//	printClient();
			cs.printGlobalView();
		}
	}
	private  void printGlobalView() {
		ServersGlobalView gv = zk.getServersGlobalView();
		System.out.println("Global View: \n"+gv);
	}
	private void printClient() {
		// TODO Auto-generated method stub
		//System.out.println("Global View: \n"+gv);
	}
	private  void printServers() {
		// TODO Auto-generated method stub
		try {
			List<ServerData> serversList = zk.getSortedServersList();
			System.out.println("Servers: " + serversList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void process(WatchedEvent event) {
		// TODO Auto-generated method stub

	}

}
