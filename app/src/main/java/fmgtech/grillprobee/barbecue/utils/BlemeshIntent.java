package fmgtech.grillprobee.barbecue.utils;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;


/**
 * The Blemesh Intent class
 */
public class BlemeshIntent {
    /**
     * Used as an optional int extra field for status code
     */
    public static final String EXTRA_STATUS =
        "com.heilo.app.blemesh.extra.STATUS";

    /**
     * Device name string extra field
     */
    public static final String EXTRA_DEVICE_NAME =
        "com.heilo.app.blemesh.extra.DEVICE_NAME";

    /**
     * Device RSSI int extra field
     */
    public static final String EXTRA_DEVICE_RSSI =
        "com.heilo.app.blemesh.extra.DEVICE_RSSI";
    
    public static final String EXTRA_DEVICE_TYPE =
            "com.heilo.app.blemesh.extra.DEVICE_TYPE";
    
    public static final String EXTRA_DEVICE_MAC =
            "com.heilo.app.blemesh.extra.DEVICE_MAC";


    /**
     * Intent to start mesh device scanning.
     *
     * This intent has no extra.
     */
    public static final String ACTION_START_MESH_DEVICE_SCANNING =
        "com.heilo.app.blemesh.action.START_MESH_DEVICE_SCANNING";

    /**
     * Intent to stop mesh device scanning.
     *
     * This intent has no extra.
     */
    public static final String ACTION_STOP_MESH_DEVICE_SCANNING =
        "com.heilo.app.blemesh.action.STOP_MESH_DEVICE_SCANNING";

    /**
     * Intent to notify that a mesh device is found
     *
     * This intent has three extras:
     *     BluetoothDevice.EXTRA_DEVICE: device found
     *     EXTRA_DEVICE_NAME: device name
     *     EXTRA_DEVICE_RSSi: device RSSI
     */
    public static final String ACTION_MESH_DEVICE_FOUND =
        "com.heilo.app.blemesh.action.MESH_DEVICE_FOUND";

    /**
     * Intent to notify that a proxy device is found
     *
     * This intent has three extras:
     *     BluetoothDevice.EXTRA_DEVICE: device found
     *     EXTRA_DEVICE_NAME: device name
     *     EXTRA_DEVICE_RSSi: device RSSI
     */
    public static final String ACTION_PROXY_DEVICE_FOUND =
        "com.heilo.app.blemesh.action.PROXY_DEVICE_FOUND";

    /**
     * Intent to add a mesh device into the network
     *
     * This intent has one extra BluetoothDevice.EXTRA_DEVICE for the device
     * to be added into the mesh network.
     */
    public static final String ACTION_ADD_MESH_DEVICE =
        "com.heilo.app.blemesh.action.ADD_MESH_DEVICE";

    /**
     * Intent to add a mesh device into the network
     *
     * This intent has one extra:
     *     BluetoothDevice.EXTRA_DEVICE: device added into the mesh network.
     */
    public static final String ACTION_MESH_DEVICE_ADDED =
        "com.heilo.app.blemesh.action.MESH_DEVICE_ADDED";
    
    public static final String ACTION_MESH_DEVICE_ADDING =
            "com.heilo.app.blemesh.action.MESH_DEVICE_ADDING";
    
    public static final String ACTION_MESH_DEVICE_CLOUDADDED =
            "com.heilo.app.blemesh.action.MESH_DEVICE_CLOUDADDED";
    public static final String ACTION_MESH_DEVICE_CLOUDADDFAIL =
            "com.heilo.app.blemesh.action.MESH_DEVICE_CLOUDADDFAIL";

    /**
     * Intent to report GATT has been connected
     *
     * This intent has no extra
     */
    public static final String ACTION_GATT_CONNECTED =
        "com.heilo.app.blemesh.action.GATT_CONNECTED";

    /**
     * Intent to report GATT has been disconnected
     *
     * This intent has no extra
     */
    public static final String ACTION_GATT_DISCONNECTED =
        "com.heilo.app.blemesh.action.GATT_DISCONNECTED";
    
    public static final String ACTION_GATT_CONNECTION_ERROR =
            "com.heilo.app.blemesh.action.ACTION_GATT_CONNECTION_ERROR";
    
    public static final String ACTION_GATT_DISCONNECTED_ERROR =
            "com.heilo.app.blemesh.action.ACTION_GATT_DISCONNECTED_ERROR";

    /**
     * Intent to report GATT send() function status
     *
     * This intent has one extra:
     *     EXTRA_RESULT: Result of the send operation
     */
    public static final String ACTION_GATT_SEND_STATUS =
        "com.heilo.app.blemesh.action.GATT_SEND_STATUS";
    
    
    public static final String ACTION_GATT_DEVICE_RESPONSE =
            "com.heilo.app.blemesh.action.GATT_DEVICE_RESPONSE";
    public static final String ACTION_GATT_WIFI_RESPONSE =
            "com.heilo.app.blemesh.action.GATT_WIFI_RESPONSE";
    
    /**
     * Send the ACTION_ADD_MESH_DEVICE intent
     */
    public static void sendAddMeshDeviceIntent(Context context, ArrayList<BluetoothDevice> devices) {
        Intent intent = new Intent(ACTION_ADD_MESH_DEVICE);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BluetoothDevice.EXTRA_DEVICE, devices);
        intent.putExtras(bundle);
        context.sendBroadcast(intent);
    }


    /**
     * Send the ACTION_MESH_DEVICE_ADDED intent
     */
    public static void sendMeshDeviceAddedIntent(Context context, BluetoothDevice device) {
        Intent intent = new Intent(ACTION_MESH_DEVICE_ADDED);
        intent.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
        context.sendBroadcast(intent);
    }
    
    public static void sendMeshDeviceCloudAddedIntent(Context context) {
        Intent intent = new Intent(ACTION_MESH_DEVICE_CLOUDADDED);
        context.sendBroadcast(intent);
    }
    public static void sendMeshDeviceCloudAddFailIntent(Context context) {
        Intent intent = new Intent(ACTION_MESH_DEVICE_CLOUDADDFAIL);
        context.sendBroadcast(intent);
    }
    
    public static void sendMeshDeviceAddingIntent(Context context) {
        Intent intent = new Intent(ACTION_MESH_DEVICE_ADDING);
        context.sendBroadcast(intent);
    }
}
