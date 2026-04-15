# Quick Test Guide

## Summary of Created Test Files

### Test Statistics
- **Total Test Files**: 15
- **Total Test Methods**: ~120+
- **Test Coverage**: Auth, Book, Transaction, User, Shared, and Integration tests

---

## Test Files Location

```
src/test/java/com/library/
├── auth/
│   ├── api/
│   │   └── AuthControllerTest.java
│   └── internal/
│       ├── JwtUtilTest.java
│       ├── AuthServiceTest.java
│       ├── CustomUserDetailsServiceTest.java
│       └── UserPrincipalTest.java
├── book/
│   ├── api/
│   │   └── BookControllerTest.java
│   └── internal/
│       ├── BookServiceTest.java
│       └── BookEventListenerTest.java
├── transaction/
│   ├── api/
│   │   └── TransactionControllerTest.java
│   └── internal/
│       └── TransactionServiceTest.java
├── user/
│   ├── api/
│   │   └── UserControllerTest.java
│   └── internal/
│       └── UserServiceTest.java
├── shared/
│   ├── exception/
│   │   └── GlobalExceptionHandlerTest.java
│   └── ...
├── notification/
│   └── internal/
│       └── NotificationListenerTest.java
└── LibraryApplicationIntegrationTest.java
```

---

## Quick Commands

### Run All Tests
```bash
mvn test
```

### Run Tests by Module

**Auth Module:**
```bash
mvn test -Dtest=Auth*
mvn test -Dtest=JwtUtilTest,AuthServiceTest,AuthControllerTest
```

**Book Module:**
```bash
mvn test -Dtest=Book*
```

**Transaction Module:**
```bash
mvn test -Dtest=Transaction*
```

**User Module:**
```bash
mvn test -Dtest=User*
```

**Integration Tests Only:**
```bash
mvn test -Dtest=LibraryApplicationIntegrationTest
```

### Run Specific Test
```bash
mvn test -Dtest=BookServiceTest#testCreateBook
```

### Run with Output
```bash
mvn test -X
```

### Run with Coverage
```bash
mvn clean test jacoco:report
# Report available at: target/site/jacoco/index.html
```

---

## Test Breakdown by Category

### Unit Tests (Mocking Dependencies)
- JwtUtilTest
- AuthServiceTest
- BookServiceTest
- TransactionServiceTest
- UserServiceTest
- GlobalExceptionHandlerTest

### Controller Tests (MockMvc)
- AuthControllerTest
- BookControllerTest
- TransactionControllerTest
- UserControllerTest

### Service Tests
- CustomUserDetailsServiceTest
- UserPrincipalTest
- BookEventListenerTest
- NotificationListenerTest

### Integration Tests (Full Application)
- LibraryApplicationIntegrationTest

---

## Key Test Scenarios Covered

### Authentication & Security
✅ JWT token generation and validation  
✅ User authentication flow  
✅ Role-based access control (ADMIN vs USER)  
✅ Authorization on protected endpoints  
✅ Password encoding verification  

### Book Management
✅ Book creation (admin only)  
✅ Book updates (admin only)  
✅ Book deletion (admin only)  
✅ Book search functionality  
✅ Availability tracking  

### Book Transactions
✅ Borrow book (with max 3 limit)  
✅ Return book  
✅ Transaction history  
✅ Event-driven availability updates  

### User Management
✅ User profile retrieval  
✅ User listing (admin only)  
✅ Current user endpoint  
✅ Permission-based access  

### Error Handling
✅ Book not found errors  
✅ User not found errors  
✅ Max borrow limit exceeded  
✅ Unauthorized access  
✅ Forbidden access  

---

## Example Test Execution

```bash
# Install dependencies (if needed)
mvn clean install

# Run all tests
mvn test

# Expected output:
# Tests run: 120+, Failures: 0, Errors: 0, Skipped: 0

# Generate coverage report
mvn jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

---

## Assertions Used

The tests use various JUnit 5 assertions:

- `assertEquals()` - Verify expected value equals actual
- `assertTrue()` / `assertFalse()` - Verify boolean conditions
- `assertNotNull()` / `assertNull()` - Verify null state
- `assertThrows()` - Verify exception thrown
- `assertDoesNotThrow()` - Verify no exception thrown

And Hamcrest matchers:

- `hasSize()` - Verify collection size
- `hasItem()` - Verify item in collection
- `containsString()` - Verify string contains substring
- `greaterThan()` / `lessThan()` - Numeric comparisons

---

## Mocking Patterns

### Using @Mock
```java
@Mock
private BookRepository repository;
```

### Using @WithMockUser
```java
@WithMockUser(roles = "ADMIN")
void testAdminEndpoint()
```

### Using MockMvc
```java
mockMvc.perform(get("/api/books"))
    .andExpect(status().isOk())
```

### Using ArgumentCaptor
```java
ArgumentCaptor<BookBorrowedEvent> captor = ArgumentCaptor.forClass(BookBorrowedEvent.class);
verify(publisher).publishEvent(captor.capture());
```

---

## Debugging Tests

### Run with Debug Output
```bash
mvn test -Dorg.slf4j.simpleLogger.debug=true
```

### Run Single Test
```bash
mvn test -Dtest=BookServiceTest#testCreateBook -X
```

### View Test Log
```bash
cat target/surefire-reports/TEST-*.xml
```

---

## CI/CD Integration

### GitHub Actions Example
```yaml
name: Tests
on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: 21
      - run: mvn clean test
      - run: mvn jacoco:report
```

---

## Notes

- All tests are isolated and can run in any order
- Integration tests use H2 in-memory database
- Mock-based tests are fast (<1s typically)
- Integration tests are slower but test real flow
- Security tests use Spring Security Test utilities

---

## Support

For questions about specific tests, refer to `TEST_DOCUMENTATION.md` for detailed information about each test class and method.

