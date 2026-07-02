package com.example.data.model

object BankingSystemSpecs {

    val SYSTEM_ARCHITECTURE = """
[LLOYDS DIGITAL BANKING PLATFORM - MICROSERVICES SYSTEM ARCHITECTURE]

                     +---------------------------------------+
                     |  Internet / Client Layer (TLS 1.3)    |
                     |  (Android App / Swift iOS / Web Admin)  |
                     +---------------------------------------+
                                         |
                                         v
                     +---------------------------------------+
                     |       Cloudflare API Gateway          |
                     | (Rate Limiting, WAF, DDoS Mitigation) |
                     +---------------------------------------+
                                         |
                                         v
                     +---------------------------------------+
                     |      Spring Cloud Gateway Service     |
                     |   (JWT Validation & Route Routing)    |
                     +---------------------------------------+
                                         |
               +-------------------------+-------------------------+
               |                         |                         |
               v                         v                         v
   +-----------------------+ +-----------------------+ +-----------------------+
   |  Auth & KYC Service   | |   Accounts Service    | |   Payments Service    |
   | (Spring Boot + Redis) | | (Spring Boot + Cache) | | (Spring Boot + Kafka) |
   +-----------------------+ +-----------------------+ +-----------------------+
               |                         |                         |
               v                         v                         v
   +-----------------------+ +-----------------------+ +-----------------------+
   |   Cards Mgmt Service  | |  Fraud Alert Service  | |  Notify & Alert Svc   |
   | (E2EE PIN / PCI-DSS)  | |  (Rules Engine / AI)  | | (SMS/Email/APNS/FCM)  |
   +-----------------------+ +-----------------------+ +-----------------------+
               |                         |                         |
               +-------------------------+-------------------------+
                                         |
                                         v
                     +---------------------------------------+
                     |    Apache Kafka Event Streaming Bus   |
                     |   (Topics: payments, fraud, audits)   |
                     +---------------------------------------+
                                         |
               +-------------------------+-------------------------+
               |                                                   |
               v                                                   v
   +-----------------------+                           +-----------------------+
   |  Analytics & Insights |                           |   Audit Logging Svc   |
   |   (Spring Boot + DB)  |                           | (Immutable Audit Log) |
   +-----------------------+                           +-----------------------+
               |                                                   |
               +-------------------------+-------------------------+
                                         |
                                         v
                     +---------------------------------------+
                     |       Database & Persistence Layer    |
                     |   (PostgreSQL Core + Redis Cache)     |
                     +---------------------------------------+
"""

    val SPRING_BOOT_MICROSERVICES = """
// Typical Spring Boot Payments Service Controller Structure (Secure BACS/Faster Payments)

@RestController
@RequestMapping("/api/v1/payments")
@Validated
public class PaymentsController {

    private final PaymentsService paymentsService;
    private final FraudClient fraudClient;

    @Autowired
    public PaymentsController(PaymentsService paymentsService, FraudClient fraudClient) {
        this.paymentsService = paymentsService;
        this.fraudClient = fraudClient;
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAuthority('SCOPE_payment:write')")
    public ResponseEntity<PaymentResponse> initiateTransfer(
            @Valid @RequestBody PaymentRequest request,
            @RequestHeader("X-Device-Fingerprint") String deviceFingerprint) {
            
        // 1. Run real-time fraud scoring interceptor
        FraudScoreResponse fraudResult = fraudClient.scoreTransaction(request, deviceFingerprint);
        
        if (fraudResult.getRiskScore() >= 80.0) {
            // Trigger immediate account freeze
            paymentsService.freezeAccount(request.getSourceAccountId(), "AUTOMATED_AI_FRAUD_BLOCK");
            throw new SuspiciousActivityException("Payment flagged by Lloyds Sentinel engine. Account frozen.");
        }

        // 2. Process Faster Payments / Event Stream
        PaymentResponse response = paymentsService.processUKFasterPayment(request);
        return ResponseEntity.ok(response);
    }
}
"""

    val POSTGRESQL_SCHEMA = """
-- Core Tables Schema with Financial Grade Auditing Constraints

CREATE TABLE users (
    user_id VARCHAR(50) PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    pin_hash VARCHAR(256) NOT NULL,
    mfa_secret VARCHAR(128),
    is_biometric_enabled BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE accounts (
    account_id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) REFERENCES users(user_id),
    account_name VARCHAR(100) NOT NULL,
    account_type VARCHAR(20) NOT NULL, -- CURRENT, SAVINGS, JOINT, BUSINESS
    balance NUMERIC(15, 4) NOT NULL,
    available_funds NUMERIC(15, 4) NOT NULL,
    account_number CHAR(8) UNIQUE NOT NULL,
    sort_code CHAR(8) NOT NULL,
    interest_rate NUMERIC(5, 2) DEFAULT 0.00,
    is_frozen BOOLEAN DEFAULT FALSE,
    currency CHAR(3) DEFAULT 'GBP'
);

CREATE TABLE transactions (
    transaction_id BIGSERIAL PRIMARY KEY,
    account_id VARCHAR(50) REFERENCES accounts(account_id),
    title VARCHAR(100) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    amount NUMERIC(15, 4) NOT NULL,
    direction VARCHAR(6) CHECK (direction IN ('DEBIT', 'CREDIT')),
    status VARCHAR(20) DEFAULT 'COMPLETED', -- COMPLETED, PENDING, FAILED
    risk_score NUMERIC(5, 2) DEFAULT 0.00,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_transactions_account_id ON transactions(account_id);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);
"""

