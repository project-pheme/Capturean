package atos.knowledgelab.capture.stream.translator.test;

import java.util.HashMap;
import java.util.Map;

import atos.knowledgelab.capture.stream.translator.MessageReceiver;

public class CheckThrottling {

	public static void main(String args[]) {
		
		Map<String, Integer> map = new HashMap<>();
		map.put("818ef321", 5);
		map.put("c51f8d33", 1831);
		map.put("6a8c73b2", 64);

		
		for (int i = 0; i < 10; i++) {
			
			int sum = 0;
//			for (String s : map.keySet()) {
//				sum += map.get(s);
//			}
			//System.out.println("Global prob. for i = " + i + " : " + MessageReceiver.calculateGlobalProbability(i));
			
			//ñSystem.out.println("Loc prob. for i = " + (loc) + " : " + MessageReceiver.calculateLocalProbability(loc, map, t));
			for (String s : map.keySet()) {
				double l = MessageReceiver.calculateLocalProbability(map.get(s), map, 180, 10);
				double g = MessageReceiver.calculateGlobalProbability(sum, 1.0/4.0, 100);
				System.out.println("Count: " + map.get(s) + " Loc: " + l + " glob: " + g + " | Total: " + (l * g));
				
				//System.out.println(" -> " + map.get(s) + "  log: " + Math.log(map.get(s)) + "  prob: " + l);
				
			}
			
			System.out.println();
		}

	}
}
