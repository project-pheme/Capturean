package atos.knowledgelab.capture.monitor.live;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import atos.knowledgelab.capture.monitor.live.beans.Aggregate;
import atos.knowledgelab.capture.monitor.live.beans.Aggregates;
import atos.knowledgelab.capture.monitor.live.beans.Event;
import atos.knowledgelab.capture.monitor.live.beans.Sample;
import atos.knowledgelab.capture.monitor.live.beans.Samples;
import atos.knowledgelab.pheme.format.v2.PhemeSource;

public class TestSerialize {

	public static void main(String[] args) throws JsonProcessingException {
		
		
		
		ObjectMapper om = new ObjectMapper();
		om.enable(SerializationFeature.INDENT_OUTPUT);
		om.setSerializationInclusion(Include.NON_NULL);
		
		Samples ss = new Samples();
		
		ss.setName("samples");
		 
		Sample s = new Sample();
		List<Sample> slist = new ArrayList<>();
		
		Event e = new Event();
		
		PhemeSource ps = new PhemeSource();
		
		ps.setCreatedAt("2017-06-01T21:21:00.111Z");
		ps.setText("Hello world!");
		
		e.setMessage(ps);
		s.setEvent(e);
		slist.add(s);
		
		ss.setSamples(slist);
		
		System.out.println(om.writeValueAsString(ss));
		
		
		Aggregates aa = new Aggregates();
		
		Aggregate a = new Aggregate();
		
		a.getAll().put("DC1", 21L);
		a.getAll().put("DC2", 11L);
		
		aa.setName("aggregated");
		aa.setAggregate(a);
		
		System.out.println(om.writeValueAsString(aa));
	}
}
