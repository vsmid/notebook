mvn clean package
$JAVA_HOME/bin/native-image \
-H:EnableURLProtocols=https \
-H:ReflectionConfigurationFiles=./META-INF/native-image/reflect-config.json \
--no-fallback \
-cp target/classes hr.yeti.notebook.Nb nb


