# FACILON-API

Facilon Management System API
new one fsdevb2c old facilonservices

## Run user-mgmt-service (port 8081)
java -DSERVICE_1_PORT=8081 -DSERVICE_1_NAME=user-management-service -DAZURE_AD_ISSUER_NAME=fsdevb2c -jar target/azure-user-management-1.0.0-SNAPSHOT.jar



`AZURE_FORCE_RESET_PASSWORD` defaults to `false` (Laravel-parity: temp password works at first login, change is voluntary). Set `-DAZURE_FORCE_RESET_PASSWORD=true` only if you also wire up a B2C reset-password redirect on the UI; otherwise first login returns "Your password has expired".

## Run platform API
mvn spring-boot:run "-Dspring-boot.run.jvmArguments=-DAZURE_B2C_CLIENT_ID=3b841e08-47a0-4c5e-a0d0-89545697c713 -DAZURE_B2C_CLIENT_SECRET=G518Q~pdvwEoSapIq16Z.1ZRtoIURU5~kbZW.cTi -Dintegration.user-mgmt.enabled=true -DUSER_MGMT_SERVICE_URL=http://localhost:8081/usr-mgmt"