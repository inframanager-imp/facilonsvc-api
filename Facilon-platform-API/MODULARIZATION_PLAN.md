# Facilon Platform — Modularization Plan

> **Organizing principle:** every tile in the dashboard's **Available Applications**
> list is its own **module**. One dashboard application = one module.
> New products (e.g. Facilon Appointment) plug in as new sibling modules — and are
> later extractable into **microservices** with minimal surgery.
>
> Status: **Plan / analysis only. No application code changed yet.**
> Stack: Spring Boot 3.4.3 (Spring Modulith 1.3.x compatible).

---

## 1. The principle: dashboard application = module

The investor dashboard (`/investor/dashboard?tab=applications`) shows a list of
**Available Applications**. Each of those tiles is a product the investor uses (and,
in future, *subscribes to*). **Each tile must be its own module.**

Today these tiles are produced by a single hardcoded method —
`InvestorProgressService.buildApplications()` — and all the code behind them is lumped
together in `module/client`. That lump is what we split, **one module per tile**.

### Current dashboard tiles → target modules

| Tile `code` | Tile name | Route | Today | Target module |
|---|---|---|---|---|
| `status` | **Facilon Status** | `/investor/progress` | always enabled | `application/status/` |
| `onboard` | **Facilon Onboard** | `/investor/profile` | gated by SP product assignment | `application/onboard/` |
| `dsr` | **Data Subject Rights** | `/investor/dsr-center` | always enabled | `application/dsr/` |
| `report` | **Facilon Report** | — | "Coming Soon" (disabled) | `application/report/` (shell) |
| `appointment` *(future)* | **Facilon Appointment** | — | does not exist yet | `application/appointment/` |

> Note: the **office-visit appointment** feature that exists today
> (`AppointmentService`, `VerificationAppointment`) is **part of Facilon Status**, not
> the future standalone "Facilon Appointment" product. It moves into `application/status/`.

We build a **modular monolith** (one deployable) first — module boundaries become the
microservice boundaries later, so the eventual split is mechanical, not a rewrite.

---

## 2. Current state (measured from the codebase)

- **Organized by *actor*, not by *application*:**
  `com.facilon.app.module.{client, serviceagent, serviceprovider, admin}`
  plus a root layer (`config`, `security`, `integration`, `model`, `service`, `repository`, `util`, `exception`).
- **`module/client` is the lump:** 43 services mixing all dashboard applications
  (Status / Onboard / DSR / Report) together.
- **Applications are NOT modules yet.** The tiles are a hardcoded list in
  `InvestorProgressService.buildApplications()` — four literal `apps.add(...)` calls.
  Adding a product means editing that method and recompiling.
- **80 `@Entity` tables** across the modules.
- **Coupling that must be cut:**
  - Other actors import `module/client` internals **16 times**
    (mostly `Investor`, `InvestorRepository`, `client.service.*`, `client.dto.*`).
  - `client` imports `serviceagent` **4 times** (dashboard delegation lookup).
  - Inside `client`, ~14 services reach across would-be application boundaries.

---

## 3. Target architecture

```
com.facilon.app
│
├── platform/                  ← SHARED KERNEL (the only thing applications may depend on)
│   ├── config/  security/  exception/  util/
│   ├── integration/           (dynamics, sharepoint, graphemail, sms, ocr, laravel, b2b)
│   ├── tenant/                (Tenant, TenantEntity, multi-tenancy, *Config)
│   ├── identity/              (Investor, AuthorizedUser, InvestorRepository, consents, roles)
│   ├── masterdata/            (all master_* tables + repos)
│   └── application/           (registry SEAM: ApplicationProvider, Registry, Context)
│
├── application/               ← ONE MODULE PER DASHBOARD TILE (self-contained)
│   ├── status/                Facilon Status  (progress, dashboard, account details, office-visit appointment)
│   │   ├── controller/ service/ model/ repository/ dto/
│   │   └── StatusApplicationProvider     (registers its dashboard tile)
│   ├── onboard/               Facilon Onboard (KYC, documents, profile, verification, registration, PMS)
│   │   └── OnboardApplicationProvider
│   ├── dsr/                   Data Subject Rights
│   │   └── DsrApplicationProvider
│   ├── report/                Facilon Report  (coming soon — shell)
│   │   └── ReportApplicationProvider
│   └── appointment/           ← FUTURE product: add this folder + provider, nothing else changes
│       └── AppointmentApplicationProvider
│
└── actor/                     ← non-application actors (serve the apps, cut across them)
    ├── serviceagent/
    ├── serviceprovider/
    └── admin/
```

