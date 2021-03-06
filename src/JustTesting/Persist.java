package JustTesting;



public class Persist extends Thread{

	boolean running = true;
	HashedBuffer buffer;
	int time ;
	public Persist(HashedBuffer buffer, int time ){
		this.buffer = buffer;
		this.time = time;
	}
	public void stopThread(){
		running = false;
		interrupt();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(running){
			try {
				LogEntry entry = buffer.nextToPersist();
				Thread.sleep(time);
				System.out.println("PERSISTED LL "+ entry.getEntryId().getMessageId());
				buffer.remove(entry.getEntryId());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//buffer.remove();
		}
		
	}

}
