/*
 * Copyright (c) 2003, David Brackeen
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of David Brackeen nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * Class for modeling a pool of Threads to be used for event publication 
 *
 * Adapted by LinkSmart
 */

package eu.linksmart.external;

import java.util.Iterator;
import java.util.LinkedList;

import eu.linksmart.eventmanager.impl.data.Publication;
import eu.linksmart.eventmanager.impl.data.Topic;


public class ThreadPool extends ThreadGroup {

	private boolean isAlive;
	private LinkedList<Publication> taskQueue;
	private int threadID;
	private static int threadPoolID;


	public ThreadPool() {
		super("ThreadPool-"+(threadPoolID++));
		setDaemon(true);
		isAlive = true;
		this.taskQueue = new LinkedList<Publication>();

		new PooledThread().start();
	}

	public synchronized void runTask(Publication task) {
		if(!isAlive) {
			throw new IllegalStateException();
		}
		if(task!=null) {
			addNewTask(task);
			notify();
		}
	}

	public synchronized void updateTasks(String topic, int priority) {

		Iterator<Publication> pubsIterator = taskQueue.iterator();
		while(pubsIterator.hasNext()) {
			Publication currentPub = pubsIterator.next();
			if(currentPub.getTopic().getTopicName().equals(topic)) {
				taskQueue.remove(currentPub);
				addNewTask(new Publication(currentPub.getThread(), new Topic(topic,priority)));
			}
		}
	}

	private synchronized void addNewTask(Publication newPub) {
		if(taskQueue.size() == 0) {
			taskQueue.addFirst(newPub);
			return;
		}
		Iterator<Publication> pubsIter = taskQueue.iterator();
		int currentPos = 0;
		boolean inserted = false;
		while(pubsIter.hasNext()) {
			Publication current = pubsIter.next();
			if(current.getTopic().getPriority() > newPub.getTopic().getPriority()) {
				taskQueue.add(currentPos, newPub);
				inserted = true;
				break;
			}
			else {
				currentPos++;
			}
		}
		if(!inserted) 
			taskQueue.add(newPub);		
	}

	protected synchronized Runnable getTask() throws InterruptedException {
		while(taskQueue.size() == 0) {
			if(!isAlive) {
				return null;
			}
			wait();
		}
		return (Runnable) taskQueue.removeFirst().getThread();
	}

	public synchronized void close() {
		if(isAlive) {
			isAlive = false;
			taskQueue.clear();
			interrupt();
		}
	}

	public void join() {

		synchronized(this) {
			isAlive = false;
			notifyAll();
		}
		Thread[] threads = new Thread[activeCount()];
		int count = enumerate(threads);
		for(int i= 0; i < count; i++) {
			try{
				threads[i].join();
			} catch(InterruptedException ex) {ex.printStackTrace();}
		}
	}

	private class PooledThread extends Thread {

		public PooledThread() {
			super(ThreadPool.this, "PooledThread-"+(threadID++));
		}

		public void run() {
			while(!isInterrupted()) {
				Runnable task = null;
				try {
					task = getTask();
				}catch(InterruptedException ex) {
					ex.printStackTrace();
				}

				if(task == null) {
					return;
				}

				try{
					task.run();
				}catch(Throwable t) {
					uncaughtException(this, t);
				}
			}
		}
	}
}


