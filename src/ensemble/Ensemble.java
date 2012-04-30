package ensemble;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import client.Log.LogEntry;
import client.Log.LogEntry.Identifier;
import client.Log.LogEntry.Type;

import utility.Configuration;

public class Ensemble {
	final CircularBuffer buffer;
	List<InetSocketAddress> sortedChainSocketAddress;
	public Channel getSuccessorChannel() {
		return successorChannel;
	}
	public Channel getPredecessorChannel() {
		return predecessorChannel;
	}
	public HashMap<String, Channel> getHeadDbClients() {
		return headDbClients;
	}
	public HashMap<String, Channel> getTailDbClients() {
		return tailDbClients;
	}

	Channel successorChannel;//to send logs
	Channel predecessorChannel;//to send remove message
	HashMap<String, Channel> headDbClients = new HashMap<String, Channel>();//receive logs <clientId, channel>
	HashMap<String, Channel> tailDbClients = new HashMap<String, Channel>();//send ack
	Configuration conf;

	public Ensemble(Configuration conf, List<InetSocketAddress> sortedChainSocketAddress) throws Exception{
		this.conf=conf;
		this.sortedChainSocketAddress = sortedChainSocketAddress;
		if(sortedChainSocketAddress.size()<2)
			throw new Exception("obj.chain Ensemble size < 2 ");
		buffer = new NaiveCircularBuffer(conf.getEnsembleBufferSize());
	}
	public InetSocketAddress getSuccessorSocketAddress() throws Exception{
		int index = getLocalAddressIndex();
		if(index<0)
			throw new Exception("Obj.Ensemble. local index = -1");
		index= (index+1)%sortedChainSocketAddress.size();
		return sortedChainSocketAddress.get(index);
	}
	public InetSocketAddress getPredessessorSocketAddress() throws Exception{
		int index = getLocalAddressIndex();
		if(index<0)
			throw new Exception("Obj.Chain. local index = -1");
		index= (index+sortedChainSocketAddress.size()-1)%sortedChainSocketAddress.size();
		return sortedChainSocketAddress.get(index);
	}
	int getLocalAddressIndex(){
		for(int i = 0; i<sortedChainSocketAddress.size(); i++)
			if(sortedChainSocketAddress.get(i).equals(conf.getBufferServerSocketAddress()))
				return i;
		return -1;				
	}
	public List<InetSocketAddress> getSortedChainSocketAddress() {
		return sortedChainSocketAddress;
	}
	public void setSortedChainSocketAddress(
			List<InetSocketAddress> sortedChainSocketAddress) {
		this.sortedChainSocketAddress = sortedChainSocketAddress;
	}
	public Channel getSuccessor() {
		return successorChannel;
	}
	public void setSuccessor(Channel successor) {
		this.successorChannel = successor;
	}
	public Channel getPredecessor() {
		return predecessorChannel;
	}
	public void setPredecessor(Channel predecessor) {
		this.predecessorChannel = predecessor;
	}
	public CircularBuffer getBuffer() {
		return buffer;
	}

	public boolean addHeadDBClient(String clientId, String clientSocketAddress, Channel channel) throws Exception{
		LogEntry tailNotify = LogEntry.newBuilder()
				.setMessageType(Type.TAIL_NOTIFICATION)
				.setEntryId(Identifier.newBuilder().setClientId(clientId))
				.setClientSocketAddress(clientSocketAddress)
				.build();
		ChannelFuture future;
		if(predecessorChannel.isOpen())
			future = predecessorChannel.write(tailNotify);
		else
			throw new Exception("Predecessor channel is Closed!" + predecessorChannel);
		//	if(future.isSuccess()){
		headDbClients.put(clientId, channel);
		return true;
		//}
		//	else
		//		return false;
	}
	public void removeHeadDbClient(String clientId){
		headDbClients.remove(clientId);
	}
	public void addTailDBClient(String clientId, Channel tailChannel){
		tailDbClients.put(clientId, tailChannel);
	}
	public void removeTailDbClient(String clientId){
		tailDbClients.remove(clientId);
	}
	public void close(){
		successorChannel.close();
		predecessorChannel.close();
		for(Channel ch : tailDbClients.values())
			ch.close();
	}

	public void entryPersisted(final LogEntry entry) throws Exception{
		buffer.remove(entry.getEntryId());
		ChannelFuture channelFuture = null;
		if(!headDbClients.containsKey(entry.getEntryId().getClientId()))
			if(predecessorChannel.isOpen())
				channelFuture = predecessorChannel.write(entry);
			else
				throw new Exception("Predecessor channel is Closed!" +  predecessorChannel);

		if(channelFuture!=null)
			channelFuture.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					// TODO Auto-generated method stub
					if(!future.isSuccess())
						throw new Exception("Persisted Message failed to deliver." +  future.getCause());
				}
			});
	}

	public boolean addToBuffer(final LogEntry entry) throws Exception{
		Channel channel = tailDbClients.get(entry.getEntryId().getClientId());
		ChannelFuture future;
		if(channel==null)
			channel = successorChannel;
		
		if(channel==null || !channel.isOpen())
			throw new Exception("Attempt to send data while channel is not open or its null. Chenl: " + channel );
		else
			future = channel.write(ackMessage(entry.getEntryId()));

		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				// TODO Auto-generated method stub
				if(!future.isSuccess())
					throw new Exception("Failed to send entry to destination(Ack t client or the log to next buffer server)." + future.getCause());
				else
					System.out.println("Msg Buffered and delivered" +  entry.getEntryId());
			}
		});
		buffer.add(entry);
		return true;
	}

	LogEntry ackMessage(Identifier id){
		return LogEntry.newBuilder().setEntryId(id)
				.setClientSocketAddress(conf.getBufferServerSocketAddress().toString())
				.setMessageType(Type.ACK).build();
	}

	public void removeClient(){

	}
}
