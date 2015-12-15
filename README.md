# BluPhish
Bluetooth device and service discovery tool that can be used for security assessment 
and penetration testing.

BluPhish is written in Java, and relies on the Bluecove 2.1.1 API (http://bluecove.org/).

# Compilation

Run the script *compile.script*, which contains the following command:

javac -cp bluecove-2.1.1-SNAPSHOT.jar *.java

# Execution

Run the script *run.script*, which contains the following command:

java -cp .;bluecove-2.1.1-SNAPSHOT.jar BluPhish

The application will try to discover all the locally reachable bluetooth devices and services, attempt to connect to the services, and print their information.

The information about a service will have the following format: Device Name, Service Name, Connection URL, Requires Pairing, where:

* *Device Name* is the user-friendly name given to the device.

* *Service Name* is the name of the service discovered associated with the device.
* *Connection URL* is an URL-formatted string that contains the connection type used by the service (BTSPP, BTL2CAP, BTGOEP), the address of the device,
service port, and whether the servcie requires authentication, authorization and has to be master in the communication.

* *Requires Pairing* says whether the application could connect to the service 
without pairing (generally meaning an unsafe connection could be made with 
the service). This value is irrelevant when the '--no-pair' directive is 
provided to BluPhish.

An example output is the following:

iPhone | STRING Phonebook | btgoep://D896959D35B9:13;authenticate=false;encrypt=false;master=false | Yes

This output says that 'iPhone' has a service named 'STRING Phonebook' that 
connects through 'btgoep'. The Bluetooth address of 'iPhone' is D896959D35B9, 
the service is listening to port 13, doesn't require authentication nor 
encryption, and doesn't have to be master. The service also requires 
explicit pairing between the device and another application attempting 
to connect to it, so it's secure in that sense.


By default BluPhish will attempt to discover and pair with all the bluetooth services in order to probe for vulnerabilities. 
** This will result in a pairing notification sent to the device. **

In order to perform a non-pairing discovery, invoke BluPhish with the 
'--no-pair' directive:

java -cp .;bluecove-2.1.1-SNAPSHOT.jar BluPhish --no-pair

# Testing Environment

BluPhish was tested on a 64-bit Windows 10 machine running the Winsock Bluetooth stack
over a Kinivo BTD-400 Bluetooth adapter.

# Contact

Please direct any questions and suggestions to Oswaldo Olivo (oswaldo.l.olivo@gmail.com).