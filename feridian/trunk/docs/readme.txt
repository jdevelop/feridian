Echomine Muse API
====================

Echomine Muse API gives you one easy-to-use set of APIs and frameworks for you
to connect with other communication services such as Gnutella, ICQ,
MSN, Yahoo, AIM, IRC, FTP, etc.  It implements the protocols and then adds an
extra layer on top of the protocols so that access to all the services go through
one unified API (similar to how JDBC works).

License
---------

Echomine Muse API is distributed under the Apache Software License.  See the enclosed
license file for more information regarding legal issues on using this API.


Changelog and Features
----------------------

See the included Changelog file for more information on version updates


Requirements
--------------

Muse API is now developed under JDK 1.4 and requires JDK 1.4+ to run.  

There are also external libraries needed by Muse.  These libraries come as
part of the distribution, and should also be added into the classpath when
using the Muse API.

jakarta commons-logging is required for compile and distribution with Muse.
  However, you can use any logging packages that is supported by commons-logging.
  Muse comes by default to work with log4j.  If you have JDK 1.4, you may
  discard the use of log4j and instead go with the JDK's own logging package,
  which is supported by commons-logging.  Take a look at commons-logging
  if you have problems making logging work, but by default if you include
  log4j in the classpath, you should be running just fine.

JUnit test cases are now part of the source code distribution.  The JUnit
  test cases are compiled only when explicitly asked.  It is located in
  its own folder.  In order to run them, you will need to run it through
  one of the ANT tasks.  Normally people should not need to use the tests
  unless they want to check against different JDK and/or having problems
  with their Muse package.


Installation
---------------

Just add the required libraries in the lib/ directory into your classpath
and you're ready to go.  Muse API is meant to be a low-level developer API
so it does not contain any application that can be run.  Well, there are
some example codes under the examples/ directory.  Take a look and then
you're ready to create your own application!


Building From Source
-----------------------

There is an ANT build.xml file in the main directory.  You can simply run it from
there to rebuild the entire source tree (ie. if you happen to modify it).  The
default option is to make a jar for you to use.

You must have ANT installed already on your system in order to compile.
If you do not have ANT installed, you can download it at
http://ant.apache.org/.  After installing ANT, you can change into Muse's
main directory and run any tasks supported by Muse's build.xml file.

You can also create javadocs by running "ant javadocs".  Then you don't need to be
online to read the docs through the web site.

You can jar up the examples as well using "ant jar-examples".
  muse-examples.jar is already created and installed inside the lib/
  directory for you to use to run the examples directly out of
  the distribution.

JUnit tests are not compiled and not packaged into the JAR files.  To run the junit tests,
you simply run the ant task "test.junit" and it will do its work to run the tests.

More up-to-date installation instructions are posted on the website.

Directory Structure
---------------------

lib/ -- contains the muse API along with all the other dependent libraries.
    All the libraries in this directory should be included in your classpath.

src/ -- contains the muse API source files

build/ -- contains files necessary to build the API package.  This package
    uses ANT to build and create distributions.  This directory also contains
    java libraries that are required for compilation. These libraries are not
    necessary when using Muse itself.

docs/ -- may/may not exist, but it contains the javadocs and other docs for
    the Muse API.

bin/ -- contains any build scripts or other binaries that comes along with
    the package.

license/ -- contains all the license files for Muse and also any libraries that
		Muse depends on.

test/ -- contains all the unit testing code for Muse.  You can run the tests
    to make sure the Muse is doing its job and test if it's compatible under
    certain JDKs just in case you run into problems.

examples/ -- contains any example classes that you can run directly to check out
    the capabilities of Muse.  You can read the comments as well as check out the
    code on how to write a sample client.  The examples are there for you to
    learn how to use Muse as fast as possible.

work/ -- contains all the temporary working sources and classes when you run
    ANT.  This is where ANT does all its dirty work.  Running "ant clean" will
    remove this directory.  This directory is a temporary working directory
    and is not packaged with the distribution.

Contact Info
--------------

You can find the latest news and releases for Echomine Muse API at:
    http://open.echomine.org/

If you have any questions or problems, send email to:
    support@echomine.com


Echomine Muse (c) 2001-2005 Echomine.  All right reserved.
