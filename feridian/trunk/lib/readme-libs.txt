==================
Build time
==================
junit.jar (3.8.1)
jibx-*.jar (CVS)
bcel.jar

==================
Runtime
==================

commons-logging.jar (1.0.4)
- required

log4j.jar (1.2.9)
- required if not using JDK 1.4 or above
- optional if using JDK 1.4's own logging system

feridian-*.jar (same as distribution version)
- required, main module libraries

feridian-*-examples.jar (same as distribution version)
- optional
- required only when running examples

xpp3.jar (1.1.3.4.M)
- required
- optional if using StAX as alternative parser

jibx-run.jar (CVS)
- required
