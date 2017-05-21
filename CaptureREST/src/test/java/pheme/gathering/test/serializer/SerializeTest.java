package pheme.gathering.test.serializer;



import java.io.IOException;
import java.util.ArrayList;

import org.apache.solr.common.SolrInputDocument;

import atos.knowledgelab.capture.exception.CaptureException;
import atos.knowledgelab.capture.persistence.CollectionSerializer;

public class SerializeTest {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		
		ArrayList<SolrInputDocument> collection = new ArrayList<SolrInputDocument>();
		SolrInputDocument si = new SolrInputDocument();
		si.addField("tipo", "es un tipo");
				
		collection.add(si);

		try {
			CollectionSerializer.serializeToFile(collection, "file.txt");
			
			collection = CollectionSerializer.deserializeFromFile("file.txt");
			System.out.println(collection.get(0));
			
		} catch (CaptureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
