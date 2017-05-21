# Chat-application
Distributed chat application built using Java-EE and AngularJS with JAX-RS as REST API end point, JMS as API for formal communication between applications and Web sockets for communication between client and server.

# Getting started
In order to get application started there are few requirements that needs to be settled.
# Prerequisites
* [Java 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [WildFly 10.1 AS](http://wildfly.org/downloads/)
# Setup
After getting neccessary JDK and server, it is needed to:
* Deploy both applications to WildFly/ Copy UserApp.ear and ChatApp.ear to `path_to_wildfly/standalone/deployments`
* Add JMS end points 
# Configuring JSM end points
Adding end points can be done either by changing standalone-full.xml file or via admin console in browser(recommended).
Following second way, go to bin folder of WildFly server, and run `add-user.sh/add-user.bat` and add another managment user. 
Next start server and go to localhost:8080 and then admin console. 
From within the menu choose Configuration -> Subsystem -> ActiveMQ -> default/Queue-Topics and add next Queues:
* java:/jms/queue/userQueue
* java:/jms/queue/chatQueue
* java:/jms/queue/socketQueue
* java:/jms/queue/socketChatQueue
* java:/jms/queue/socketMessageQueue

Queue name is the same as the last part of JNDI name.
# Running application
Open terminal and navigation to WildFly bin folder. First you need to start master by typing following:
* `./standalone.sh(or standalone.bat) -Djboss.server.default.config=standalone-full.xml`

By default master is started at localhost:8080.
Next starting slave node:
* `./standalone.sh -Djboss.server.default.config=standalone-full.xml -Djboss.socket.binding.port-offset='offset' -Dmaster='ip address:port' -Dalias='slave name' -Dlocal='slave ip'`

-Dlocal parameter is optional. By default localhost ip address is taken.
Start any number of slaves by setting different offsets and that's it, you are ready to chat.
# Licence 
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
