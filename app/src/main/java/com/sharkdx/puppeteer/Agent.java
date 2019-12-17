package com.sharkdx.puppeteer;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class Agent {
	public static String TAG = "Puppet";

    private String hostname;
    private int port;
    private String name;
    private Float version;

	public int screenCount = 1;
	public int screenWidth = 1920;
	public int screenHeight = 1080;

	public int secondaryX = 1920;
	public int secondaryY = 0;
	public int secondaryWidth = 1920;
	public int secondaryHeight = 1080;

    private Socket socket = null;
	private int start_x = 0;
	private int start_y = 0;

    public Agent (String hostname, int port, String name, Float verison){
        this.hostname = hostname;
        this.port = port;
        this.name = name;
        this.version = verison;
    }

    public String getHostname()
    {
        return this.hostname;
    }

    public int getPort()
    {
        return this.port;
    }

    public String getName()
    {
        return this.name;
    }

    public Float getVersion()
    {
        return this.version;
    }

    public String getFullHostname()
    {
        return this.hostname + ":" + this.port;
    }

    public boolean Connect()
    {
        try
        {
            socket = new Socket(InetAddress.getByName(hostname), port);
            socket.setKeepAlive(true);

			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String response = in.readLine();
			if (response != null)
			{
				Log.d(TAG, response);
				// {"screen_count": 2, "width": 1920, "height": 1080, "secondary_x": 0, "secondary_y": 0, "secondary_width": 1920, "secondary_height": 1080}

				JSONObject reader = new JSONObject(response);
				this.screenCount = reader.getInt("screen_count");
				this.screenWidth= reader.getInt("width");
				this.screenHeight = reader.getInt("height");
				this.secondaryX = reader.getInt("secondary_x");
				this.secondaryY = reader.getInt("secondary_y");
				this.secondaryWidth = reader.getInt("secondary_width");
				this.secondaryHeight = reader.getInt("secondary_height");
			}

            return true;
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (JSONException e)
		{
			e.printStackTrace();
		}
		return false;
    }

    public boolean isConnected()
    {
        return socket != null && socket.isConnected();
    }

    public void MoveTo(float x, float y)
    {
    	float dx = x;
		float dy = y;

    	new AgentUpdateTask(socket).execute(1.0f, dx, dy);
    }

    public void Tap()
	{
		new AgentUpdateTask(socket).execute(2.0f);
	}
}