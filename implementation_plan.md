# Implementation Plan — Module 2: Book Returning (Xử lý trả sách)

> Module 1 (Book Receiving) is **complete**. This plan covers the remaining work for Module 2.

---

## Current Status

### ✅ Already Done

| Layer | File | Status |
|---|---|---|
| **View** | `LoginFrm.java` | ✅ Complete |
| **View** | `LibrarianHomeFrm.java` | ✅ Complete |
| **View** | `SearchBorrowingFrm.java` | ✅ Complete (both modes, inline detail panel for RETURN_BOOK mode built) |
| **View** | `ConfirmBorrowingFrm.java` | ✅ Complete (Module 1) |
| **View** | `SearchMode.java` | ✅ Complete |
| **DAO** | `DAO.java`, `UserDAO.java`, `BookDAO.java` | ✅ Complete |
| **DAO** | `BorrowingDAO.java` | ✅ `searchBorrowing()`, `updateBorrowingStatus()`, `loadBorrowedBooks()` |
| **DAO** | `BorrowedBookDAO.java` | ✅ `findByBorrowingId()`, `updateReturnStatus()` |
| **DAO** | `BookItemDAO.java` | ✅ `updateStatus()` |
| **DAO** | `FineDAO.java` | ✅ `findAll()` |
| **DAO** | `BorrowedBookFineDAO.java` | ✅ `save()` |
| **DAO** | `BillDAO.java` | ✅ `saveBill()` |
| **Model** | All 9 model classes | ✅ Complete |

### ❌ Remaining Work

| # | Layer | File | What to do |
|---|---|---|---|
| 1 | **View** | `AddFineDlg.java` | **Create** — modal JDialog for adding fines to a BorrowedBook |
| 2 | **View** | `ReturnConfirmFrm.java` | **Create** — bill summary + save bill frame |
| 3 | **View** | `SearchBorrowingFrm.java` | **Wire up** — connect `btnAddFine` → `AddFineDlg`, `btnContinue` → `ReturnConfirmFrm` |
| 4 | **DAO** | `BillDAO.java` | **Add** `calculateFine(Borrowing)` method |

---

## Step-by-Step Plan

### Step 1: Create `AddFineDlg.java`

**File**: `src/main/java/com/ptit/p/documents/view/AddFineDlg.java`

**Purpose**: A modal `JDialog` that lets the librarian select a fine type and attach it to a specific `BorrowedBook`.

