xmpp-over-websocket - a solution for real time message push/poll
===============
This project aimed to provide a solution for real time message pull/poll based on
xmpp and websocket.

 * Homepage: <http://netease.github.com/xmpp-over-websocket>
 * Wiki: <https://github.com/netease/xmpp-over-websocket/wiki/>
 * Issues: <https://github.com/netease/xmpp-over-websocket/issues/>
 * Tags: java, xmpp, websocket

Getting Started
---------------
Clone the submodules belong to this repo, and run the corresponding release script,
that will generate a release package for each submodule. 

Start ganger in three steps: 1) deploy the release packages; 2) start ws-xmpp-master;
2) start the Openfire servers; 3) start ws-xmpp-robot; 4) start ws-xmpp-proxy.
Note: before test, you should have a application server started.

After all the servers started, connect to the proxies with websocket or tcp with XMPP message.

Document
--------
TBD.