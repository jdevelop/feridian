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
See the included Changelog file for more information on version updates


Requirements
--------------
Feridian API is now developed under JDK 1.4 and requires JDK 1.4+ to run.

There are also external libraries needed by Feridian.  These libraries come as
part of the distribution, and should also be added into the classpath when
using the API.

jakarta commons-logging is required for compile and distribution with Feridian.
  However, you can use any logging packages that is supported by commons-logging.
  Feridian comes by default to work with log4j.  If you have JDK 1.4, you may
  discard the use of log4j and instead go with the JDK's own logging package,
  which is supported by commons-logging.  Take a look at commons-logging
  if you have problems making logging work, but by default if you include
  log4j in the classpath, you should be running just fine.

JUnit test cases are now part of the source code distribution.  The JUnit
  test cases are compiled only when explicitly asked.  It is located in
  its own folder.  In order to run them, you will need to run it through
  one of the ANT tasks.  Normally people should not need to use the tests
  unless they want to check against different JDK and/or having problems
  with their Feridian distribution.


Installation
---------------
Just add the required libraries in the lib/ directory into your classpath
and you're ready to go.  Feridian API is meant to be a low-level developer API
so it does not contain any application that can be run.  There are some example
codes under the examples/ directory for reference.  Take a lookand then
you're ready to create your own application!


Building From Source
-----------------------
There is an ANT build.xml file in the main directory.  You can simply run it from
there to rebuild the entire source tree (ie. if you happen to modify the source).
The default option is to make a jar for you to use.

You must have ANT installed already on your system in order to compile.
If you do not have ANT installed, you can download it at
http://ant.apache.org/.  After installing ANT, you can change into Feridian's
main directory and run any tasks supported by the build.xml file.

You can also create javadocs by running "ant javadocs".  Then you don't need to be
online to read the docs through the web site.

You can jar up the examples as well using "ant jar-<module>-examples".
  All the example jars are already created and installed inside the lib/
  directory for you to use to run the examples directly out of
  the distribution.

JUnit tests are not compiled and not packaged into the JAR files.  To run the junit tests,
you simply run the ant task "test-all" and it will do its work to run the tests.

More up-to-date installation instructions are posted on the website.

Directory Structure
---------------------
lib/ -- contains the API along with all the other dependent libraries.
    All the libraries in this directory should be included in your classpath.

src/ -- contains the API source files

build/ -- contains files necessary to build the API package.  This package
    uses ANT to build and create distributions.  This directory also contains
    java libraries that are required for compilation. These libraries are not
    necessary when running itself.

contrib/ -- contains useful files (log4j config file) that is not
    not required to run Feridian but is useful as reference to enhance your
    experience in using the API.

docs/ -- may/may not exist, but it contains the javadocs and other docs for
    the API.

license/ -- contains all the license files for Feridian and also any libraries that
		it depends on.

test/ -- contains all the unit testing codes.  You can run the tests
    to make sure the Feridian is doing its job and test if it's compatible under
    certain JDKs just in case you run into problems.

examples/ -- contains any example classes that you can run directly to check out
    the capabilities of Feridian.  You can read the comments as well as check out the
    code on how to write a sample client.  The examples are there for you to
    learn how to use the API as fast as possible.

work/ -- contains all the temporary working sources and classes when you run
    ANT.  This is where ANT keeps all its dirty work.  Running "ant clean" will
    remove this directory.  This directory is a temporary working directory
    and is not packaged with the distribution.

Contact Info
--------------

You can find the latest news and releases for Echomine Feridian API at:
    http://open.echomine.org/

If you have any questions or problems, send email to:
    support@echomine.com


Echomine Feridian (c) 2005 Echomine.  All rights reserved.
