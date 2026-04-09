# Page Flow Diagrams - Visual Reference

## 1. Self-Registration Complete Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                    LANDING PAGE (/)                              │
│                    "Register as Investor"                        │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│         STEP 0: Main Registration                               │
│         GET  /investor/register/main/step                       │
│         POST /investor/register/main/step                       │
│                                                                  │
│         [Select: Self / Entity]                                  │
│         [Enter: Full Name, Country of Incorporation]            │
│         [Generate: unique_code = YYYYMMDD + random(1000-9999)]  │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│         STEP 1: Contact Information                             │
│         GET  /investor/register/step1/{encrypted_code}          │
│         POST /investor/register/step1/{encrypted_code}           │
│                                                                  │
│         [Enter: First/Middle/Last Name]                         │
│         [Enter: Email, Mobile, DOB, Gender]                     │
│         [Generate: Email OTP (4 digits)]                        │
│         [Generate: SMS OTP (4 digits)]                          │
│         [Send: Email OTP via Microsoft Graph]                   │
│         [Send: SMS OTP via SMS Country API]                     │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│         STEP 2: OTP Verification                                │
│         GET  /investor/register/step2/{encrypted_code}          │
│         POST /investor/register/step2/verify-otp                │
│                                                                  │
│         [Enter: Email OTP]                                      │
│         [Enter: SMS OTP]                                        │
│         [Validate: OTP matches & not expired]                   │
│         [Mark: Email & Mobile as verified]                      │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│         STEP 3: Full Name Entry                                 │
│         GET  /investor/register/step3/{encrypted_code}         │
│         POST /investor/register/step3/enter_full_name           │
│                                                                  │
│         [Enter: Full Name]                                      │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│         STEP 4: Complete Registration                           │
│         GET  /investor/register/step4/{encrypted_code}         │
│         POST /investor/register/step4/{encrypted_code}           │
│                                                                  │
│         [Enter: All investor details]                           │
│         [Save: To local database]                                │
│         [Create: Contact in Dataverse (POST)]                    │
│         [Create: Investor in Dataverse (POST)]                    │
│         [Link: Contact ↔ Investor (PATCH)]                       │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│         Thank You Page                                           │
│         GET  /investor/register/thank-you                        │
│         OR                                                       │
│         GET  /investor/registration/thank-you (no OCI/PAN)       │
│                                                                  │
│         [Display: Registration success message]                  │
│         [Link: Login via Azure AD B2C]                           │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│         Azure AD B2C Login                                       │
│         [Redirect to Azure login]                                │
│         [After login: Redirect to /home/{code}]                  │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│         Investor Dashboard                                       │
│         GET  /home/{encrypted_code}                             │
│                                                                  │
│         [Shows: Progress overview]                               │
│         [Navigation: To all sections]                            │
└─────────────────────────────────────────────────────────────────┘
```

## 2. Introduced Investor Registration Flow

```
┌─────────────────────────────────────────────────────────────────┐
│         Email Received with introduce_id                        │
│         [Link: /introduce-investor/{introduce_id}]              │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│         Fetch from Dataverse                                     │
│         GET  /introduce-investor/{introduce_id}                  │
│                                                                  │
│         [Fetch: Investor data from Dataverse]                   │
│         [Pre-fill: Form with introduced investor details]        │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│         Main Step                                                │
│         POST /introduce-investor/{introduce_id}                  │
│                                                                  │
│         [Generate: unique_code]                                  │
│         [Save: To local database]                                │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│         STEP 1: Contact Information                               │
│         GET  /introduce-investor/step1/{encrypted_code}         │
│         POST /introduce-investor/step1/{encrypted_code}          │
│                                                                  │
│         [Enter: Email, Mobile, DOB, Gender]                     │
│         [Send: OTP]                                              │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│         STEP 2: OTP Verification                                │
│         GET  /introduce-investor/step2/{encrypted_code}         │
│         POST /introduce/investor/register/step2/verify-otp       │
│                                                                  │
│         [Verify: OTP]                                            │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│         STEP 4: Complete Registration                            │
│         GET  /introduce/investor/register/step4/{encrypted_code} │
│         POST /introduce/investor/register/step4/{encrypted_code} │
│                                                                  │
│         [Enter: All investor details]                            │
│         [Update: Contact in Dataverse (PATCH)]                   │
│         [Update: Investor in Dataverse (PATCH)]                   │
│         [Link: Contact ↔ Investor (PATCH)]                       │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│         Thank You → Dashboard                                    │
└─────────────────────────────────────────────────────────────────┘
```

## 3. Investor Dashboard Navigation Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                    DASHBOARD HOME                                │
│         GET  /home/{encrypted_code}                              │
│                                                                  │
│         [Progress Overview]                                      │
│         [Quick Actions]                                          │
│         [Status Indicators]                                      │
└─────┬──────┬──────┬──────┬──────┬──────┬──────┬──────┬──────────┘
      │      │      │      │      │      │      │      │
      ▼      ▼      ▼      ▼      ▼      ▼      ▼      ▼
┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐
│ My   │ │ Info │ │ Docs │ │ Onbd │ │ In-  │ │ Phys │ │ Acct │
│Prog  │ │      │ │      │ │ Docs │ │Pers  │ │ Sub  │ │      │
└──────┘ └──────┘ └──────┘ └──────┘ └──────┘ └──────┘ └──────┘
```

