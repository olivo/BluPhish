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

import java.io.*;
import java.util.*;
import javax.bluetooth.*;

// Class that discovers Bluetooth devices and services.
class BtDiscoveryListener implements DiscoveryListener {

    // Adds a discovered device to the global set of devices. 
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {

		    BluPhish.btDevices.add(btDevice);
    }

    // Notifies all event listeners that device discovery is complete.
    public void inquiryCompleted(int discType) {

	synchronized(BluPhish.inquiryCompletedEvent) {
	    BluPhish.inquiryCompletedEvent.notifyAll();
	}
    }

    // Notifies all event listeners that service search is complete.
    public void serviceSearchCompleted(int transID, int respCode) {

	synchronized(BluPhish.serviceSearchCompletedEvent) {
	    BluPhish.serviceSearchCompletedEvent.notifyAll();
	}
    }

    // Stores discovered service information into the global map containing device-to-service information.
    public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {

		    for(int i = 0; i < servRecord.length; i++) {

			DataElement serviceName = servRecord[i].getAttributeValue(0x0100);
			
			// Get the device that offers the service.
			RemoteDevice btDevice = servRecord[i].getHostDevice();
			    
			// Store the service information for the device.
			if (!BluPhish.btDevice2Service.containsKey(btDevice)) {
			    BluPhish.btDevice2Service.put(btDevice, new HashSet<ServiceRecord>());
			}
			
			BluPhish.btDevice2Service.get(btDevice).add(servRecord[i]);
		    }
		}
}