### Rules that make it real (not just folders)
1. **One-way dependencies:** `application/*` → `platform`. Never `platform → application`,
   and **never `application/onboard → application/status`** (apps are siblings).
2. **Each app owns its data.** Its tables/entities live in its package; apps don't query
   each other's tables.
3. **Cross-app needs go through the platform:** a published interface in `platform`, or a
   **domain event** (e.g. Onboard publishes `KycCompletedEvent`; Status listens).
4. **Each app self-registers its dashboard tile** by contributing one
   `ApplicationProvider` bean. Adding a tile = new module + new provider. No edits elsewhere.

---

## 4. Change sets

### Change Set 1 — Create the `platform/` shared kernel
Move shared, app-agnostic code out of the root and out of `module/client`:

| Move into `platform/` | From |
|---|---|
| `tenant/` — `Tenant`, `TenantEntity`, `MultiTenant*`, `TenantContext*`, `Tenant*Config` | root `config/`, `model/` |
| `security/` — `Jwt*`, `SecurityConfig`, `UserPrincipal`, `CustomUserDetailsService` | root `security/` |
| `identity/` — `AuthorizedUser`, `Investor`, `InvestorRepository`, `InvestorConsents`, roles/usergroups | root `model/`, `repository/`, `module/client/model` |
| `masterdata/` — all `Master*` entities + repos | `module/client/model/master`, `repository` |
| `integration/` — dynamics, sharepoint, graphemail, sms, ocr, laravel, b2b | root `integration/` |
| `email/`, `util/`, `exception/`, `config/` | root |

**Why first:** every application depends on these. `Investor` + `InvestorRepository` are
imported by every actor — they must be in the shared kernel, not inside one application.

### Change Set 2 — Split `module/client` into one module per dashboard tile
New `com.facilon.app.application.*`, each with `controller/ service/ model/ repository/ dto/`:

| New module | Tile | Code that moves in (from `module/client`) |
|---|---|---|
| `application/status/` | Facilon Status | `InvestorProgressService`, `InvestorDashboardDto`, `InvestorProgressDto`, account-details, **+ office-visit appointment** (`AppointmentService/Controller`, `VerificationAppointment*`) |
| `application/onboard/` | Facilon Onboard | KYC* (`ClientKycService`, `KycSmartUpload`, `KycValidation`, `KycRequirement`, `KycPdf`…), documents, profile, verification, physical submission, bank/personal/passport/tax/nomination/risk, registration, introduced*, nextholder, PMS, SOW, consent |
| `application/dsr/` | Data Subject Rights | `DsrService`, `ClientDsrController`, `DsrCase`, `DsrCaseRepository`, 2 DTOs — **cleanest carve-out** |
| `application/report/` | Facilon Report | new empty shell (currently "coming soon") |
| `actor/serviceagent/`, `actor/serviceprovider/`, `actor/admin/` | — | rename `module/*` → `actor/*` |

### Change Set 3 — Break the coupling (the hard, essential part)
Specific tangles the scan found that must be resolved so modules are independent:

1. **Status → Onboard data coupling (biggest).** `InvestorProgressService` reads
   onboarding-owned data directly: `KycDocumentsRepository`,
   `InvestorPhysicalSubmissionRepository`, `InvestorBankDetailsRepository`,
   `UserPersonalInformationRepository`.
   → **Fix:** Onboard exposes a `platform` interface (e.g. `OnboardingSummaryService`)
   that Status calls. Status must not touch Onboard repositories.
2. **Status → ServiceAgent coupling.** Dashboard delegation lookup
   (`delegationRepository`, `serviceAgentRepository`) belongs to the serviceagent actor.
   → **Fix:** expose `DelegationQueryService` from `platform` / the serviceagent actor's API.
3. **Actors → client internals (16 imports).** `ServiceAgentInvestorController` imports
   `client.service.*` and `client.dto.*` wholesale.
   → **Fix:** get `Investor`/identity from `platform`; per-app data via that app's
   published interface — not `client.service.*`.
4. **Intra-client cross-app refs (~14 services).** Resolve per app as each tile is carved
   out; most collapse once shared bits move to `platform`.

### Change Set 4 — Dashboard application registry (the pluggable tile seam)
Replace the hardcoded `buildApplications()` with a registry so each module supplies its own tile:

