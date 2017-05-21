package atos.knowledgelab.capture.dashboard.stream;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import atos.knowledgelab.capture.importer.beans.DashboardDocument;
import atos.knowledgelab.capture.importer.beans.DashboardDocumentFeatures;
import atos.knowledgelab.capture.importer.beans.SDQC;
import atos.knowledgelab.capture.importer.beans.SDQCKV;

public class SerializeTest {

	public static void main(String[] args) throws JsonProcessingException {
		// TODO Auto-generated method stub
		ObjectMapper om = new ObjectMapper();
		om.enable(SerializationFeature.INDENT_OUTPUT);
		om.setSerializationInclusion(Include.NON_NULL);
		
		DashboardDocument dd = new DashboardDocument();
		
		DashboardDocumentFeatures psf = new DashboardDocumentFeatures();
		
		SDQCKV sdqc = new SDQCKV();
		sdqc.setLabel("label");
		sdqc.setProbability(0.1234);
		
		psf.setSdqc(sdqc);
		dd.setFeatures(psf);
		
		System.out.println(om.writeValueAsString(dd));
		
	}

}
