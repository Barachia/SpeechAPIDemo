package SpeechAPIDemo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.utwente.hmi.middleware.Middleware;
import nl.utwente.hmi.middleware.loader.GenericMiddlewareLoader;
import nl.utwente.hmi.middleware.worker.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Properties;

/**
 * Class for testing the speech
 */
public class MiddlewareSpraak implements Worker {

	private static Logger logger = LoggerFactory.getLogger(MiddlewareSpraak.class.getName());
	private SpraakApp app;
	private String socket;
	private Middleware middleware;
	private ObjectMapper mapper;

	public MiddlewareSpraak(String mw, String socket) throws Exception {
		this.socket = socket;
		this.mapper = new ObjectMapper();
		JsonNode mwProps = mapper.readTree(mw);
		if (!mwProps.has("middleware")) throw new Exception("middleware object required in params");
		Properties mwProperties = getGMLProperties(mwProps.get("middleware"));
		String loaderClass = getGMLClass(mwProps.get("middleware"));
		if (loaderClass == null || mwProperties == null) throw new Exception("Invalid middleware spec in params");
		GenericMiddlewareLoader gml = new GenericMiddlewareLoader(loaderClass, mwProperties);
		init(gml.load());
	}

	private void init(Middleware mw) {
		this.middleware = mw;
	}

	public static void main(String[] args) throws Exception {

		String mw = "{\n" +
				"	\"middleware\": {" +
				"    	\"loaderClass\": \"nl.utwente.hmi.middleware.activemq.ActiveMQMiddlewareLoader\",\n" +
				"    	\"properties\": {\n" +
				"       	 \"iTopic\": \"speechInFeedback\",\n" +
				"        	 \"oTopic\": \"speechIn\",\n" +
				"        	 \"amqBrokerURI\": \"tcp://localhost:61616\"\n" +
				"    	}\n" +
				"	}" +
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
		MiddlewareSpraak fs = new MiddlewareSpraak(mw,websocket);
		fs.run();
	}


	public static Properties getGMLProperties(JsonNode spec) {
		Properties res = new Properties();
		JsonNode jnprops = null;
		if (spec.has("properties")) {
			jnprops = spec.get("properties");
		} else {
			return null;
		}
		Iterator<String> fieldNames = jnprops.fieldNames();
		while (fieldNames.hasNext()) {
			String fieldName = fieldNames.next();
			res.put(fieldName, jnprops.get(fieldName).asText());
		}
		return res;
	}

	public static String getGMLClass(JsonNode spec) {
		String loaderClass = "";
		if (spec.has("loaderClass")) {
			loaderClass = spec.get("loaderClass").asText();
		} else {
			return null;
		}
		return loaderClass;
	}

	@Override
	public void addDataToQueue(JsonNode jsonNode) {

	}

	@Override
	public void run() {
		app = new SpraakApp(socket,middleware);
		app.startCapturingAudio();
	}

	public void stopSpraak(){
		this.app.stopCapturingAudio();
	}
}
