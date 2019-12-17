package com.sharkdx.puppeteer;

import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class AgentUpdateTask extends AsyncTask<Float, Void, Void>
{
	private Socket socket;

	public  AgentUpdateTask(Socket socket)
	{
		this.socket = socket;
	}

	@Override
	protected Void doInBackground(Float... data) {
		try
		{
			DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

			for (float i : data)
			{
				outputStream.writeFloat(i);
			}
			outputStream.write('\n');
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}

class AgentConnectTask extends AsyncTask<Agent, Integer, Agent>
{
	private Agent agent;
	private  Runnable runnable;


	public AgentConnectTask(Agent agent, Runnable runnable)
	{
		this.agent = agent;
		this.runnable = runnable;
	}

	@Override
	protected Agent doInBackground(Agent... agents) {
		agent.Connect();
		return agent;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {

	}

	@Override
	protected void onPostExecute(Agent agent) {
		runnable.run();
	}
}