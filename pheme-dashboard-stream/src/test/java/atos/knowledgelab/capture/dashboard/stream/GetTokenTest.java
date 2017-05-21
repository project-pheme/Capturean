package atos.knowledgelab.capture.dashboard.stream;

import java.io.IOException;

public class GetTokenTest {

	public static void main(String[] args) throws IOException {
		
		System.out.println("Get token...");
		APICaller apic = new APICaller("", "https://api.weblyzard.com/0.1/documents/pheme.weblyzard.com/api");
		//APICaller apic = new APICaller("", "https://api.weblyzard.com/0.1/documents/pheme.weblyzard.com/pheme_reddit_dataset");
		try {
			String token = apic.getToken();
			System.out.println("Security token: \n" + token);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
