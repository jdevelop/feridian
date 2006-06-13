- In order to run this maven submodule, some additional steps are required
  - maven-dependency-plugin 2.0-SNAPSHOT is required, and must be locally installed.
    - this should be an automatic process, but in case the plugin cannot be downloaded,
      go to the next step.
  - checkout the latest maven-dependency-plugin from 
    https://svn.apache.org/repos/asf/maven/plugins/trunk/maven-dependency-plugin
  - run and install the dependency plugin "mvn install"
- After this, you can run "mvn package" to properly create a distribution bundle.