### 3.1 Information Management Sub-Flow

```
┌─────────────────────────────────────────────────────────────────┐
│         Information Page                                         │
│         GET  /information/{encrypted_code}                      │
│                                                                  │
│         [Tabs: 8 Information Sections]                          │
└─────┬──────┬──────┬──────┬──────┬──────┬──────┬──────┬──────────┘
      │      │      │      │      │      │      │      │
      ▼      ▼      ▼      ▼      ▼      ▼      ▼      ▼
┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐
│Pers  │ │Pass  │ │Resid │ │Tax   │ │Bank  │ │Cont  │ │Nomin │ │Risk  │
│Info  │ │port  │ │Status│ │Info  │ │Detls │ │Detls │ │ation │ │Prof  │
└──┬───┘ └──┬───┘ └──┬───┘ └──┬───┘ └──┬───┘ └──┬───┘ └──┬───┘ └──┬───┘
   │       │       │       │       │       │       │       │
   ▼       ▼       ▼       ▼       ▼       ▼       ▼       ▼
┌─────────────────────────────────────────────────────────────────┐
│         POST /information/{section}/{encrypted_code}            │
│                                                                  │
│         [Save: Section data]                                    │
│         [Update: Status flag (1=Complete, 2=Incomplete)]        │
│         [Redirect: Back with success message]                   │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│         Final Submit                                             │
│         POST /information/final-submit/{encrypted_code}          │
│                                                                  │
│         [Validate: All sections complete]                       │
│         [Update: Final submission status]                       │
│         [Trigger: Status update in Dataverse]                   │
└─────────────────────────────────────────────────────────────────┘
```

## 4. Document Submission Flow

```
┌─────────────────────────────────────────────────────────────────┐
│         Document Submission Page                                 │
│         GET  /document-submission/{encrypted_code}              │
│                                                                  │
│         [Shows: Document upload forms]                          │
│         [Lists: Existing documents]                             │
└─────┬────────────────────────────────────────────────────────────┘
      │
      ├──→ KYC Documents
      │   POST /document-submission/kyc/{encrypted_code}
      │   [Upload: To SharePoint]
      │   [Save: Metadata to database]
      │
      └──→ Onboarding Documents
          GET  /onboarding-documents/{encrypted_code}
          POST /document-submission/onboard/{encrypted_code}
          [Upload: To SharePoint]
          [Save: Metadata to database]
```

## 5. Admin Panel Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                    Admin Login                                   │
│         GET  /admin/login                                        │
│         POST /login-functionality                                │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Admin Dashboard                               │
│         GET  /dashboard (Protected by 'admin' middleware)       │
│                                                                  │
│         [Statistics]                                            │
│         [Recent Activities]                                     │
└─────┬────────────────────────────────────────────────────────────┘
      │
      ├──→ Master Data Management
      │   ├── Nationality (/list-nationality)
      │   ├── Market Type (/list-market-type)
      │   └── Investor Type Category (/list-investor-type-category)
      │
      └──→ Investor Management
          ├── List Investors (/list-investors)
          └── View Details (/view-investor-details/{id})
