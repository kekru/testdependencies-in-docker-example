FROM jboss/wildfly:8.2.1.Final

RUN sed -i "s|<subsystem xmlns=\"urn:jboss:domain:pojo:1.0\"/>||g" /opt/jboss/wildfly/standalone/configuration/standalone.xml

RUN /opt/jboss/wildfly/bin/add-user.sh admin pass --silent

ADD eardir/ /opt/jboss/wildfly/standalone/deployments/

EXPOSE 8080 9990

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
