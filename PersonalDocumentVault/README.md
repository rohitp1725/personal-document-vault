# Personal Document Vault
### OOP Mini Project | SY-IT-B | 2025-26 | Java (ITPCC407)

---

## Project Overview
A Java console application to securely store, manage, and share important personal
documents. Supports multiple users, family vault networks, emergency access, expiry
alerts, document encryption, smart suggestions, and much more.

---

## Features
1. Multi-user login with password hashing and vault locking
2. Document Scanner Simulation (auto-fill templates)
3. Caesar Cipher + Base64 Encryption for document data
4. Smart Suggestion Engine (missing docs, expiry warnings)
5. Auto Backup on logout
6. Dashboard Summary on login
7. Document Version History
8. Emergency Mode (one command — blood group, insurance, contacts)
9. Reminder Scheduler
10. Role-Based Access (Adult / Minor / Senior)
11. Advanced Search & Sort (expiry, category, recently added)
12. Family Vault Network (link family members)
13. Guardian Emergency Access to family member vaults
14. Full Audit Log
15. Export Summary Report to .txt file
16. Vault Recovery via security question

---

## OOP Concepts Used

| Concept             | Where                                                          |
|---------------------|----------------------------------------------------------------|
| Abstract Classes    | Document (abstract), User (abstract)                          |
| Inheritance         | IdentityDoc, MedicalDoc, FinancialDoc, EducationDoc, PropertyDoc |
|                     | AdultUser, MinorUser, SeniorUser                              |
| Interfaces          | Expirable, Searchable, Auditable, Shareable                   |
| Exception Handling  | VaultLockedException, AuthenticationFailedException,          |
|                     | DocumentExpiredException, UnauthorizedAccessException,        |
|                     | DuplicateDocumentException                                    |
| Generics            | Used in Vault (Searchable with List<Document>)                |
| Collections         | ArrayList, HashMap, LinkedHashMap, stream().filter/sort       |
| File Handling       | Per-vault files, audit log, backup, history, reminders        |
| Comparator/Sorting  | Sort by expiry, category, recently added                      |
| Encryption          | Caesar Cipher + Base64 (EncryptionUtil)                       |

---

## Project Structure
```
PersonalDocumentVault/
├── src/
│   ├── Main.java                        ← Entry point + all menus
│   ├── models/
│   │   ├── Document.java                ← Abstract base class
│   │   ├── IdentityDocument.java
│   │   ├── MedicalDocument.java
│   │   ├── FinancialDocument.java
│   │   ├── EducationDocument.java
│   │   ├── PropertyDocument.java
│   │   ├── User.java                    ← Abstract base class
│   │   ├── AdultUser.java
│   │   ├── MinorUser.java
│   │   ├── SeniorUser.java
│   │   ├── VersionedDocument.java
│   │   └── Reminder.java
│   ├── interfaces/
│   │   ├── Expirable.java
│   │   ├── Searchable.java
│   │   ├── Auditable.java
│   │   └── Shareable.java
│   ├── exceptions/
│   │   ├── VaultLockedException.java
│   │   ├── AuthenticationFailedException.java
│   │   ├── DocumentExpiredException.java
│   │   ├── UnauthorizedAccessException.java
│   │   └── DuplicateDocumentException.java
│   ├── engine/
│   │   ├── AlertEngine.java             ← Expiry alerts on login
│   │   ├── AuditLogger.java             ← Singleton audit logger
│   │   └── SuggestionEngine.java        ← Smart suggestions
│   ├── utils/
│   │   ├── UIUtil.java                  ← Console colors & formatting
│   │   ├── EncryptionUtil.java          ← Caesar Cipher + Base64
│   │   ├── DocumentScanner.java         ← Scan simulation templates
│   │   └── FileHandler.java             ← All file read/write ops
│   └── vault/
│       ├── Vault.java                   ← Core vault per user
│       ├── FamilyNetwork.java           ← Family links & emergency access
│       └── VaultManager.java            ← Login, signup, session
├── run.sh                               ← Mac/Linux build & run
├── run.bat                              ← Windows build & run
└── README.md
```

---

## How to Run

### Option 1 — Mac / Linux
```bash
chmod +x run.sh
./run.sh
```

### Option 2 — Windows
Double-click `run.bat`

### Option 3 — Manual (any OS)
```bash
mkdir out
javac -d out -sourcepath src src/Main.java src/models/*.java src/interfaces/*.java src/exceptions/*.java src/engine/*.java src/utils/*.java src/vault/*.java
cd out
java Main
```

### Requirements
- Java JDK 8 or above
- Any terminal / command prompt

---

## Data Files (auto-created in `out/data/`)
| File | Contents |
|------|----------|
| `users.txt` | All user accounts |
| `vault_username.txt` | Documents for each user |
| `family_network.txt` | Family links |
| `audit_log.txt` | All actions logged |
| `history/history_username.txt` | Version history |
| `backups/vault_backup_*.txt` | Auto backups on logout |
| `reminders/reminders_username.txt` | User reminders |
| `report_username_date.txt` | Exported reports |

---

## Subject Mapping (ITPCC407)
| CO   | Description | Covered By |
|------|-------------|------------|
| 407.2 | OOP Concepts | Document hierarchy, User hierarchy |
| 407.3 | Exception Handling | 5 custom exceptions throughout |
| 407.4 | Generics | List<Document>, Comparator<Document> |
| 407.5 | File Handling | FileHandler — all persistent storage |
| 407.6 | Mini Project | Complete working application |
