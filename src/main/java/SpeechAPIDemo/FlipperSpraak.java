package SpeechAPIDemo;

import com.fasterxml.jackson.databind.JsonNode;
import hmi.flipper2.middleware.FlipperMiddleware;
import nl.utwente.hmi.middleware.MiddlewareWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for testing the speech
 */
public class FlipperSpraak extends FlipperMiddleware {


	private static Logger logger = LoggerFactory.getLogger(FlipperSpraak.class.getName());
	private SpraakApp app;
	private String socket;

	public FlipperSpraak(String mw, String socket){
		super(mw);
		this.socket = socket;
	}

	public boolean init(){
		super.init();
		app = new SpraakApp(socket,this.wrapper);
		app.startCapturingAudio();
		return true;
	}

	public static void main(String[] args){

		String mw = "{\n" +
				"    \"loader\": \"nl.utwente.hmi.middleware.activemq.ActiveMQMiddlewareLoader\",\n" +
				"    \"properties\": {\n" +
				"        \"iTopic\": \"speechInFeedback\",\n" +
				"        \"oTopic\": \"speechIn\",\n" +
				"        \"amqBrokerURI\": \"tcp://localhost:61616\"\n" +
				"    }\n" +
				"}";
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
		FlipperSpraak fs = new FlipperSpraak(mw,websocket);
		fs.init();
	}

}