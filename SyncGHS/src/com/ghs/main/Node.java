package com.ghs.main;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

/*
 * A class to store node specific data
 */
public class Node {

	int UID;
	int componentId;
	CopyOnWriteArrayList<Edge> graphEdges;
	CopyOnWriteArrayList<Edge> treeEdges;
	boolean leaderInd;

	String host;
	int port;
	ServerSocket serverSocket;

	boolean stopClientMgr;
	boolean isMarked;
	int phaseNumber;
	boolean startMWOESearchFlag;
	int BFSParentUID;

	/**
	 * @return the bFSParentUID
	 */
	public synchronized int getBFSParentUID() {
		return BFSParentUID;
	}

	/**
	 * @param bFSParentUID the bFSParentUID to set
	 */
	public synchronized void setBFSParentUID(int bFSParentUID) {
		BFSParentUID = bFSParentUID;
	}

	/**
	 * @return the componentId
	 */
	public int getComponentId() {
		return componentId;
	}

	/**
	 * @param componentId the componentId to set
	 */
	public void setComponentId(int componentId) {
		this.componentId = componentId;
	}

	/**
	 * @return the isMarked
	 */
	public boolean isMarked() {
		return isMarked;
	}

	/**
	 * @param isMarked the isMarked to set
	 */
	public void setMarked(boolean isMarked) {
		this.isMarked = isMarked;
	}

	/**
	 * @param leaderInd the leaderInd to set
	 */
	public void setLeaderInd(boolean leaderInd) {
		this.leaderInd = leaderInd;
	}

	Node(int id)
	{
		this.UID = id;
		this.msgBuffer = new CopyOnWriteArrayList<>();
		this.graphEdges = new CopyOnWriteArrayList<>();
		this.treeEdges = new CopyOnWriteArrayList<>();
		this.mwoeCadidateReplyBuffer = new CopyOnWriteArrayList<>();
		this.leaderInd = true;
		this.componentId = this.UID;
		this.phaseNumber = 0;
		this.startMWOESearchFlag = true;
		this.BFSParentUID = -1;
	}

	/**
	 * @return the startMWOESearchFlag
	 */
	public synchronized boolean isStartMWOESearchFlag() {
		return startMWOESearchFlag;
	}

	/**
	 * @param startMWOESearchFlag the startMWOESearchFlag to set
	 */
	public synchronized void setStartMWOESearchFlag(boolean startMWOESearchFlag) {
		this.startMWOESearchFlag = startMWOESearchFlag;
	}

	public synchronized boolean isStopClientMgr() {
		return stopClientMgr;
	}

	public void setStopClientMgr(boolean stopClientMgr) {
		this.stopClientMgr = stopClientMgr;
	}

	//map of configuration file with UID as a key
	static HashMap<Integer, Node> configMap;

	/*
	 * Thread safe buffer of messages received till now
	 */
	CopyOnWriteArrayList<Msg> msgBuffer;
	CopyOnWriteArrayList<Msg> mwoeCadidateReplyBuffer;

	public int getUID() {
		return UID;
	}

	public void setUID(int uID) {
		UID = uID;
	}

	public int getLeaderUID() {
		return componentId;
	}

	public void setLeaderUID(int leaderUID) {
		this.componentId = leaderUID;
	}

	public CopyOnWriteArrayList<Edge> getGraphEdges() {
		return graphEdges;
	}

	public void setGraphEdges(CopyOnWriteArrayList<Edge> graphEdges) {
		this.graphEdges = graphEdges;
	}

	public CopyOnWriteArrayList<Edge> getTreeEdges() {
		return treeEdges;
	}

	public void setTreeEdges(CopyOnWriteArrayList<Edge> treeEdges) {
		this.treeEdges = treeEdges;
	}

	public Boolean getLeaderInd() {
		return leaderInd;
	}