**Design** (from [ui_design_spec.md](file:///home/huycao/Desktop/PDocs/P-Documents/resources/ui_design_spec.md)):
```
CLASS: AddFineDlg extends JDialog  (modal)
SIZE:  500 × 350
```

**Components**:
- `lblBookInfo` — display book name/ID (read-only)
- `cmbFineType` — `JComboBox<Fine>` populated from `FineDAO.findAll()`
- `txtFineRate` — auto-filled when `cmbFineType` changes
- `txtNote` — `JTextArea(3, 30)` for detailed notes
- `btnAdd` / `btnCancel`

**Logic**:
```
Constructor(JFrame parent, BorrowedBook bb):
  1. this.borrowedBook = bb
  2. Load fine types: FineDAO.findAll() → populate cmbFineType

cmbFineType.change → txtFineRate.text = selectedFine.fineRate

btnAdd.click:
  1. Validate a Fine is selected
  2. Create BorrowedBookFine:
     - fineRate = selectedFine.fineRate
     - fine = selectedFine
     - borrowedBook = this.borrowedBook
  3. Call BorrowedBookFineDAO.save(borrowedBookFine)
  4. Add the fine to borrowedBook.getBorrowedBookFines() list (in-memory)
  5. dispose()
```

**Dependencies**: `FineDAO`, `BorrowedBookFineDAO`, models `Fine`, `BorrowedBookFine`, `BorrowedBook`

---

### Step 2: Create `ReturnConfirmFrm.java`

**File**: `src/main/java/com/ptit/p/documents/view/ReturnConfirmFrm.java`

**Purpose**: Shows the bill summary (overdue fines + damage fines) and lets the librarian confirm the return and save the bill.

**Design** (from [ui_design_spec.md](file:///home/huycao/Desktop/PDocs/P-Documents/resources/ui_design_spec.md)):
```
CLASS: ReturnConfirmFrm extends JFrame
SIZE:  850 × 600
```

**Components**:
- **pnlStudentInfo** (NORTH): read-only labels for student name, ID, email, phone
- **pnlBookDetail** (CENTER): `tblReturnBooks` table with columns: `#, Mã sách, Tên sách, Hạn trả, Trạng thái, Lỗi phạt, Tiền phạt, Ghi chú`
- **pnlBillSummary**: read-only fields for:
  - `txtBorrowDate`, `txtReturnDate` (= today)
  - `txtOverdueDays` (calculated)
  - `txtOverdueFine`, `txtDamageFine`, `txtTotalAmount` (bold, red)
  - `cmbPaymentType` (`JComboBox`: "Tiền mặt", "Chuyển khoản")
  - `txtNote` (`JTextField`)
- **pnlActions** (SOUTH): `btnSaveBill`, `btnCancel`

**Logic** (from [demoDesign.md](file:///home/huycao/Desktop/PDocs/P-Documents/resources/demoDesign.md) steps 38–57):
```
Constructor(User currentUser, Borrowing borrowing):
  1. Populate student info from borrowing.getStudent()
  2. Populate tblReturnBooks from borrowing.getBorrowedBooks()
     (include each book's BorrowedBookFine list)
  3. Calculate fine summary:
     For each BorrowedBook:
       - overdueDays = max(0, ChronoUnit.DAYS.between(expectedReturnDate, today))
       - overdueFine = overdueDays * fineRatePerDay (from tblFine "Trả trễ")
       - damageFine = sum of all BorrowedBookFine.fineRate for this book
     totalAmount = sum of all overdueFines + sum of all damageFines
  4. Fill summary fields

btnSaveBill.click:
  1. Confirm dialog
  2. For each BorrowedBook:
     a. bb.setActualReturnDate(LocalDate.now())
     b. BorrowedBookDAO.updateReturnStatus(bb)
  3. For each BookItem:
     a. BookItemDAO.updateStatus(bookItem.id, bb.status)
        ("good"→"good", "damaged"→"damaged", "lost"→"lost")
  4. BorrowingDAO.updateBorrowingStatus(borrowing.id, "returned")
  5. Create Bill object:
     - paymentDate = today
     - paymentType = cmbPaymentType.selectedItem
     - note = txtNote.text
     - borrowing = this.borrowing
     - user = currentUser
  6. BillDAO.saveBill(bill)
  7. Show success message
  8. dispose()
```

**Dependencies**: `BorrowingDAO`, `BorrowedBookDAO`, `BookItemDAO`, `BillDAO`, `BookDAO`, all related models

---

### Step 3: Wire up `SearchBorrowingFrm.java`

**File**: `src/main/java/com/ptit/p/documents/view/SearchBorrowingFrm.java`

Two placeholder actions need to be connected:

**3a. `addFineAction()` (line 268–281)**:
```java
// BEFORE (placeholder):
JOptionPane.showMessageDialog(this, "Mở AddFineDlg ...");

// AFTER:
new AddFineDlg(this, bb).setVisible(true);
showInlineDetail(); // refresh to show updated fines
```

**3b. `btnContinue` listener (line 165–170)**:
```java
// BEFORE (placeholder):
JOptionPane.showMessageDialog(this, "Chuyển sang ReturnConfirmFrm ...");

// AFTER:
new ReturnConfirmFrm(currentUser, selectedBorrowing).setVisible(true);
this.dispose();
```

---

### Step 4: Add `calculateFine()` to `BillDAO.java`

**File**: `src/main/java/com/ptit/p/documents/dao/BillDAO.java`

Currently `BillDAO` only has `saveBill()`. Add a calculation method:

```java
public Bill calculateFine(Borrowing borrowing) {
    Bill bill = new Bill();
    bill.setBorrowing(borrowing);
    bill.setPaymentDate(LocalDate.now());

    int totalOverdueDays = 0;
    double totalFine = 0.0;

    // Get the "Trả trễ" fine rate from DB
    FineDAO fineDAO = new FineDAO();
    List<Fine> fineTypes = fineDAO.findAll();
    double overdueRatePerDay = fineTypes.stream()
        .filter(f -> f.getName().contains("Trả trễ"))
        .findFirst().map(Fine::getFineRate).orElse(5000.0);

    for (BorrowedBook bb : borrowing.getBorrowedBooks()) {
        // Overdue calculation
        if (bb.getExpectedReturnDate() != null) {
            long days = ChronoUnit.DAYS.between(bb.getExpectedReturnDate(), LocalDate.now());
            if (days > 0) {
                totalOverdueDays += (int) days;
                totalFine += days * overdueRatePerDay;
            }
        }
        // Damage/loss fines
        if (bb.getBorrowedBookFines() != null) {
            for (BorrowedBookFine bbf : bb.getBorrowedBookFines()) {
                totalFine += bbf.getFineRate();
            }
        }
    }

    bill.setOverdueDay(totalOverdueDays);
    bill.setFine(totalFine);
    bill.setAmount(totalFine);
    return bill;
}
```

---

## Implementation Order

```
Step 1 ─── AddFineDlg.java ─────────────┐
                                         │
Step 2 ─── ReturnConfirmFrm.java ────────┤
                                         ├──→ Step 3: Wire up SearchBorrowingFrm
Step 4 ─── BillDAO.calculateFine() ──────┘
```

> [!TIP]
> Steps 1, 2, and 4 are independent and can be done in parallel.
> Step 3 can only be done after Steps 1 and 2 are complete.

---

## Testing Checklist

- [ ] **AddFineDlg**: Open from SearchBorrowingFrm inline detail → select a fine type → save → fine appears in the table
- [ ] **ReturnConfirmFrm**: Click "Tiếp tục" → bill summary loads → overdue days calculated correctly
- [ ] **ReturnConfirmFrm**: Click "Lưu hóa đơn" → `tblBill` row created → `tblBorrowing.status` = "returned" → `tblBookItem.status` updated
- [ ] **End-to-end**: LibrarianHomeFrm → "Trả sách" → search → select → add fine → continue → save bill → success
