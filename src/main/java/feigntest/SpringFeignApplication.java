package feigntest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import feign.Feign;
import feign.Headers;
import feign.Logger;
import feign.RequestLine;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.httpclient.ApacheHttpClient;

@SpringBootApplication
public class SpringFeignApplication implements CommandLineRunner {
	protected final Log logger = LogFactory.getLog(getClass());
	
	public static void main(String[] args) {
		SpringApplication.run(SpringFeignApplication.class, args);
	}

	static class SimplePojo {
		String name;
		int value;
	}
	
	interface LocalPatch {

	    @RequestLine("PATCH /")
	    @Headers("Content-Type: application/json")
	    SimplePojo patch(SimplePojo test);

	    @RequestLine("POST /")
	    @Headers("Content-Type: application/json")
	    SimplePojo post(SimplePojo test);
/*
	    default SimplePojo patch(SimplePojo simplePojo) {
			simplePojo.name = simplePojo.name + "Changed";
			simplePojo.value= simplePojo.value + 5;
			return simplePojo;
	    }
*/

	    static LocalPatch connect() {
    	ApacheHttpClient apacheHttpClient = new ApacheHttpClient();
	      return Feign.builder()    		  
    		  .client(apacheHttpClient)
	          .decoder(new GsonDecoder())
	          .encoder(new GsonEncoder())
//	          .errorDecoder(new GitHubErrorDecoder(decoder))
	          .logger(new Logger.ErrorLogger())
	          .logLevel(Logger.Level.BASIC)
	          .target(LocalPatch.class, "http://localhost:8080");
	    }

	  }
	
	@Override
	public void run(String... arg0) throws Exception {
		logger.info("Application Started !!");
		LocalPatch localPatch = LocalPatch.connect();
		logger.info("Application Connected !!");
		SimplePojo simplePojo = new SimplePojo();
		simplePojo.name = "my test";
		simplePojo.value = 5;
		
		simplePojo = localPatch.post(simplePojo);
		logger.info("POST Called !!");
		System.out.println(simplePojo.name);

		simplePojo.name = "my test";
		simplePojo.value = 5;
		simplePojo = localPatch.patch(simplePojo);
		logger.info("PATCH Called !!");
		System.out.println(simplePojo.name);
	}
	
}
