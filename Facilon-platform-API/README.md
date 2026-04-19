# FACILON-API


Facilon Management System API
java -DSERVICE_1_PORT=8081 -DSERVICE_1_NAME=user-management-service -DAZURE_AD_ISSUER_NAME=facilonservices -DAZURE_FORCE_RESET_PASSWORD=true -jar target/azure-user-management-1.0.0-SNAPSHOT.jar

mvn spring-boot:run "-Dspring-boot.run.jvmArguments=-DAZURE_B2C_CLIENT_ID=3b841e08-47a0-4c5e-a0d0-89545697c713 -DAZURE_B2C_CLIENT_SECRET=G518Q~pdvwEoSapIq16Z.1ZRtoIURU5~kbZW.cTi -Dintegration.user-mgmt.enabled=true -DUSER_MGMT_SERVICE_URL=http://localhost:9090"