```

## 6. Status Tracking Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                    Status Flags                                  │
│                                                                  │
│         Table: investor_information_status                       │
│         Table: user_personal_information                         │
│                                                                  │
│         Flags (1=Complete, 2=Incomplete):                        │
│         - personal_info                                          │
│         - passport                                               │
│         - resident_status                                        │
│         - tax_information                                        │
│         - bank_details                                           │
│         - contact_details                                        │
│         - nomination                                             │
│         - risk_profile                                           │
└─────────────────────────────────────────────────────────────────┘
```

## 7. Complete Application Flow Map

```
┌─────────────────────────────────────────────────────────────────┐
│                         APPLICATION                              │
└─────┬────────────────────────────────────────────────────────────┘
      │
      ├──→ PUBLIC PAGES
      │   ├── Landing (/)
      │   ├── About Pages (/our-team, /our-expertise)
      │   ├── Solution Pages (/solution/investor/{slug})
      │   └── Contact (/contact-us)
      │
      ├──→ INVESTOR REGISTRATION
      │   ├── Self Registration (4 steps)
      │   ├── Introduced Registration (3 steps)
      │   ├── Multiple Registration (4 steps)
      │   └── PMS Registration (3 steps)
      │
      ├──→ INVESTOR JOURNEY
      │   ├── Dashboard (/home/{code})
      │   ├── Information Management (8 sections)
      │   ├── Document Submission
      │   ├── Physical Submission
      │   └── Progress Tracking
      │
      ├──→ ADMIN PANEL
      │   ├── Dashboard
      │   ├── Master Data Management
      │   └── Investor Management
      │
      └──→ API ENDPOINTS
          ├── /api/login
          └── /api/fetch-investor-details
```

## 8. Data Flow Between Pages

```
┌─────────────────────────────────────────────────────────────────┐
│                    DATA FLOW PATTERN                             │
│                                                                  │
│    User Input                                                    │
│         ↓                                                        │
│    Controller Validation                                         │
│         ↓                                                        │
│    Save to Local Database (MySQL)                                │
│         ↓                                                        │
│    Create/Update Dataverse (if applicable)                       │
│         ↓                                                        │
│    Send Notifications (Email/SMS)                                │
│         ↓                                                        │
│    Redirect to Next Page                                        │
│         ↓                                                        │
│    Display Success/Error Message                                 │
└─────────────────────────────────────────────────────────────────┘
```

## 9. Authentication Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                    AUTHENTICATION METHODS                        │
│                                                                  │
│    ┌──────────────────┐    ┌──────────────────┐                │
│    │  Azure AD B2C    │    │  Direct Access   │                │
│    │  (Primary)       │    │  (Code-based)    │                │
│    └────────┬─────────┘    └────────┬─────────┘                │
│             │                        │                           │
│             ▼                        ▼                           │
│    ┌──────────────────────────────────────┐                    │
│    │     Investor Dashboard                 │                    │
│    │     /home/{encrypted_code}             │                    │
│    └──────────────────────────────────────┘                    │
│                                                                  │
│    ┌──────────────────┐                                         │
│    │  Admin Login     │                                         │
│    │  (Session-based) │                                         │
│    └────────┬─────────┘                                         │
│             │                                                    │
│             ▼                                                    │
│    ┌──────────────────────────────────────┐                    │
│    │     Admin Dashboard                   │                    │
│    │     /dashboard                        │                    │
│    └──────────────────────────────────────┘                    │
└─────────────────────────────────────────────────────────────────┘
```

## 10. Key Navigation Patterns

### 10.1 Linear Navigation (Registration)

```
Step 0 → Step 1 → Step 2 → Step 3 → Step 4 → Thank You
  ↓        ↓        ↓        ↓        ↓
[Main]  [Contact] [OTP]   [Name]  [Complete]
```

### 10.2 Hub-and-Spoke Navigation (Dashboard)

```
              Dashboard
                 │
    ┌────────────┼────────────┐
    │            │            │
Information  Documents   Progress
    │            │            │
  [8 Tabs]    [2 Types]  [Status]
```

### 10.3 Conditional Navigation

```
Registration
    │
    ├──→ Has OCI/PAN? → Yes → Thank You (Standard)
    │                    │
    │                    No
    │                    ↓
    └──→ Thank You (No OCI/PAN)
```

---

**Use these diagrams as visual reference for understanding the complete page flow structure.**
