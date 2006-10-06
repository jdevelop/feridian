Echomine Feridian API
====================
Echomine Feridian API provides an API to communicate with XMPP-compliant
servers.  It also includes support to work with newer versions of Jabber
servers and Jabber-specific JEPs.

License
---------
Echomine Feridian API is distributed under the Apache Software License.  See the enclosed
license file for more information regarding legal issues on using this API.


Changelog and Features
----------------------
See the online Release Notes and JIRA system for more information on version updates.


Requirements
--------------
Feridian API is now developed under JDK 1.5 and requires JDK 1.5+ to run.

There are also external libraries needed by Feridian.  These libraries come as
part of the binary distribution, and should also be added into the classpath 
when using the API.

jakarta commons-logging is required for compile and distribution with Feridian.
  However, you can use any logging packages that is supported by commons-logging.
  Feridian comes by default to work with log4j.  If you have JDK 1.4, you may
  discard the use of log4j and instead go with the JDK's own logging package,
  which is supported by commons-logging.  Take a look at commons-logging
  if you have problems making logging work, but by default if you include
  log4j in the classpath, you should be running just fine.

Installation
---------------
Just add the required libraries in the lib/ directory into your classpath
and you're ready to go.  Feridian API is meant to be a low-level developer API
so it does not contain any application that can be run.  There are some example
codes in the feridian-examples JAR file for reference.  Take a look and then
you're ready to create your own application!

Feridian comes in separate modules.  Other than the core XMPP module, all other
modules are optional.  This helps to save distribution size for those who do not
need all the additional features.

You can find out more in the Documentation hosted online at Echomine's site.


Building From Source
-----------------------
Feridian is now entirely driven by Maven2 for project and distribution management.
For more details on how to build from source, please refer to Feridian's website 
documentation.

Contact Info
--------------

You can find the latest news and releases for Echomine Feridian API at:
    http://open.echomine.org/

If you have any questions or problems, send email to:
    support@echomine.com


Echomine Feridian (c) 2005, 2006 Echomine.  All rights reserved.