	public void setLeaderInd(Boolean leaderInd) {
		this.leaderInd = leaderInd;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public static HashMap<Integer, Node> getConfigMap() {
		return configMap;
	}

	public static void setConfigMap(HashMap<Integer, Node> configMap) {
		Node.configMap = configMap;
	}

	public synchronized CopyOnWriteArrayList<Msg> getMsgBuffer() {
		return msgBuffer;
	}

	public synchronized void setMsgBuffer(CopyOnWriteArrayList<Msg> msgBuffer) {
		this.msgBuffer = msgBuffer;
	}


	/**
	 * @return the phaseNumber
	 */
	public synchronized int getPhaseNumber() {
		return phaseNumber;
	}

	/**
	 * @param phaseNumber the phaseNumber to set
	 */
	public synchronized void setPhaseNumber(int phaseNumber) {
		this.phaseNumber = phaseNumber;
	}

	public static void main(String[] args) throws IOException {

		int UID = Integer.parseInt(args[0]);

		System.out.println("Starting execution for the UID:" + UID);

		String configFile = args[1];
		int numberOfNode = 0;
		Scanner sc = new Scanner(new File(configFile));
		HashMap<Integer, Node> confMap = new HashMap<>();

		boolean nodeParsingDone = false;
		int count=0;
		ArrayList<Edge> edgeList = new ArrayList<>();
		while(sc.hasNextLine())
		{
			String line = sc.nextLine().trim();
			if(line.startsWith("# Number of nodes"))
			{
				numberOfNode = Integer.parseInt(sc.nextLine());
			}

			if(numberOfNode > 0 && !nodeParsingDone && !line.startsWith("#"))
			{
				String[] nodeData = line.split("\\s+");

				int tempUID = Integer.parseInt(nodeData[0]);	
				Node tempNode = new Node(tempUID);
				tempNode.setHost(nodeData[1]);
				tempNode.setPort(Integer.parseInt(nodeData[2]));
				confMap.put(tempUID, tempNode);
				count++;

				if(numberOfNode==count)
				{
					line = sc.nextLine().trim();
					nodeParsingDone=true;
				}

			}

			if(nodeParsingDone && !line.startsWith("#"))
			{
				String[] nodeData = line.split("\\s+");
				String[] edge = nodeData[0].substring(1, nodeData[0].length()-1).split(",");

				Edge edg = new Edge(Integer.parseInt(edge[0]), 
						Integer.parseInt(edge[1]), Integer.parseInt(nodeData[1]));

				edgeList.add(edg);	
			}
		}

		sc.close();

		/*
		 * filter only this nodes edges
		 */
		CopyOnWriteArrayList<Edge> tempList = new CopyOnWriteArrayList<>();
		for(Edge e: edgeList)
		{
			if(e.isContainsUID(UID))
				tempList.add(e);
		}

		Node.setConfigMap(confMap);
		Node thisNode = Node.getConfigMap().get(UID);
		thisNode.setGraphEdges(tempList);

		ServerSocket socket = new ServerSocket(thisNode.getPort(), numberOfNode);

		thisNode.setServerSocket(socket);

		System.out.println("Config file read, Server Socket created");

		/*
		 * Start client manager process which will keep accepting incoming connections to socket
		 * read messages and store them into thread safe buffer
		 */
		ClientManager ct = new ClientManager(thisNode);
		Thread t = new Thread(ct);
		t.start();

		System.out.println("Waiting for all nodes to spin up");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		/*
		 * Start GHS processing now
		 */
		GHSProcessor ghsProcessor = new GHSProcessor(thisNode);
		Thread t1 = new Thread(ghsProcessor);
		t1.start();
	}

	/**
	 * @return the mwoeCadidateReplyBuffer
	 */
	public synchronized CopyOnWriteArrayList<Msg> getMwoeCadidateReplyBuffer() {
		return mwoeCadidateReplyBuffer;
	}

	/**
	 * @param mwoeCadidateReplyBuffer the mwoeCadidateReplyBuffer to set
	 */
	public synchronized void setMwoeCadidateReplyBuffer(CopyOnWriteArrayList<Msg> mwoeCadidateReplyBuffer) {
		this.mwoeCadidateReplyBuffer = mwoeCadidateReplyBuffer;
	}

}
