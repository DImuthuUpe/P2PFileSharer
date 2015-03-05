
<h3>To setup a sample netwok</h3>

* Start BS server
* Start sample nodes  using ./startup.sh

<h3>All log files for each node can be found in logs folder</h3>

<h5>Sample node output</h5>

File List .........

Happy Feet

Jack and Jill

The Vampire Diarie

..................


Socket created ....... ip 127.0.0.1 port 3000

Added node 127.0.0.1 3001

Added node 127.0.0.1 3002

Added node 127.0.0.1 3004

Added node 127.0.0.1 3005

Added node 127.0.0.1 3006

Added node 127.0.0.1 3010

<h3>Console queries</h3>

* To search file 
echo "22 CONSOLE SEARCH <file name>"|nc -u <ip> <port>

* To leave the network
echo "22 CONSOLE LEAVE"|nc -u <ip> <port>

