package com.azure.user_management.application.controller;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.user_management.application.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.log4j.Log4j2;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Log4j2
@RestController
@RequestMapping("/api/v1/azure-ad")
public class AzureADUserController {

    @Value("${azure.issuerName}")
    private String issuerName;

    @Value("${azure.forceResetPassword}")
    private Boolean forceResetPassword;

    private boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    private Boolean isNullOrEmpty(String textString) {
        return textString == null || textString.trim().isEmpty();
    }

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/create-user-latest")
    public ResponseEntity<MicrosoftGraphResponseDto> createUser_latest(@RequestBody SignUpDto signUpDto) {
        try {
            log.info("Into create user latest with => {}", signUpDto.toString());
            if (signUpDto.getClientId() == null || signUpDto.getClientSecret() == null
                    || signUpDto.getTenantId() == null) {
                MicrosoftGraphResponseDto microsoftGraphResponseDto = new MicrosoftGraphResponseDto();
                microsoftGraphResponseDto.setErrorMsg("Missing clientId, clientSecret, or tenantId in the payload.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(microsoftGraphResponseDto);
            }

            MicrosoftLoginResponseDto accessToken = getAccessTokenUpdated(signUpDto.getClientId(),
                    signUpDto.getClientSecret(), signUpDto.getTenantId());

            if (accessToken == null) {
                throw new Exception("Access token is null or empty.");
            }

            log.info("Access Token on Create User=> {}", accessToken);

            MicrosoftGraphResponseDto microsoftGraphResponseDto = registerUserToGraph(
                    signUpDto.getFirstName() + " " + signUpDto.getLastName(),
                    signUpDto.getEmail(), signUpDto.getMobilePhone(), signUpDto.getCountry(), signUpDto.getPassword(),
                    accessToken.getAccess_token().trim());

            if (microsoftGraphResponseDto == null) {
                log.warn("registerUserToGraph returned null, check logs for Graph API error");
                MicrosoftGraphResponseDto graphResponseDto = new MicrosoftGraphResponseDto();
                graphResponseDto.setErrorMsg("Azure AD creation failed. Check service logs.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(graphResponseDto);
            }

            if (microsoftGraphResponseDto.getErrorMsg() != null) {
                log.warn("Azure AD reported error: {}", microsoftGraphResponseDto.getErrorMsg());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(microsoftGraphResponseDto);
            }

            log.info("Microsoft Graph Response => {}", microsoftGraphResponseDto);

            return ResponseEntity.status(HttpStatus.CREATED).body(microsoftGraphResponseDto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ChangePasswordResponseDto> changePassword(@RequestBody PasswordChangeDto passwordChangeDto) {
        try {
            log.info("Into Change password with => {}", passwordChangeDto.toString());
            if (passwordChangeDto.getClientId() == null || passwordChangeDto.getClientSecret() == null
                    || passwordChangeDto.getTenantId() == null) {
                ChangePasswordResponseDto changePasswordResponseDto = new ChangePasswordResponseDto();
                changePasswordResponseDto.setErrorMsg("Missing clientId, clientSecret, or tenantId in the payload.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(changePasswordResponseDto);
            }

            MicrosoftLoginResponseDto accessToken = getAccessTokenUpdated(passwordChangeDto.getClientId(),
                    passwordChangeDto.getClientSecret(), passwordChangeDto.getTenantId());

            if (accessToken == null) {
                throw new Exception("Access token is null or empty.");
            }

            log.info("Access Token for Change password => {}", accessToken);

            ChangePasswordResponseDto changePasswordResponseDto = changePasswordToGraph(passwordChangeDto.getUserId(),
                    passwordChangeDto.getNewPassword(),
                    accessToken.getAccess_token().trim());

            if (changePasswordResponseDto == null) {
                ChangePasswordResponseDto passwordResponseDto = new ChangePasswordResponseDto();
                passwordResponseDto.setErrorMsg("Invalid User");
                log.info("getting Microsoft Graph Response is null on Change password");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(passwordResponseDto);
            }

            log.info("Microsoft Graph Response For change password => {}", changePasswordResponseDto);

            return ResponseEntity.status(HttpStatus.CREATED).body(changePasswordResponseDto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/change-mobile")
    public ResponseEntity<ChangeMobileResponseDto> changeMobile(@RequestBody PasswordChangeDto passwordChangeDto) {
        try {
            log.info("Into Change Mobile with => {}", passwordChangeDto.toString());
            if (passwordChangeDto.getClientId() == null || passwordChangeDto.getClientSecret() == null
                    || passwordChangeDto.getTenantId() == null) {
                ChangeMobileResponseDto changeMobileResponseDto = new ChangeMobileResponseDto();
                changeMobileResponseDto.setErrorMsg("Missing clientId, clientSecret, or tenantId in the payload.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(changeMobileResponseDto);
            }

            MicrosoftLoginResponseDto accessToken = getAccessTokenUpdated(passwordChangeDto.getClientId(),
                    passwordChangeDto.getClientSecret(), passwordChangeDto.getTenantId());

            if (accessToken == null) {
                throw new Exception("Access token is null or empty.");
            }

            log.info("Access Token for Change Mobile => {}", accessToken);

            ChangeMobileResponseDto changeMobileResponseDto = changeMobileToGraph(passwordChangeDto.getUserId(),
                    passwordChangeDto.getNewMobile(),
                    accessToken.getAccess_token().trim());

            if (changeMobileResponseDto == null) {
                ChangeMobileResponseDto graphResponseDto = new ChangeMobileResponseDto();
                graphResponseDto.setErrorMsg("Invalid User");
                log.info("getting Microsoft Graph Response is null on Change Mobile");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(graphResponseDto);
            }

            log.info("Response For change Mobile => {}", changeMobileResponseDto);

            if (changeMobileResponseDto.getStatus().equals("Failed")) {
                return ResponseEntity.status(HttpStatus.CREATED).body(changeMobileResponseDto);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(changeMobileResponseDto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ChangePasswordResponseDto changePasswordToGraph(String userId, String newPassword, String accessToken) {
        Gson gson = new Gson();
        okhttp3.MediaType mediaType = MediaType.parse("application/json");
        MicrosoftGraphPasswordProfileDto passwordProfile = new MicrosoftGraphPasswordProfileDto();
        passwordProfile.setPassword(newPassword);
        passwordProfile.setForceChangePasswordNextSignIn(false);

        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType,
                "{\"passwordProfile\":" + getJson(passwordProfile) + "}");
        try {
            String url = "https://graph.microsoft.com/v1.0/" + issuerName + ".onmicrosoft.com/users/" + userId;
            Response response = patchServiceCall(url, body, accessToken);
            if (!response.isSuccessful()) {
                return null;
            }

            ChangePasswordResponseDto changePasswordResponseDto = new ChangePasswordResponseDto();
            changePasswordResponseDto.setStatus("Success");
            changePasswordResponseDto.setMessage("Password Update Successfully");
            return changePasswordResponseDto;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ChangeMobileResponseDto changeMobileToGraph(String userId, String newMobile, String accessToken) {
        Gson gson = new Gson();
        okhttp3.MediaType mediaType = MediaType.parse("application/json");
        Map<String, Object> jsonbody = Map.of(
                "mobilePhone", newMobile);

        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, getJson(jsonbody));
        try {
            String url = "https://graph.microsoft.com/v1.0/" + issuerName + ".onmicrosoft.com/users/" + userId;
            Response response = patchServiceCall(url, body, accessToken);
            if (!response.isSuccessful()) {
                return null;
            }
            url = "https://graph.microsoft.com/v1.0/" + issuerName + ".onmicrosoft.com/users/" + userId
                    + "/authentication/phoneMethods";
            String responseStr = getServiceCall(url, accessToken);
            if (responseStr != null) {
                JsonParser jsonParser = new JsonParser();
                JsonObject object = (JsonObject) jsonParser.parse(responseStr);
                JsonArray foundAuthentications = object.get("value").getAsJsonArray();
                if (!foundAuthentications.isEmpty()) {
                    for (int i = 0; i < foundAuthentications.size(); i++) {
                        JsonObject jsonObject = foundAuthentications.get(i).getAsJsonObject();
                        if (jsonObject.get("phoneType").getAsString().equals("mobile")) {
                            String phoneMethodId = jsonObject.get("id").toString().replace("\"", "");
                            String oldMobile = jsonObject.get("phoneNumber").toString().replace("\"", "");
                            if (oldMobile.replaceAll(" ", "").equals(newMobile.replaceAll(" ", ""))) {
                                ChangeMobileResponseDto changeMobileResponseDto = new ChangeMobileResponseDto();
                                changeMobileResponseDto.setStatus("Failed");
                                changeMobileResponseDto
                                        .setMessage("Old mobile number and new mobile numbers should not match");
                                return changeMobileResponseDto;
                            }
                            url = "https://graph.microsoft.com/v1.0/" + issuerName + ".onmicrosoft.com/users/" + userId
                                    + "/authentication/phoneMethods/" + phoneMethodId;
                            response = deleteServiceCall(url, accessToken);
                            if (!response.isSuccessful()) {
                                return null;
                            }
                            log.info("Old mobile MFA services deleted successfully");
                        }
                    }
                }
            }

            String authURL = "https://graph.microsoft.com/v1.0/users/" + userId + "/authentication/phoneMethods";
            UserAuthenticationMobile userAuthenticationMobile = new UserAuthenticationMobile();
            userAuthenticationMobile.setPhoneNumber(newMobile);
            userAuthenticationMobile.setPhoneType("mobile");
            okhttp3.RequestBody bodyAuth = okhttp3.RequestBody.create(mediaType, getJson(userAuthenticationMobile));
            String authResponse = postServiceCall(authURL, bodyAuth, accessToken);
            log.info("Auth Response = " + authResponse);
            ChangeMobileResponseDto changeMobileResponseDto = new ChangeMobileResponseDto();
            changeMobileResponseDto.setStatus("Success");
            changeMobileResponseDto.setMessage("Mobile Changed successfully");
            return changeMobileResponseDto;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/get_last_login_details")
    public ResponseEntity<SignInActivityResponseDto> get_last_login_details(@RequestBody SignInDto signInDto) {
        try {
            if (signInDto.getClientId() == null || signInDto.getClientSecret() == null
                    || signInDto.getTenantId() == null) {
                SignInActivityResponseDto signInActivityResponseDto = new SignInActivityResponseDto();
                signInActivityResponseDto.setErrorMsg("Missing clientId, clientSecret, or tenantId in the payload.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(signInActivityResponseDto);
            }

            MicrosoftLoginResponseDto accessToken = getAccessTokenUpdated(signInDto.getClientId(),
                    signInDto.getClientSecret(), signInDto.getTenantId());

            if (accessToken == null) {
                throw new Exception("Access token is null or empty.");
            }

            String url = "https://graph.microsoft.com/v1.0/users/" + signInDto.getUserId() + "?$select=signInActivity";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken.getAccess_token());

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            SignInActivityResponseDto signInActivityResponseDto = new SignInActivityResponseDto();

            if (response.getStatusCode().value() == 204) {
                signInActivityResponseDto.setErrorMsg("No data available");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(signInActivityResponseDto);
            } else {
                Map<String, Object> responseBody = response.getBody();
                Map<String, Object> signInActivity = (Map<String, Object>) responseBody.get("signInActivity");
                if (signInActivity != null) {
                    signInActivityResponseDto.setObjectMap(signInActivity);
                } else {
                    signInActivityResponseDto.setErrorMsg("No data available");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(signInActivityResponseDto);
                }
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(signInActivityResponseDto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private MicrosoftGraphResponseDto registerUserToGraph(String name, String email, String mobilePhone, String country,
            String password, String accessToken) {
        Gson gson = new Gson();
        okhttp3.MediaType mediaType = MediaType.parse("application/json");
        MicrosoftGraphRequestDto requestData = new MicrosoftGraphRequestDto();
        requestData.setMail(email);
        requestData.setDisplayName(name);
        requestData.setMobilePhone(mobilePhone);
        requestData.setCountry(country);
        requestData.setPasswordPolicies("DisablePasswordExpiration");
        MicrosoftGraphPasswordProfileDto passwordProfile = new MicrosoftGraphPasswordProfileDto();
        passwordProfile.setPassword(password);
        passwordProfile.setForceChangePasswordNextSignIn(forceResetPassword);
        requestData.setPasswordProfile(passwordProfile);
        MicrosoftIdentitiesDto identity = new MicrosoftIdentitiesDto();
        identity.setIssuer(issuerName + ".onmicrosoft.com");
        identity.setIssuerAssignedId(email);
        List<MicrosoftIdentitiesDto> identities = new ArrayList<>();
        identities.add(identity);
        requestData.setIdentities(identities);
        // Add mailNickname - required for some Azure AD configurations
        requestData.setMailNickname(email.split("@")[0]);
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, getJson(requestData));
        try {
            String url = "https://graph.microsoft.com/v1.0/" + issuerName + ".onmicrosoft.com/users";
            String response = postServiceCall(url, body, accessToken);
            if (response == null || response.isEmpty()) {
                return null;
            } else if (response.equals("User already exists")) {
                MicrosoftGraphResponseDto graphResponseDto = new MicrosoftGraphResponseDto();
                graphResponseDto.setErrorMsg(response);
                return graphResponseDto;
            }
            MicrosoftGraphResponseDto microsoftGraphResponseDto = gson.fromJson(response,
                    MicrosoftGraphResponseDto.class);
            // Graph error responses parse into a DTO with id=null. Surface the Graph error so
            // upstream callers don't think creation succeeded.
            if (microsoftGraphResponseDto == null
                    || microsoftGraphResponseDto.getId() == null
                    || microsoftGraphResponseDto.getId().isBlank()) {
                MicrosoftGraphResponseDto err = microsoftGraphResponseDto != null
                        ? microsoftGraphResponseDto
                        : new MicrosoftGraphResponseDto();
                err.setErrorMsg(extractGraphErrorMessage(response));
                log.warn("Graph create-user returned no id. Body: {}", response);
                return err;
            }
            String authURL = "https://graph.microsoft.com/v1.0/users/" + microsoftGraphResponseDto.getId()
                    + "/authentication/phoneMethods";
            UserAuthenticationMobile userAuthenticationMobile = new UserAuthenticationMobile();
            userAuthenticationMobile.setPhoneNumber(mobilePhone);
            userAuthenticationMobile.setPhoneType("mobile");
            okhttp3.RequestBody bodyAuth = okhttp3.RequestBody.create(mediaType, getJson(userAuthenticationMobile));
            String authResponse = postServiceCall(authURL, bodyAuth, accessToken);
            log.info("Auth Response From Add Phone = {}", authResponse);

            // Invitation invitation = new Invitation();
            // invitation.invitedUserEmailAddress = email;
            // invitation.inviteRedirectUrl =
            // "https://fispoke-dev.firstrate-wealthtech.com";
            // invitation.sendInvitationMessage = true;
            // String inviteURL = "https://graph.microsoft.com/v1.0/invitations";
            // okhttp3.RequestBody bodyAuthInvite = okhttp3.RequestBody.create(mediaType,
            // getJson(invitation));
            // String authResponseInvite = postServiceCall(inviteURL, bodyAuthInvite,
            // accessToken);
            // log.info("Auth Response From Invite = {}", authResponseInvite);
            return microsoftGraphResponseDto;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String extractGraphErrorMessage(String body) {
        if (body == null || body.isBlank()) return "Azure AD user creation failed (empty response)";
        try {
            JsonObject root = JsonParser.parseString(body).getAsJsonObject();
            if (root.has("error")) {
                JsonObject err = root.getAsJsonObject("error");
                String code = err.has("code") ? err.get("code").getAsString() : "";
                String msg = err.has("message") ? err.get("message").getAsString() : "";
                return (code.isEmpty() ? "" : code + ": ") + (msg.isEmpty() ? body : msg);
            }
        } catch (Exception ignored) {
        }
        return "Azure AD user creation failed: " + body;
    }

    private String postServiceCall(String url, okhttp3.RequestBody requestBody, String accessToken) throws Exception {
        OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(10, TimeUnit.MINUTES).build();
        String key = "Bearer " + accessToken;
        Request request = new Request.Builder().url(url).post(requestBody).addHeader("Authorization", key).build();
        Response response = client.newCall(request).execute();
        InputStream byteStream = response.body().byteStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(byteStream));
        String lines = "";
        String line = "";
        while ((line = br.readLine()) != null) {
            lines = lines + line;
        }
        log.info("Line output => {}", lines);
        br.close();
        byteStream.close();
        response.close();
        if (response.isSuccessful()) {
            return lines;
        } else if (lines.contains("Another object with the same value for property proxyAddresses already exists.")) {
            return "User already exists";
        } else {
            log.warn("Graph API Call Failed. HTTP Status: {}, Body: {}", response.code(), lines);
            return lines; // Return the actual error JSON
        }
    }

    private Response patchServiceCall(String url, okhttp3.RequestBody requestBody, String accessToken)
            throws Exception {
        OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(10, TimeUnit.MINUTES).build();
        String key = "Bearer " + accessToken;
        Request request = new Request.Builder().url(url).patch(requestBody).addHeader("Authorization", key).build();
        return client.newCall(request).execute();
    }

    private String getServiceCall(String url, String accessToken) throws Exception {
        OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(10, TimeUnit.MINUTES).build();
        String key = "Bearer " + accessToken;
        Request request = new Request.Builder().url(url).get().addHeader("Authorization", key).build();
        Response response = client.newCall(request).execute();
        InputStream byteStream = response.body().byteStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(byteStream));
        String lines = "";
        String line = "";
        while ((line = br.readLine()) != null) {
            lines = lines + line;
        }
        br.close();
        byteStream.close();
        response.close();
        if (!response.isSuccessful())
            return null;
        return lines;
    }

    private Response deleteServiceCall(String url, String accessToken) throws Exception {
        OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(10, TimeUnit.MINUTES).build();
        String key = "Bearer " + accessToken;
        Request request = new Request.Builder().url(url).delete().addHeader("Authorization", key).build();
        return client.newCall(request).execute();
    }

    public static String getJson(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    @PostMapping("/create-user")
    public ResponseEntity<String> createUser(@RequestBody Map<String, Object> userPayload) {
        try {
            String clientId = (String) userPayload.get("clientId");
            String clientSecret = (String) userPayload.get("clientSecret");
            String tenantId = (String) userPayload.get("tenantId");

            if (clientId == null || clientSecret == null || tenantId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Missing clientId, clientSecret, or tenantId in the payload.");
            }

            String accessToken = getAccessToken(clientId, clientSecret, tenantId);

            if (accessToken == null || accessToken.isEmpty()) {
                throw new Exception("Access token is null or empty.");
            }

            userPayload.remove("clientId");
            userPayload.remove("clientSecret");
            userPayload.remove("tenantId");

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(userPayload, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://graph.microsoft.com/v1.0/" + issuerName + ".onmicrosoft.com/users",
                    request,
                    String.class);

            if (response.getStatusCode() == HttpStatus.CREATED) {
                return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully.");
            } else {
                return ResponseEntity.status(response.getStatusCode())
                        .body("Failed to create user. Response: " + response.getBody());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating user: " + e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, Object> userPayload) {
        try {
            String clientId = (String) userPayload.get("clientId");
            String clientSecret = (String) userPayload.get("clientSecret");
            String tenantId = (String) userPayload.get("tenantId");
            String userId = (String) userPayload.get("userId");
            String newPassword = (String) userPayload.get("newPassword");

            if (clientId == null || clientSecret == null || tenantId == null || userId == null || newPassword == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Missing clientId, clientSecret, tenantId, userId, or newPassword in the payload.");
            }

            String accessToken = getAccessToken(clientId, clientSecret, tenantId);

            if (accessToken == null || accessToken.isEmpty()) {
                throw new Exception("Access token is null or empty.");
            }

            userPayload.remove("clientId");
            userPayload.remove("clientSecret");
            userPayload.remove("tenantId");
            userPayload.remove("userId");
            userPayload.remove("newPassword");

            Map<String, Object> passwordPayload = new HashMap<>();
            Map<String, Object> passwordProfile = new HashMap<>();
            passwordProfile.put("password", newPassword);
            passwordProfile.put("forceChangePasswordNextSignIn", false);
            passwordPayload.put("passwordProfile", passwordProfile);

            // RestTemplate restTemplate = new RestTemplate(new
            // HttpComponentsClientHttpRequestFactory(HttpClients.createDefault()));
            //
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);
            //
            // HttpEntity<Map<String, Object>> request = new HttpEntity<>(passwordPayload,
            // headers);
            //
            // ResponseEntity<String> response = restTemplate.exchange(
            // "https://graph.microsoft.com/v1.0/users/" + userId,
            // HttpMethod.PATCH,
            // request,
            // String.class
            // );
            //
            // if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
            // return ResponseEntity.status(HttpStatus.OK).body("Password reset
            // successfully.");
            // } else {
            // return ResponseEntity.status(response.getStatusCode()).body("Failed to reset
            // password. Response: " + response.getBody());
            // }
            WebClient webClient = WebClient.builder().baseUrl("https://graph.microsoft.com/v1.0").build();

            String url = "/users/" + userId;

            String response = webClient.patch()
                    .uri(url)
                    .headers(httpHeaders -> httpHeaders.setAll(headers.toSingleValueMap()))
                    .bodyValue(passwordPayload)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError,
                            clientResponse -> clientResponse.bodyToMono(String.class).map(Exception::new))
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.status(HttpStatus.OK).body("Password reset successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error resetting password: " + e.getMessage());
        }
    }

    private String getAccessToken_second(String clientId, String clientSecret, String tenantId) throws Exception {
        String scope = "https://graph.microsoft.com/.default";
        ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .tenantId(tenantId)
                .build();
        TokenRequestContext tokenRequestContext = new TokenRequestContext()
                .addScopes(scope);
        AccessToken token = clientSecretCredential.getToken(tokenRequestContext).block();
        assert token != null;
        return token.getToken();
    }

    private MicrosoftLoginResponseDto getAccessTokenUpdated(String clientId, String clientSecret, String tenantId)
            throws Exception {
        Gson gson = new Gson();
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("https://login.microsoftonline.com/").append(tenantId).append("/oauth2/v2.0/token");

        StringBuilder requestDataBuilder = new StringBuilder();
        requestDataBuilder.append("grant_type=client_credentials&").append("client_id=")
                .append(clientId).append("&client_secret=")
                .append(clientSecret).append("&scope=")
                .append("https://graph.microsoft.com/.default");
        okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/x-www-form-urlencoded");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, requestDataBuilder.toString());
        try {
            String response = postServiceCall(urlBuilder.toString(), body);
            if (isNullOrEmpty(response))
                return null;
            MicrosoftLoginResponseDto loginResponseDto = gson.fromJson(response, MicrosoftLoginResponseDto.class);
            return loginResponseDto;
        } catch (Exception e) {
            log.error("Exception in getAccessTokenUpdated", e);
            return null;
        }
    }

    private String postServiceCall(String url, okhttp3.RequestBody requestBody) throws Exception {
        if (isNullOrEmpty(url)) {
            return null;
        }

        OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(10, TimeUnit.MINUTES).build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            InputStream byteStream = response.body().byteStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(byteStream));
            String lines = "";
            String line = "";
            while ((line = br.readLine()) != null) {
                lines = lines + line;
            }
            log.info("Lines => {}", lines);
            br.close();
            byteStream.close();
            response.close();
            return lines;
        } else {
            String errorBody = "";
            try {
                if (response.body() != null) {
                    errorBody = response.body().string();
                }
            } catch (Exception e) {
                log.error("Failed to read error body", e);
            }
            log.error("Failed to get access token. Status: {}, Body: {}", response.code(), errorBody);
            return null;
        }
    }

    private String getAccessToken(String clientId, String clientSecret, String tenantId) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> tokenRequestPayload = new LinkedMultiValueMap<>();
        tokenRequestPayload.add("grant_type", "client_credentials");
        tokenRequestPayload.add("client_id", clientId);
        tokenRequestPayload.add("client_secret", clientSecret);
        tokenRequestPayload.add("scope", "https://graph.microsoft.com/.default");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(tokenRequestPayload, headers);

        String tokenEndpoint = "https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/token";

        ResponseEntity<Map> response = restTemplate.postForEntity(
                tokenEndpoint,
                request,
                Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("access_token")) {
                return responseBody.get("access_token").toString();
            } else {
                throw new Exception("Access token not found in the response.");
            }
        } else {
            throw new Exception("Failed to get access token. Response: " + response.getBody());
        }
    }

    @PostMapping("/log-password-failure-count")
    public Map<String, Object> logPasswordFailureCount(@RequestBody PasswordChangeDto passwordChangeDto)
            throws Exception {

        String accessToken = getAccessToken(passwordChangeDto.getClientId(), passwordChangeDto.getClientSecret(),
                passwordChangeDto.getTenantId());

        String url = "https://graph.microsoft.com/v1.0/auditLogs/signIns" +
                "?$top=50" +
                "&$orderby=createdDateTime desc" +
                "&$select=userPrincipalName,status,createdDateTime";

        // 3. OkHttp GET request
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .build();

        // 4. Execute call
        String response = getServiceCall(request);

        // return JsonParser.parseString(response).getAsJsonObject();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response, Map.class);
    }

    private String getServiceCall(okhttp3.Request request) throws Exception {

        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();

        try (okhttp3.Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new RuntimeException("HTTP error: " + response.code());
            }

            return response.body().string();
        }
    }

}
