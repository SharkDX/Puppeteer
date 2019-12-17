package com.sharkdx.puppeteer;

import android.app.ProgressDialog;
import android.net.nsd.NsdManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.druk.dnssd.BrowseListener;
import com.github.druk.dnssd.DNSSD;
import com.github.druk.dnssd.DNSSDEmbedded;
import com.github.druk.dnssd.DNSSDException;
import com.github.druk.dnssd.DNSSDService;
import com.github.druk.dnssd.ResolveListener;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "Puppet";
    public static final String SERVICE_NAME = "Puppet";

    private NsdManager.ResolveListener mResolveListener = null;
    private NsdManager.DiscoveryListener mDiscoveryListener = null;

    private DNSSD dnssd = null;
    private DNSSDService browseService = null;

    private RecyclerView mRecyclerView = null;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Puppet started!");

        dnssd = new DNSSDEmbedded(this.getApplicationContext());
        startBrowse();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerViewAdapter(new ArrayList<Agent>());
        mRecyclerView.setAdapter(mAdapter);
    }

	@Override
	protected void onResume() {
		super.onResume();
		mAdapter.setOnItemClickListener((position, v) ->
        {
            Log.i(TAG, " Clicked on Item " + position);
            Agent agent = mAdapter.getItem(position);

            onAgentClicked(agent);
        });

        onAgentClicked(new Agent("192.168.14.49", 1337, "Guy", 1.0f));
	}

    private void startBrowse() {
        Log.i(TAG, "Searching for services of '" + SERVICE_NAME + "'");
        try {
            browseService = dnssd.browse("_http._tcp", new BrowseListener() {
                @Override
                public void serviceFound(DNSSDService browser, int flags, int ifIndex, final String serviceName, String regType, String domain) {
                    Log.d(TAG, "Found service " + serviceName);
                    if (serviceName.equals(SERVICE_NAME))
                    {
                        Log.i(TAG, "Found " + SERVICE_NAME + ". Resolving...");
                        startResolve(flags, ifIndex, serviceName, regType, domain);
                    }
                }

                @Override
                public void serviceLost(DNSSDService browser, int flags, int ifIndex, String serviceName, String regType, String domain) {
//                    mServiceAdapter.remove(new BonjourService.Builder(flags, ifIndex, serviceName, regType, domain).build());
                }

                @Override
                public void operationFailed(DNSSDService service, int errorCode) {
                    Log.e(TAG, "error: " + errorCode);
                }
            });
        } catch (DNSSDException e) {
            e.printStackTrace();
            Log.e(TAG, "error", e);
        }
    }

    private void startResolve(int flags, int ifIndex, final String serviceName, final String regType, final String domain) {
        try {
            dnssd.resolve(flags, ifIndex, serviceName, regType, domain, new ResolveListener() {
                @Override
                public void serviceResolved(DNSSDService resolver, int flags, int ifIndex, String fullName, String hostName, int port, Map<String, String> txtRecord) {
                    String ip = hostName.substring(0, hostName.length() - 1);
                    Log.d(TAG, "Resolved service: " + ip + ":" + port);

                    String name = txtRecord.get("name");
                    if (name == null)
                        name = "";
                    float version = -1;
                    String versionStr = txtRecord.get("version");
                    if (versionStr != null)
                        version = Float.parseFloat(versionStr);

                    onServiceDiscovered(ip, port, name, version);

                }

                @Override
                public void operationFailed(DNSSDService service, int errorCode) {

                }
            });
        } catch (DNSSDException e) {
            e.printStackTrace();
        }
    }

    private void onServiceDiscovered(String ip, int port, String name, Float version)
    {
        mAdapter.addItem(new Agent(ip, port, name, version), 0);
        findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
    }

    private void onAgentClicked(Agent agent)
    {
        try
        {
            ProgressDialog dialog =  new ProgressDialog(MainActivity.this);
            dialog.setMessage("Connecting...");
            dialog.show();

			new AgentConnectTask(agent, () ->
            {
                if (agent.isConnected())
                {
                    Toast.makeText(getApplicationContext(), "Connected!", Toast.LENGTH_LONG).show();
                    ControlActivity.StartControllingAgent(MainActivity.this, agent);
                }
                else
                    Toast.makeText(getApplicationContext(), "Couldn't connect!", Toast.LENGTH_LONG).show();
                dialog.hide();
            }).execute();

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
