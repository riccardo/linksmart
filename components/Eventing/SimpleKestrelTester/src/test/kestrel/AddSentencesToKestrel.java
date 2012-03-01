package test.kestrel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import backtype.storm.spout.KestrelClient;
import backtype.storm.spout.KestrelClient.Item;
import backtype.storm.spout.KestrelClient.ParseError;

public class AddSentencesToKestrel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
				
		InputStream is = System.in;
				
			char closing_bracket = ']';
			
			int val = closing_bracket;
			
			boolean aux = true;
			
			try {
				
				KestrelClient kestrelClient = null;
				String queueName = "niceSentences";
				
				while(aux){
					
					kestrelClient = new KestrelClient("localhost",22133);
					
//					kestrelClient.flush(queueName);
//					queueSentenceItems(kestrelClient, queueName);
					dequeueItems(kestrelClient, queueName);
					
					kestrelClient.close();
					
					Thread.sleep(1000);
					
					if(is.available()>0){
					 if(val==is.read())
						 aux=false;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (ParseError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("end");

	}
	
	private static void queueSentenceItems(KestrelClient kestrelClient, String queueName)
			throws ParseError, IOException {
		
		String[] sentences = new String[] {
	            "the cow jumped over the moon",
	            "an apple a day keeps the doctor away",
	            "four score and seven years ago",
	            "snow white and the seven dwarfs",
	            "i am at two with nature"};
		
		Random _rand = new Random();
		
		for(int i=1; i<=10; i++){
			
			String sentence = sentences[_rand.nextInt(sentences.length)];
			
			String val = "ID " + i + "\r\n" + sentence + "\r\n" + "satzinhalt";
			
			boolean queueSucess = kestrelClient.queue(queueName, val);
			
			System.out.println("queueSucess=" +queueSucess+ " [" + val +"]");
		}
	}
	
	private static void dequeueItems(KestrelClient kestrelClient, String queueName) throws IOException, ParseError
			 {
		for(int i=1; i<=12; i++){
			
			Item item = kestrelClient.dequeue(queueName);
			
			if(item==null){
				System.out.println("The queue (" + queueName + ") contains no items.");
			}
			else
			{
				byte[] data = item._data;
				
				String receivedVal = new String(data);
				
				System.out.println(receivedVal.contains("\r\n"));
				
				System.out.println("receivedItem=" + receivedVal);
			}
		}
	}

	private static void dequeueAndRemoveItems(KestrelClient kestrelClient, String queueName) throws IOException, ParseError
		 {
			for(int i=1; i<=12; i++){
				
				Item item = kestrelClient.dequeue(queueName);
				
				
				if(item==null){
					System.out.println("The queue (" + queueName + ") contains no items.");
				}
				else
				{
					int itemID = item._id;
					
					
					byte[] data = item._data;
					
					String receivedVal = new String(data);
					
					kestrelClient.ack(queueName, itemID);
					
					System.out.println("receivedItem=" + receivedVal);
				}
			}
	}

}
