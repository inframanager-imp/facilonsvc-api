# FACILON-API

Facilon Management System API
new one fsdevb2c old facilonservices

## Run user-mgmt-service (port 8081)
java -DSERVICE_1_PORT=8081 -DSERVICE_1_NAME=user-management-service -DAZURE_AD_ISSUER_NAME=fsdevb2c -DAZURE_FORCE_RESET_PASSWORD=true -jar target/azure-user-management-1.0.0-SNAPSHOT.jar

`AZURE_FORCE_RESET_PASSWORD=true` is the correct pairing for the FISP-style setpassword flow: the temp password is generated to satisfy Graph but never emailed; users can only reach the system via the `/investor/setpassword/:azureUserId` link, which drives them through the B2C reset-password policy and clears the force-change flag as part of that flow.

Only flip to `false` if you also stop emailing the setpassword link and want temp-password sign-in to work directly.

## Run platform API
mvn spring-boot:run "-Dspring-boot.run.jvmArguments=-DAZURE_B2C_CLIENT_ID=3b841e08-47a0-4c5e-a0d0-89545697c713 -DAZURE_B2C_CLIENT_SECRET=G518Q~pdvwEoSapIq16Z.1ZRtoIURU5~kbZW.cTi -Dintegration.usalhost:8081/usr-mgmt"er-mgmt.enabled=true -DUSER_MGMT_SERVICE_URL=http://loc