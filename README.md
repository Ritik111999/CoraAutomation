# Cora Automation

End-to-end UI automation framework for the **Cora PWA** web application. Built with Selenium WebDriver, TestNG, and Maven, with parallel Chrome execution and PDF/HTML test reporting.

**Target environment:** [https://app.cora.betaplanets.com](https://app.cora.betaplanets.com)

---

## Features

- **Page Object Model** — maintainable locators and reusable page classes
- **Parallel execution** — up to 10 concurrent Chrome browsers (local); 3 in CI
- **Headless by default** — matches CI/CD pipeline behavior
- **Data-driven tests** — TestNG `@DataProvider` for login, countries, listing statuses, and more
- **Rich reporting** — Extent HTML dashboard, module-wise PDF, and scenario PDF with screenshots
- **GitHub Actions CI** — automated runs on push/PR to `main`

---

## Tech Stack

| Tool | Version |
|------|---------|
| Java | 21 |
| Maven | 3.x |
| Selenium WebDriver | 4.27.0 |
| TestNG | 7.10.2 |
| Extent Reports | 5.1.2 |
| Browser | Google Chrome |

---

## Prerequisites

- **Java 21** (JDK)
- **Maven 3.8+**
- **Google Chrome** (latest stable)
- Valid Cora test account credentials

---

## Project Structure

```
CoraAutomation/
├── .github/workflows/          # GitHub Actions CI pipeline
├── src/main/java/com/cora/
│   ├── base/                   # BaseTest (ThreadLocal WebDriver)
│   ├── config/                 # ConfigReader
│   ├── listeners/              # TestListener (reporting hooks)
│   ├── pages/                  # Page Object classes
│   ├── reporting/              # PDF/HTML report generators
│   └── utils/                  # BrowserFactory, WebElementUtils, etc.
├── src/main/resources/
│   └── config.properties       # Environment, test data, reporting config
├── src/test/java/com/cora/tests/
│   └── *Test.java              # TestNG test classes
└── src/test/resources/
    ├── testng.xml              # Full suite (local)
    ├── testng-ci.xml           # CI suite (3 parallel browsers)
    └── testng-profile-contact-faq-parallel.xml
```

---

## Test Coverage

| Module | Scenarios | Notes |
|--------|-----------|-------|
| Login | 5 | Valid login + 4 negative rows |
| Home | 12 | Follow-ups, Live Feed, Appointments |
| Home Property Search | 5 | Sequential |
| Properties | 6 | Wizard steps 1–6, validation |
| Profile | 5 | Parallel-safe with account lock |
| Contact Us | 6 | Fully parallel |
| FAQ | 7 | Fully parallel |
| Draft Lifecycle | 8 | Create → edit → delete + 5 countries |
| Published Lifecycle | 8 | Create → edit → delete + 4 statuses + negative |
| Negative (countries/status) | 5 | Empty fields, country reset, invalid status |

**Out of scope:** Voice assistant, microphone, and AI chat features are not tested.

---

## Quick Start

### 1. Clone the repository

```bash
git clone https://github.com/Ritik111999/CoraAutomation.git
cd CoraAutomation
```

### 2. Configure credentials

Edit `src/main/resources/config.properties`:

```properties
base.url=https://app.cora.betaplanets.com
cora.login.valid.username=your-email@example.com
cora.login.valid.password=YourPassword
```

### 3. Run tests

```bash
# Full suite (headless, ~12 min)
mvn test

# Single test class
mvn test -Dtest=LoginTest

# Single test method
mvn test -Dtest=LoginTest#testPositiveLogin_validCredentials_redirectsToHome
```

### 4. View reports

```bash
open test-output/reports/ExtentReport_LATEST.html
```

PDF reports are generated in `test-output/reports/` after each run.

---

## Maven Profiles

| Profile | Command | Description |
|---------|---------|-------------|
| *(default)* | `mvn test` | Full suite, 10 parallel browsers |
| `ci` | `mvn test -Pci` | CI suite, 3 parallel browsers |
| `profile-contact-faq` | `mvn test -Pprofile-contact-faq` | Profile + Contact Us + FAQ only |

### Headless mode

Headless is **on by default** (`chrome.headless=true` in config).

To run with a visible browser (debugging):

```bash
mvn test -Dchrome.headless=false
```

---

## Configuration

All settings live in `src/main/resources/config.properties`:

| Key | Description |
|-----|-------------|
| `base.url` | Application URL |
| `chrome.headless` | Run Chrome without UI (default: `true`) |
| `explicit.wait.seconds` | WebDriverWait timeout |
| `cora.login.valid.username` | Test account email |
| `cora.login.valid.password` | Test account password |
| `screenshot.on.pass` / `screenshot.on.fail` | Capture screenshots |

System properties override config at runtime:

```bash
mvn test -Dbase.url=https://staging.example.com -Dchrome.headless=false
```

---

## CI/CD (GitHub Actions)

Workflow file: `.github/workflows/cora-automation.yml`

**Triggers:** push to `main`, pull requests, manual dispatch

**Required GitHub Secrets:**

| Secret | Description |
|--------|-------------|
| `CORA_USERNAME` | Test account email |
| `CORA_PASSWORD` | Test account password |
| `CORA_BASE_URL` | *(optional)* Override base URL |

Reports are uploaded as workflow artifacts after each run.

---

## Parallel Execution

| Environment | Max browsers | Suite file |
|-------------|--------------|------------|
| Local | 10 | `testng.xml` |
| CI | 3 | `testng-ci.xml` |

Each test thread gets its own Chrome instance via `ThreadLocal<WebDriver>` in `BaseTest`. Lifecycle tests (draft/published) run sequentially within their group; DataProvider rows inside them can still run in parallel.

---

## Reports

After each run:

| Artifact | Location |
|----------|----------|
| HTML dashboard | `test-output/reports/ExtentReport_LATEST.html` |
| PDF scenario report | `test-output/reports/*.pdf` |
| Screenshots | `test-output/screenshots/` |
| Surefire XML | `target/surefire-reports/` |

---

## License

Private / internal use — Cora PWA QA automation.
