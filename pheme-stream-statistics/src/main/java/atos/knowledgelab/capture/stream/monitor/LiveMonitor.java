package atos.knowledgelab.capture.stream.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class LiveMonitor extends Observable {

	List<Object> list = new ArrayList<>();
	
	public void tick(List<Object> list) {
		//System.out.println("Tick! Live");
		
		this.list = list;
		
		this.setChanged();
		this.notifyObservers(list);
	}
	
	
}
