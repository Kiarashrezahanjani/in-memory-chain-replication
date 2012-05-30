package utility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.util.Properties;

public class Configuration {

	int protocolPort;
	int bufferServerPort;
	//int bufferClientPort;
	int ensembleBufferSize;
	int replicationFactor;
	//for testing 
	int valueSize;
	
	int gvInfoInterval;
	int serverInfoInterval;
	int serverInfoIntervalDeviation;
	
	int serverUpdateThreshold;
	int saturationPoint;
	
	InetSocketAddress protocolSocketAddress;
	InetSocketAddress bufferServerSocketAddress;
	//InetSocketAddress bufferClientSocketAddress;
	String dbClientId;

	String configDirectory = "configuration";

	String defaultPropertiesFile = "defaultProperties";
	static String applicationPropertiesFile = "applicationProperties";

	static String defaultPropertiesPath;
	static String applicationPropertiesPath;

	static String zkNameSpace;
	static String zkServersRoot;
	static String zkClientRoot;
	static String zkEnsemblesRoot;
	static String zkServersGlobalViewRoot;
	static String zkConnectionString;
	static int zkSessionTimeOut;

	/**
	 * Load properties from the default properties file (configuration/applicationProperties).
	 */
	public Configuration()
	{
		this(applicationPropertiesFile);
	}

	/**
	 * Load properties from the given properties file. File should be place in configuration folder.
	 */
	public Configuration(String applicationPropertiesFile)
	{	
		defaultPropertiesPath = configDirectory + System.getProperty("file.separator") + defaultPropertiesFile;
		applicationPropertiesPath = configDirectory + System.getProperty("file.separator") + applicationPropertiesFile;
		// create and load default properties
		Properties defaultProperty =  new Properties();
		Properties applicationProperties;
		FileInputStream input;
		try {
			//default properties
			input = new FileInputStream(defaultPropertiesPath);
			defaultProperty.load(input);
			input.close();

			zkNameSpace = defaultProperty.getProperty("zkNameSpace").trim();
			zkServersRoot = defaultProperty.getProperty("zkServersRoot").trim();
			zkClientRoot = defaultProperty.getProperty("zkClientRoot").trim();
			zkEnsemblesRoot = defaultProperty.getProperty("zkEnsemblesRoot").trim();
			zkServersGlobalViewRoot = defaultProperty.getProperty("zkServersGlobalViewRoot").trim();
			zkConnectionString = defaultProperty.getProperty("zkConnectionString").trim();
			zkSessionTimeOut = Integer.parseInt( defaultProperty.getProperty("zkSessionTimeOut") );
			replicationFactor = Integer.parseInt( defaultProperty.getProperty("replicationFactor") );
			valueSize = Integer.parseInt( defaultProperty.getProperty("valueSize") );

			gvInfoInterval = Integer.parseInt( defaultProperty.getProperty("gvInfoInterval") );
			serverInfoInterval = Integer.parseInt( defaultProperty.getProperty("serverInfoInterval") );
			serverInfoIntervalDeviation = Integer.parseInt( defaultProperty.getProperty("serverInfoIntervalDeviation") );
		
			serverUpdateThreshold = Integer.parseInt( defaultProperty.getProperty("serverUpdateThreshold") );
			saturationPoint = Integer.parseInt( defaultProperty.getProperty("saturationPoint") );

			//application properties
			applicationProperties = new Properties(defaultProperty);
			input = new FileInputStream(applicationPropertiesPath);
			applicationProperties.load(input);
			input.close();

			dbClientId = applicationProperties.getProperty("DB_CLIENT_ID");
			protocolPort = Integer.parseInt( applicationProperties.getProperty("protocol_port") );
			bufferServerPort = Integer.parseInt( applicationProperties.getProperty("buffer_server_port") );
			ensembleBufferSize = Integer.parseInt( applicationProperties.getProperty("ensemble_buffer_size"));

			System.out.println("protocolPort"+protocolPort);
			System.out.println("bufferServerPort"+bufferServerPort);
			//System.out.println("bufferClientPort"+bufferClientPort);
			System.out.println("ensembleBufferSize"+ensembleBufferSize);

			protocolSocketAddress = new InetSocketAddress(Inet4Address.getLocalHost(),  protocolPort);
			bufferServerSocketAddress = new InetSocketAddress(Inet4Address.getLocalHost(), bufferServerPort);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public int getValueSize(){
		return valueSize;
	}
	
	public int getServerUpdateThreshold() {
		return serverUpdateThreshold;
	}
	
	public int getReplicationFactor() {
		return replicationFactor;
	}

	public int getSaturationPoint() {
		return saturationPoint;
	}

	public int getGvInfoInterval() {
		return gvInfoInterval;
	}

	public int getServerInfoInterval() {
		return serverInfoInterval;
	}

	public int getServerInfoIntervalDeviation() {
		return serverInfoIntervalDeviation;
	}

	public String getDbClientId() {
		return dbClientId;
	}

	public int getEnsembleBufferSize() {
		return ensembleBufferSize;
	}

	public int getZkSessionTimeOut() {
		return zkSessionTimeOut;
	}

	public  String getZkConnectionString() {
		return zkConnectionString;
	}

	public  String getZkServersGlobalViewRoot() {
		return zkServersGlobalViewRoot;
	}

	public  String getZkNameSpace() {
		return zkNameSpace;
	}

	public  String getZkServersRoot() {
		return zkServersRoot;
	}

	public  String getZkClientRoot() {
		return zkClientRoot;
	}

	public  String getZkEnsemblesRoot() {
		return zkEnsemblesRoot;
	}

	/**
	 * IP address is the wild card address and port is specified in the configuration file.
	 * @return
	 */
	public InetSocketAddress getProtocolSocketAddress()
	{
		return protocolSocketAddress;
	}

	/**
	 * IP address is the wild card address and port is specified in the configuration file.
	 * @return
	 */
	public InetSocketAddress getBufferServerSocketAddress()
	{
		return bufferServerSocketAddress;
	}

	public int getBufferServerPort()
	{
		return bufferServerPort; 
	}

	public int getProtocolPort()
	{
		return protocolPort; 
	}

}
