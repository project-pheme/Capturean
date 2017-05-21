/*******************************************************************************
 * Copyright (C) 2016  ATOS Spain S.A.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *      Mateusz Radzimski (ATOS, ARI, Knowledge Lab)
 *      Iván Martínez Rodriguez (ATOS, ARI, Knowledge Lab)
 *      María Angeles Sanguino Gonzalez (ATOS, ARI, Knowledge Lab)
 *      Jose María Fuentes López (ATOS, ARI, Knowledge Lab)
 *      Jorge Montero Gómez (ATOS, ARI, Knowledge Lab)
 *      Ana Luiza Pontual Costa E Silva (ATOS, ARI, Knowledge Lab)
 *      Miguel Angel Tinte García (ATOS, ARI, Knowledge Lab)
 *      
 *******************************************************************************/
package atos.knowledgelab.capture.client.messages;

import java.util.ArrayList;
import java.util.Observable;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;

import atos.knowledgelab.capture.bean.stream.KafkaMessage;
import atos.knowledgelab.capture.bean.stream.StreamItem;



public class MessageDispatcher<T> extends Observable {

	/*
	
	//experimental code
	ConcurrentLinkedQueue<KafkaMessage> queue = new ConcurrentLinkedQueue<KafkaMessage>();
	ConcurrentLinkedDeque<StreamItem> dequeue = new ConcurrentLinkedDeque<StreamItem>();
	Boolean batchMode = false;
	Integer batchSize = 10;
	*/
	
//	private () {
//		// TODO Auto-generated constructor stub
//	}
//
//	/**
//	 * SingletonHolder is loaded on the first execution of
//	 * Singleton.getInstance() or the first access to SingletonHolder.INSTANCE,
//	 * not before.
//	 */
//	private static class SingletonHolder {
//		public static final MessageDispatcher INSTANCE = new MessageDispatcher();
//	}
//
//	public static MessageDispatcher getInstance() {
//		return SingletonHolder.INSTANCE;
//	}

	public MessageDispatcher() {
		
	}
	
	//the method that receives all messages from kafka clients
	public void offer(T msg) {

		setChanged();
		notifyObservers(msg);
		
	}
	
	/*
	public StreamItem getLatest() throws InterruptedException {
		return this.dequeue.peekFirst();
	}
	
	public KafkaMessage poll() throws InterruptedException {
		return queue.poll();
	}
	*/
}
