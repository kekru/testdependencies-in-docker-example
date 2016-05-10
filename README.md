[![Build Status](https://travis-ci.org/kekru/testdependencies-in-docker-example.svg?branch=master)](https://travis-ci.org/kekru/testdependencies-in-docker-example)

# testdependencies-in-docker-example
example project for integrating test dependencies in Docker containers in Continuous Delivery

Beispielprojekt für meine Projektarbeit an der FH Dortmund.

Die Varianten Docker Compose, Vagrant mit Docker als Provider und Docker Maven Plugin werden über Travis CI durchgeführt. Siehe auch .travis.yml  

Die Travis CI Ergebnisse können unter [https://travis-ci.org/kekru/testdependencies-in-docker-example](https://travis-ci.org/kekru/testdependencies-in-docker-example) eingesehen werden.  

Vagrant mit Docker als Provisioner in einer Virtual Box VM konnte nicht auf Travis CI zum Laufen gebracht werden.  
Zum lokalen Starten muss Java JDK 1.8, Maven, VirtualBox und Vagrant installiert sein. Dann kann der Build folgendermaßen gestartet werden:  
`mvn clean verify -P vagrantprovisionervm -Ddocker.host=<IP des Hostcomputer>`  
