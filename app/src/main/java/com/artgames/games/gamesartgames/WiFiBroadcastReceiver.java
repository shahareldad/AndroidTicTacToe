package com.artgames.games.gamesartgames;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import BroadcastReceiver;

public class WiFiBroadcastReceiver extends BroadcastReceiver {

    private final WiFiDirectActivity _activity;
    private final WifiP2pManager _manager;
    private final WifiP2pManager.Channel _channel;

    public WiFiBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, WiFiDirectActivity activity){
        super();
        _activity = activity;
        _manager = manager;
        _channel = channel;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                _activity.setIsWifiP2pEnabled(true);
            }else {
                _activity.setIsWifiP2pEnabled(false);
                _activity.resetData();
            }
        }
        if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            if (_manager != null) {
                //_manager.requestPeers(_channel, (WifiP2pManager.PeerListListener) _activity.getFragmentManager().findFragmentById(R.id.frag_list));
            }
        }
        if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){
            if (_manager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                // we are connected with the other device, request connection
                // info to find group owner IP

                DeviceDetailFragment fragment = (DeviceDetailFragment) _activity.getFragmentManager().findFragmentById(R.id.frag_detail);
                _manager.requestConnectionInfo(_channel, fragment);
            } else {
                // It's a disconnect
                _activity.resetData();
            }
        }
        if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){
            DeviceListFragment fragment = (DeviceListFragment) _activity.getFragmentManager().findFragmentById(R.id.frag_list);
            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
        }
    }


}
