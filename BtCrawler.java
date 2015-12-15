/*
	Copyright 2015, Oswaldo Olivo

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.

	You may obtain a copy of the License at
		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/

import com.intel.bluetooth.*;
import java.io.*;
import java.util.*;
import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.obex.*;

// Class that processes discovered Bluetooth devices and services and displays diagnostic information.
class BtCrawler {

    static final Vector btDevices = new Vector();
    static final Object inquiryCompletedEvent = new Object();
    static final Object serviceSearchCompletedEvent = new Object();

    static final HashMap<RemoteDevice, HashSet<ServiceRecord>> btDevice2Service = new HashMap<RemoteDevice, HashSet<ServiceRecord>>();

    // Starts Bluetooth device discovery process.
    public static void discoverDevices(BtDiscoveryListener listener) {

	try {
	    synchronized(inquiryCompletedEvent) {

		boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().
		    startInquiry(DiscoveryAgent.GIAC, listener);

		if (started) {
		    inquiryCompletedEvent.wait();
		}
	    }
	}
	catch (Exception e) { 
	    System.out.println("ERROR: Could not start Bluetooth discovery.");
	    System.out.println("Exception: " + e);
	}
    }

    // Starts Bluetooth service discovery process.
    public static void discoverServices(BtDiscoveryListener listener) {

	// Searching for the Public Browse Group: all public services 
	// on a device.
	javax.bluetooth.UUID[] searchUuidSet = 
	    new javax.bluetooth.UUID[] { new javax.bluetooth.UUID(0x1002) };

	// Extracting the name attribute of the service, apart from the default attributes.
	int[] attrIDSet = new int[] { 0x0100 };
	
	// Searching services for each discovered device.
	for(Enumeration en = btDevices.elements(); en.hasMoreElements(); ) {
	    try {
	           RemoteDevice btDevice = (RemoteDevice)en.nextElement();
		   synchronized (serviceSearchCompletedEvent) {

		       LocalDevice.getLocalDevice().getDiscoveryAgent()
		       .searchServices(attrIDSet, searchUuidSet, btDevice, listener);
		       serviceSearchCompletedEvent.wait();
		   }
	    }
	    catch (Exception e) {}
	}
    }

    public static void main(String[] args) throws Exception {

	BtDiscoveryListener listener = new BtDiscoveryListener();

	discoverDevices(listener);
	discoverServices(listener);

	System.out.print("==============================================================");
	System.out.println("==============================================================");
	System.out.println("Device Name | Service Name | Connection URL (Protocol/BT Address/Port/Auth/Enc/Master-Slave) | Pairing Required?");
	System.out.print("==============================================================");
	System.out.println("==============================================================");

	for (RemoteDevice btDevice : btDevice2Service.keySet()) {

	    String deviceName = btDevice.getFriendlyName(false);

	    for (ServiceRecord servRecord : btDevice2Service.get(btDevice)) {

		String serviceName = servRecord.getAttributeValue(0x0100) != null ? 
		    ((Object)servRecord.getAttributeValue(0x0100)).toString() : "";

		String connectionURL = servRecord.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
		boolean pairingRequired = true;

		// Connecting to the device using the OBEX protocol.
		if (connectionURL != null) {
		    
		    // Trying to connect to determine if the connection doesn't require pairing (vulnerable).
		    try {

			ClientSession clientSession = (ClientSession)Connector.open(connectionURL);
			pairingRequired = false;;
		    }
		    catch (Exception e) {}
		}
		// Print diagnostic information.
		System.out.print("==============================================================");
		System.out.println("==============================================================");
		System.out.print(deviceName + " | " + serviceName + " | ");
		System.out.println(connectionURL + " | " + (pairingRequired ? "Yes" : "No"));
		System.out.print("==============================================================");
		System.out.println("==============================================================");
	    }
	}
    }
}