- `InvestorApplicationProvider` interface — `describe(context)` returns an `ApplicationItem`,
  plus `order()`. Returning `null` hides the tile (future: "not subscribed").
- `InvestorApplicationContext` — signals apps use to decide enabled/blocked
  (today: `uniqueCode`, `onboardingEnabled`; extend later).
- `InvestorApplicationRegistry` — `@Service` injecting `List<InvestorApplicationProvider>`,
  sorts by `order()`, maps to items, skips nulls.
- One `@Component` per module: `StatusApplicationProvider`, `OnboardApplicationProvider`,
  `DsrApplicationProvider`, `ReportApplicationProvider`.
- `buildApplications()` becomes one line: `return registry.buildApplications(context)`.

**Result:** adding **Facilon Appointment** later = one new module + one provider bean.
No edits to the dashboard service or to existing applications.

### Change Set 5 — Build & enforcement (keeps it modular)
1. Add **Spring Modulith** (`spring-modulith-starter-core` + `-test`, BOM 1.3.x) to `pom.xml`.
2. Add a `package-info.java` per module declaring it an `@ApplicationModule` with allowed deps.
3. Add one verification test (`ApplicationModules.of(...).verify()`) that **fails the build**
   if any application imports another application's internals. This prevents re-tangling.
4. (Optional) Replace direct cross-app calls with Spring Modulith **domain events**.

### Change Set 6 — Database (low effort now, critical for microservices later)
- No table changes required now.
- **Stop new cross-app table sharing**: Status reading `kyc_documents` etc. should go
  through Onboard's API, not a shared query.
- Logically tag the 80 tables by owning module (naming/docs) so a future microservice
  split has clean schema boundaries.

---

## 5. Recommended order (incremental, each step shippable)

1. **Spring Modulith + platform kernel extraction** (identity, tenant, masterdata, security).
   Foundation; nothing else works cleanly without it.
2. **Carve out DSR** — smallest, cleanest tile; proves the pattern with low risk.
   (Only depends on `Investor`, `InvestorRepository`, `EmailService` — all going to platform.)
3. **Carve out Status** (incl. office-visit appointment) — requires fixing couplings #1 and #2 via platform interfaces.
4. **Carve out Onboard** — largest; do last, mostly "what remains."
5. **Rename `module/*` → `actor/*`** and fix the 16 cross-actor imports.
6. **Add the dashboard registry seam** (Change Set 4) so each module self-registers its tile.

**Risk note:** Steps 1 and 3 are the heavy ones (Status↔Onboard data coupling).
DSR (step 2) is genuinely easy — a good first commit to validate the approach.

---

## 6. Path to microservices (later)

Module boundaries become service boundaries. Nothing here gets thrown away.

| Modular monolith (now) | Microservices (later) |
|---|---|
| `application/status/` | `facilon-status-service` |
| `application/onboard/` | `facilon-onboard-service` |
| `application/dsr/` | `facilon-dsr-service` |
| `application/appointment/` *(future)* | `facilon-appointment-service` |
| `platform/` shared kernel | shared library + platform APIs (identity, tenant, masterdata) |
| In-process Spring Modulith events | message broker (Kafka / Azure Service Bus) — *same event classes* |
| `ApplicationProvider` beans | each service exposes its tile via API; gateway/dashboard aggregates |

### Three things to get right NOW so the split stays cheap
1. **No shared tables across apps.** Only `platform` tables (identity, master data, tenant)
   are shared; those become the platform service.
2. **Cross-app communication only via events or platform interfaces** — never direct
   repository/service calls into another app. (Spring Modulith enforces this for free.)
3. **Stable, serializable event contracts** — treat domain events as published APIs from day one.

### Not yet
- Don't split databases or add a broker now (premature).
- Don't add service discovery, gateways, or per-service auth yet — that's transition-time work.

---

## 7. Subscription model fit

This architecture is the base for the subscription model:

- **Toggle per app:** an entitlement check decides whether an app's `ApplicationProvider`
  returns its tile (or its controller 403s). One place per app.
- **Meter per app:** usage/billing hooks attach at module boundaries.
- **Extract per app:** if one product must scale or bill independently, its module lifts
  out cleanly — because the boundaries already exist.

When true subscriptions are needed, the same registry seam absorbs it: a provider returns
`null` when the investor isn't subscribed, backed by `master_applications` +
`investor_app_subscriptions` tables. The interface does not change.
