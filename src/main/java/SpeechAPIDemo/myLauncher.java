package SpeechAPIDemo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.utwente.hmi.middleware.MiddlewareWrapper;
import nl.utwente.hmi.middleware.MiddlewareWrapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Class for testing the speech
 */
public class myLauncher {


	private static Logger logger = LoggerFactory.getLogger(myLauncher.class.getName());
	private static ObjectMapper mapper = new ObjectMapper();

	public static void main(String[] args) throws InterruptedException {

		String help = "Expecting commandline arguments in the form of \"-<argname> <arg>\".\nAccepting the following argnames: ws";
		String websocket = "ws://this.is.my.server:port/client/ws/speech";
		if(args.length % 2 != 0){
			logger.error(help);
			System.exit(0);
		}
		for (int i = 0; i < args.length; i = i + 2) {
			if (args[i].equals("-ws")) {
				websocket = args[i + 1];
			} else {
				logger.error("Unknown commandline argument: \"{}\" {} \".\n {}",args[i],args[i + 1],help);
				System.exit(0);
			}
		}


		try {
			InputStream inputStream = myLauncher.class.getClassLoader().getResourceAsStream("middleware.json");
			JsonNode props = mapper.readTree(inputStream);
			MiddlewareWrapper wrapper = MiddlewareWrapperFactory.createMiddlewareWrapper(props);
			//Creating Speech API
			SpraakApp audio = new SpraakApp(websocket,wrapper);
			audio.startCapturingAudio();
			System.out.println("Recognition was started...");

		} catch (IOException e) {
			e.printStackTrace();
		}




	}

}
