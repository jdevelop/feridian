- The current distribution creation mechanism is separated into two steps.
  This is due to some incompatibility between the assembly plugin and
  the packaging (ie. binary distribution will package up all dependencies,
  including unnecessary ones not required by Feridian). The current way
  is to counter the bugs and incompatibilities with the assembly plugin
  without having to resort to using custom scripting.
  - the source distribution is created by running "mvn assembly:assembly"
    from the main Feridian POM file.
  - the binary distribution is created by running "mvn package"
    from this dist/ directory.