    val FRAUD_MODULE_DESIGN = """
[LLOYDS SENTINEL AI FRAUD DETECTION ENGINE]

1. Real-time Feature Aggregation
   - Windowing on transaction velocity: (tx count in last 1 min / 10 mins / 1 hour)
   - Geospatial checks: distance from last transacted merchant compared to current transaction location
   - Device fingerprinting: match browser/os/sim strings against trusted list

2. Rules Engine (MVEL / Drools implementation)
   - Rule 1 (Scam Detection): If recipient name contains 'crypto' OR 'support' AND transfer amount > £2,000, add 40 points to risk.
   - Rule 2 (Mule Account): If target account opened < 48 hours ago AND incoming Faster Payment > £5,000, add 50 points.
   - Rule 3 (Speed Anomaly): If Tx2 is made < 5 mins after Tx1 AND distance > 50 miles, add 60 points.

3. Automated Mitigation Matrix
   - Score < 40: Low Risk. Allow instantly.
   - Score 40 to 79: Medium Risk. Step-up biometrics verification requested.
   - Score >= 80: Critical Risk. Automated card and online account freeze. Trigger instant alert to customer and push to Bank Operations Dashboard.
"""

    val WEB_ADMIN_DASHBOARD = """
[LLOYDS INTERNAL BANK OPERATIONS - CAPABILITIES]

1. Customer Servicing View
   - View customer contact info, active balances, and recent transaction audits.
   - Manage joint-account links and BACS interest overrides.

2. Sentinel AI Threat Radar
   - Real-time map displaying high-risk payment attempts across the UK.
   - Review automatically frozen accounts and analyze suspicious audit trails.

3. Dispute & Chargeback Workflow
   - Direct interface to file MasterCard Chargeback requests.
   - Direct integration with SWIFT-style messaging for transaction recalls.

4. Operational Telemetry
   - Microservices health monitoring, Kafka lag tracking, and database CPU states.
"""

    val API_DOCUMENTATION = """
[LLOYDS DIGITAL OPEN BANKING REST API v1.4]

1. POST /api/v1/auth/login
   - Body: { "email": "string", "pinHash": "string" }
   - Returns: JWT Token, Biometric challenge token.

2. GET /api/v1/accounts
   - Headers: Authorization: Bearer <JWT>
   - Returns: List of active retail, savings, and joint accounts.

3. POST /api/v1/payments/faster-payment
   - Headers: Authorization: Bearer <JWT>, X-Device-Fingerprint: <UUID>
   - Body: { "sourceAccountId": "string", "targetAccountNumber": "string", "targetSortCode": "string", "amount": 100.00, "reference": "Rent" }
   - Returns: Transfer status, Faster Payment ID, Risk Evaluation payload.

4. POST /api/v1/cards/{cardId}/freeze
   - Body: { "isFrozen": true }
   - Returns: Active status of physical and virtual card chips.
"""

    val SECURITY_MODEL = """
[ENTERPRISE FINANCIAL-GRADE SECURITY PROTOCOLS]

1. End-to-End Encryption (E2EE)
   - Customer PINs and login metrics are encrypted using local Hardware Security Module (HSM) keys (AES-GCM-256) before leaving the mobile device.

2. Network Resiliency (TLS 1.3 + Pinning)
   - The Android app enforces TLS 1.3 with strict Certificate Pinning against root CA chains. Prevents MITM attacks on open public networks.

3. PCI-DSS Compliance
   - Full segregation of Cardholder Data Environment (CDE). Complete card PANs are never stored in plain SQLite; they are tokenized and processed exclusively inside a secure PCI enclave.

4. Event Audit Log
   - Immutable append-only audit tracking using database triggers and event-sourcing ledgers.
"""

    val SAMPLE_DATASET = """
-- Initial seed data for database testing

INSERT INTO users (user_id, full_name, email, pin_hash) VALUES
('lloyds_user_001', 'Dr. Thomas Harrison', 'thomas.harrison@lloyds-customer.co.uk', '${"$"}$2a${"$"}$12${"$"}${"$"}u78R94k72H...');

INSERT INTO accounts (account_id, user_id, account_name, account_type, balance, available_funds, account_number, sort_code) VALUES
('acc_current', 'lloyds_user_001', 'Club Lloyds Current Account', 'CURRENT', 5432.89, 5382.89, '77341298', '30-90-15'),
('acc_savings', 'lloyds_user_001', 'Lloyds Easy Saver (0.85% AER)', 'SAVINGS', 12450.50, 12450.50, '88231149', '30-90-15');

INSERT INTO transactions (account_id, title, description, category, amount, direction) VALUES
('acc_current', 'Salary Deposit', 'LLOYDS GROUP PAYROLL', 'Salary', 3250.00, 'CREDIT'),
('acc_current', 'Sainsbury\\'s Supermarket', 'SAINSBURYS SML LONDON UK', 'Groceries', 64.20, 'DEBIT');
"""

    val DEPLOYMENT_ARCHITECTURE = """
[CLOUD DEPLOYMENT RUNBOOK (GOOGLE CLOUD PLATFORM)]

1. Application Routing & WAF
   - Google Cloud Armor rules deployed for SQL injection, cross-site scripting, and rate-limiting.
   - Envoy-based Api Gateway load balances incoming secure traffic.

2. Kubernetes Compute Cluster (GKE)
   - Microservices are packaged as secure, minimal scratch-base Docker containers.
   - Deployed on GKE Autopilot with auto-scaling (HPA) enabled for rapid spikes.

3. Financial Database System (CockroachDB / Spanner)
   - PostgreSQL dialect interface, globally distributed, offering strict Serializability (Isolation level) for preventing double-spending anomalies.

4. Caching & Message Queue
   - Redis Enterprise cluster handles real-time JWT validations and rate counts.
   - Confluent Cloud managed Kafka pipelines secure transaction ledger feeds.
"""
}
