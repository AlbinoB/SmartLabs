# SmartLabs
App for smart labs for efficient power saving

The objective behind this project is to turn on/off the systems from a computer lab by just scanning there ID card barcode.

I've launhed an Apache server along with php and MySQL to create an API so that the application can be implemented on any platform.

Next, There's the MQTT server running on the server which publishes messages to the NodeMCU to control the power plug. The publish 
message is sent from the php script itself.

A system connected to the lan can be turned on using magic packets. I've mapped the IP addresses of each system in the network
to the system code. The student barcode is all allocated to a single system. 

Once an API request is made to the server along with the barcode id the php script will sent the magic packet to the allocated system
to turn it on.

