package atos.knowledgelab.capture.dashboard.stream;

import java.io.IOException;

import atos.knowledgelab.pheme.format.v2.PhemeSource;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;



public class DeSerializeTest {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		ObjectMapper om = new ObjectMapper();
		om.enable(SerializationFeature.INDENT_OUTPUT);
		om.setSerializationInclusion(Include.NON_NULL);
		
		String obj = "{\n" + 
				"  \"created_at\": \"Wed Aug 17 13:29:23 +0000 2016\",\n" + 
				"  \"metadata\": {\n" + 
				"    \"result_type\": \"recent\",\n" + 
				"    \"iso_language_code\": \"en\"\n" + 
				"  },\n" + 
				"  \"place\": null,\n" + 
				"  \"in_reply_to_status_id_str\": null,\n" + 
				"  \"pheme_sdqc\": [\n" + 
				"    \"support\",\n" + 
				"    0.4952161412442118\n" + 
				"  ]\n" + 
				"}";
		
		PhemeSource dd = om.readValue(obj, PhemeSource.class);
		
		//System.out.println(dd.getSdqc().getLabel());
		
	}

}
