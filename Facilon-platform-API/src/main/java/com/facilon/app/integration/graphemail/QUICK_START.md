# Graph Email Module - Quick Start Guide

Get up and running with Microsoft Graph API email in 5 minutes!

---

## Prerequisites

✅ Microsoft 365 / Office 365 tenant  
✅ Azure Active Directory access  
✅ Admin permissions to grant consent

---

## Step 1: Azure App Registration (5 minutes)

### 1.1 Create App Registration

1. Go to [Azure Portal](https://portal.azure.com)
2. Navigate to: **Azure Active Directory** → **App registrations** → **New registration**
3. Fill in:
   - **Name**: `Facilon-Graph-Email`
   - **Supported account types**: Single tenant
   - **Redirect URI**: Leave empty
4. Click **Register**
5. **Copy** the following values:
   ```
   Directory (tenant) ID: ____________________
   Application (client) ID: ____________________
   ```

### 1.2 Add API Permissions

1. In your app, go to **API permissions**
2. Click **Add a permission**
3. Select **Microsoft Graph** → **Application permissions**
4. Search and add:
   - ✅ `Mail.Send`
5. Click **Grant admin consent for [Your Org]**
6. Verify: Green checkmark appears

### 1.3 Create Client Secret

1. Go to **Certificates & secrets** → **Client secrets**
2. Click **New client secret**
3. Description: `Facilon Email Service`
4. Expires: 24 months (or your preference)
5. Click **Add**
6. **Copy the Value** immediately (you won't see it again!)
   ```
   Client Secret: ____________________
   ```

✅ **Done!** You now have: Tenant ID, Client ID, and Client Secret

---

## Step 2: Configure Application (2 minutes)

### Option A: Environment Variables (Recommended)

Create `.env` file or set system variables:

```bash
GRAPH_EMAIL_TENANT_ID=your-tenant-id-from-step-1
GRAPH_EMAIL_CLIENT_ID=your-client-id-from-step-1
GRAPH_EMAIL_CLIENT_SECRET=your-client-secret-from-step-1
GRAPH_EMAIL_SENDER=noreply@yourdomain.com
```

### Option B: Application Configuration

Edit `application-dev.yml`:

```yaml
graph:
  email:
    enabled: true  # Change from false to true
    tenant-id: your-tenant-id-here
    client-id: your-client-id-here
    client-secret: your-client-secret-here
    sender-email: noreply@yourdomain.com  # Must be valid M365 mailbox
    sender-name: Facilon Platform
```

⚠️ **Important:** `sender-email` must be a real mailbox in your Microsoft 365 tenant!

---

## Step 3: Verify Sender Mailbox (1 minute)

Make sure the sender email exists:

1. Go to [Microsoft 365 Admin Center](https://admin.microsoft.com)
2. **Users** → **Active users**
3. Check if `noreply@yourdomain.com` exists
4. If not, create it:
   - Click **Add a user**
   - Email: `noreply@yourdomain.com`
   - Assign license (any M365 license will work)

---

## Step 4: Enable & Restart (1 minute)

```bash
# If using environment variables, just restart
mvn spring-boot:run

# Or if already running
# Stop the application and start again
```

---

## Step 5: Test It! (1 minute)

### Test 1: Health Check

```bash
curl http://localhost:8082/facilon/api/graph-email/health
```

✅ **Expected Response:**
```json
{
  "success": true,
  "message": "Graph Email service is operational"
}
```

### Test 2: Send Test Email

```bash
curl -X POST "http://localhost:8082/facilon/api/graph-email/send-otp" \
  -d "toEmail=your-email@example.com" \
  -d "firstName=Test" \
  -d "otp=1234"
```

✅ **Check your inbox!** You should receive an OTP email.

### Test 3: Swagger UI

Open: `http://localhost:8082/facilon/swagger-ui.html`

1. Find **"Graph Email"** section
2. Try **"Send OTP email"** endpoint
3. Execute and check response

---

## Troubleshooting

### ❌ "Failed to obtain Graph token"

**Cause:** Invalid credentials

**Solution:**
1. Double-check Tenant ID, Client ID, and Client Secret
2. Make sure no extra spaces in values
3. Verify app registration exists in Azure

```bash
# Test manually
curl -X POST "https://login.microsoftonline.com/{tenant-id}/oauth2/v2.0/token" \
  -d "grant_type=client_credentials" \
  -d "client_id={client-id}" \
  -d "client_secret={client-secret}" \
  -d "scope=https://graph.microsoft.com/.default"
```

### ❌ "401 Unauthorized"

**Cause:** Missing permissions or admin consent

**Solution:**
1. Go to Azure Portal → App registration
2. Check **API permissions**
3. Verify `Mail.Send` has green checkmark
4. Click **Grant admin consent** if needed

### ❌ "404 Not Found" or "Mailbox not found"

**Cause:** Sender email doesn't exist

**Solution:**
1. Check if `noreply@yourdomain.com` is a real mailbox
2. Go to Microsoft 365 Admin Center
3. Create the user/mailbox
4. Assign any license

### ❌ Module Not Loading

**Cause:** Not enabled in configuration

**Solution:**
1. Check `application-dev.yml`:
   ```yaml
   graph:
     email:
       enabled: true  # Must be true
   ```
2. Restart application

---

## Quick Integration

### Replace SMTP with Graph Email

**Before:**
```java
emailServiceApiClient.sendOtpEmail(email, firstName, otp);
```

**After:**
```java
@Autowired(required = false)
private GraphEmailService graphEmailService;

public void sendOtp(String email, String firstName, String otp) {
    // Try Graph first
    if (graphEmailService != null) {
        boolean sent = graphEmailService.sendOtpEmail(email, firstName, otp);
        if (sent) return;
    }
    
    // Fallback to SMTP
    emailServiceApiClient.sendOtpEmail(email, firstName, otp);
}
```

---

## Production Checklist

Before going live:

- [ ] ✅ Azure app registration created
- [ ] ✅ `Mail.Send` permission granted
- [ ] ✅ Admin consent provided
- [ ] ✅ Sender mailbox exists and has license
- [ ] ✅ Client secret stored in environment variables (not in code!)
- [ ] ✅ Health check passes
- [ ] ✅ Test emails delivered successfully
- [ ] ✅ Logs reviewed for errors
- [ ] ✅ Monitoring set up

---

## Need Help?

📖 **Full Documentation:** `README.md` (in this folder)  
🚀 **Microservice Guide:** `MICROSERVICE_EXTRACTION_GUIDE.md`  
📊 **Implementation Summary:** `docs/GRAPH_EMAIL_MODULE_IMPLEMENTATION.md`  
🔍 **Swagger UI:** `http://localhost:8082/facilon/swagger-ui.html`  
📝 **Logs:** `logs/facilon-api.log` (search for "Graph email")

---

## Success! 🎉

You're now sending emails via Microsoft Graph API!

**Next Steps:**
- Replace SMTP calls with Graph Email
- Monitor email delivery
- Extract as microservice (when needed)
- Enable email tracking

---

**Total Setup Time: ~10 minutes**  
**Difficulty: Easy** ⭐⭐☆☆☆
