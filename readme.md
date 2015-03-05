Building project
----------------

* Go to BootStrapServer

* mvn clean install

* Go to P2PNode

* mvn clean install


To setup a sample netwok
------------------------

* Start BS server using ./startup-bs.sh
* Start sample nodes  using ./startup.sh


All log files for each node can be found in logs folder

Console queries
---------------

* To search file 

echo "22 CONSOLE SEARCH <file name>" | nc -u <ip> <port>

* To leave the network

echo "22 CONSOLE LEAVE" | nc -u <ip> <port>

