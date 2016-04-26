call mvn -f ../BeispielDocker-common/pom.xml clean install
call mvn -f ../BeispielDocker-ejb/pom.xml clean install
call mvn -f ../BeispielDocker-web/pom.xml clean install
call mvn clean package docker:build-images docker:start-containers package docker:stop-containers -Ddocker.host=192.168.0.15 -Ddocker.port=4243