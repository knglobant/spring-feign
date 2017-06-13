package feigntest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import feign.Feign;
import feign.Logger;
import feign.RequestLine;
import feign.codec.Decoder;
import feign.gson.GsonDecoder;

@SpringBootApplication
public class SpringFeignApplication implements CommandLineRunner {
	protected final Log logger = LogFactory.getLog(getClass());
	
	public static void main(String[] args) {
		SpringApplication.run(SpringFeignApplication.class, args);
	}

	interface LocalPatch {

		public class SimplePojo {
			String name;
			int value;
		}

	    @RequestLine("PATCH /")
	    /** send and receive simple pojo. */

	    default SimplePojo patch(SimplePojo simplePojo) {
			simplePojo.name = simplePojo.name + "Changed";
			simplePojo.value= simplePojo.value + 5;
			return simplePojo;
	    }


	    static LocalPatch connect() {
	      Decoder decoder = new GsonDecoder();
	      return Feign.builder()
	          .decoder(decoder)
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
		LocalPatch.SimplePojo simplePojo = new LocalPatch.SimplePojo();
		simplePojo.name = "Karl.";
		simplePojo.value = 5;
		simplePojo = localPatch.patch(simplePojo);
		logger.info("Application Called !!");
		System.out.println(simplePojo.name);
		System.out.println(simplePojo.value);
	}
	
}
