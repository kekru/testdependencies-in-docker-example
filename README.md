[![Build Status](https://travis-ci.org/kekru/testdependencies-in-docker-example.svg?branch=master)](https://travis-ci.org/kekru/testdependencies-in-docker-example)

# testdependencies-in-docker-example
example project for integrating test dependencies in Docker containers in Continuous Delivery

Beispielprojekt für meine Projektarbeit an der FH Dortmund.

Die Varianten Docker Compose, Vagrant mit Docker als Provider und Docker Maven Plugin werdenüber Travis CI durchgeführt. Siehe auch .travis.yml

Vagrant mit Docker als Provisioner in einer Virtual Box VM funktioniert nicht auf Travis CI.  
Zum lokal starten muss VirtualBox und Vagrant installiert sein. Dann kann der Build folgendermaßen gestartet werden:  
`mvn clean verify -P vagrantprovisionervm -Ddocker.host=<IP des Hostcomputer>`  