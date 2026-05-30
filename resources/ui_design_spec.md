# P-Documents — Swing UI Frame Design Specification

> **Purpose**: LLM-consumable UI blueprint for implementing Java Swing JFrames.
> **Modules**: (1) Book Receiving (Xử lý nhận sách) · (2) Book Returning (Xử lý trả sách)
> **Tech**: Java Swing (`JFrame`, `JPanel`, `JTable`, `JButton`, …)
> **Package**: `com.ptit.p.documents.view`

---

## Global Conventions

| Item | Convention |
|---|---|
| Base class | Every `*Frm` class `extends JFrame` |
| Layout manager | `BorderLayout` for top-level; `GridBagLayout` / `BoxLayout` for inner panels |
| Sizing | Default frame size `900 × 600`; min size `700 × 500` |
| Close op | `JFrame.DISPOSE_ON_CLOSE` (sub-frames), `EXIT_ON_CLOSE` (main frame) |
| Naming | Components prefixed: `btn*`, `txt*`, `lbl*`, `tbl*`, `pnl*`, `cmb*`, `spn*` |
| Font | `"Segoe UI"` or system default, size 13 for body, 18+ for titles |
| Dialog | Use `JOptionPane` for confirm/error/info messages |
| Table model | `DefaultTableModel` with `isCellEditable → false` |
| Date fields | `JFormattedTextField` with `DateTimeFormatter.ofPattern("dd/MM/yyyy")` |

---

## Shared Frame: LoginFrm

```
CLASS: LoginFrm extends JFrame
TITLE: "Đăng nhập hệ thống"
SIZE:  400 × 280, centered, not resizable
CLOSE: EXIT_ON_CLOSE

LAYOUT (BorderLayout):
┌─────────────────────────────────────────┐
│            pnlHeader (NORTH)            │
│  lblTitle: "HỆ THỐNG QUẢN LÝ THƯ VIỆN" │
│  font: bold 18pt, center-aligned        │
├─────────────────────────────────────────┤
│          pnlForm (CENTER)               │
│  GridBagLayout, insets(10)              │
│                                         │
│  Row 0: lblUsername  "Tên đăng nhập:"   │
│         txtUsername  JTextField(20)      │
│  Row 1: lblPassword "Mật khẩu:"        │
│         txtPassword JPasswordField(20)  │
├─────────────────────────────────────────┤
│          pnlButtons (SOUTH)             │
│  FlowLayout(CENTER)                     │
│  btnLogin   "Đăng nhập"                │
│  btnCancel  "Thoát"                     │
└─────────────────────────────────────────┘

ACTIONS:
  btnLogin.click →
    1. Validate txtUsername & txtPassword not empty
    2. Call UserDAO.checkLogin(username, password) → User | null
    3. If User != null:
       - User.getRole() == "librarian" → new LibrarianHomeFrm(user).setVisible(true)
       - User.getRole() == "admin"     → new AdminHomeFrm(user).setVisible(true)
       - dispose() this frame
    4. If null → JOptionPane.showMessageDialog("Sai tên đăng nhập hoặc mật khẩu")
  btnCancel.click → System.exit(0)

DATA BINDING:
  - UserDAO.checkLogin(String username, String password) → User
```

---

## Shared Frame: LibrarianHomeFrm

```
CLASS: LibrarianHomeFrm extends JFrame
TITLE: "Trang chủ - Thủ thư: {user.fullName}"
SIZE:  900 × 600, centered
CLOSE: EXIT_ON_CLOSE
FIELD: User currentUser  (passed via constructor)

LAYOUT (BorderLayout):
┌──────────────────────────────────────────────────┐
│                pnlHeader (NORTH)                 │
│  lblWelcome: "Xin chào, {user.fullName}"         │
│  lblRole: "Vai trò: Thủ thư"                    │
│  font: 16pt, padding 10                          │
├─────────────┬────────────────────────────────────┤
│ pnlMenu     │        pnlContent (CENTER)         │
│ (WEST)      │  CardLayout or empty JPanel        │
│ 180px wide  │  placeholder for future content    │
│             │                                    │
│ btnConfirm  │                                    │
│ Borrowing   │                                    │
│ "XỬ LÝ     │                                    │
│  NHẬN SÁCH" │                                    │
│             │                                    │
│ btnReturn   │                                    │
│ Book        │                                    │
│ "TRẢ SÁCH"  │                                    │
│             │                                    │
│ btnLogout   │                                    │
│ "Đăng xuất" │                                    │
├─────────────┴────────────────────────────────────┤
│              pnlFooter (SOUTH)                   │
│  lblStatus: "Sẵn sàng"                          │
└──────────────────────────────────────────────────┘

ACTIONS:
  btnConfirmBorrowing.click →
    new SearchBorrowingFrm(currentUser, SearchMode.CONFIRM_BORROW).setVisible(true)
  btnReturnBook.click →
    new SearchBorrowingFrm(currentUser, SearchMode.RETURN_BOOK).setVisible(true)
  btnLogout.click →
    dispose(); new LoginFrm().setVisible(true)
```

---

## MODULE 1 — Book Receiving (Xử lý nhận sách)

### Frame 1.1: SearchBorrowingFrm

```
CLASS: SearchBorrowingFrm extends JFrame
TITLE: "Tìm kiếm phiếu mượn"
SIZE:  850 × 550, centered
CLOSE: DISPOSE_ON_CLOSE
FIELDS:
  - User currentUser
  - SearchMode mode   (enum: CONFIRM_BORROW | RETURN_BOOK)

LAYOUT (BorderLayout):
┌──────────────────────────────────────────────────┐
│              pnlSearch (NORTH)                   │
│  GridBagLayout, border titled "Tìm kiếm"        │
│                                                  │
│  Row 0:                                          │
│    lblStudentId   "Mã sinh viên:"                │
│    txtStudentId   JTextField(12)                  │
│    lblStudentName "Tên sinh viên:"               │
│    txtStudentName JTextField(15)                  │
│    btnSearch      "Tìm kiếm"                    │
│    btnClear       "Xóa"                         │
├──────────────────────────────────────────────────┤
│             pnlResult (CENTER)                   │
│  JScrollPane wrapping tblResult                  │
│                                                  │
│  tblResult columns:                              │
│  ┌────┬─────────┬───────────────┬───────────┬────────────────┬──────────┐
│  │ #  │ Mã PM   │ Tên sinh viên │ Ngày mượn │ Ngày hẹn nhận  │ Trạng thái│
│  │idx │ id      │ student.name  │ borrowDate│ expectedRecv   │ status   │
│  └────┴─────────┴───────────────┴───────────┴────────────────┴──────────┘
│  Selection mode: SINGLE_SELECTION                │
├──────────────────────────────────────────────────┤
│             pnlActions (SOUTH)                   │
│  FlowLayout(RIGHT)                               │
│  btnSelect   "Chọn phiếu mượn"                  │
│  btnBack     "Quay lại"                         │
└──────────────────────────────────────────────────┘

ACTIONS:
  btnSearch.click →
    1. Read txtStudentId.text, txtStudentName.text
    2. Call BorrowingDAO.searchBorrowing(studentId, studentName)
       → List<Borrowing>  (filter by status suitable for current mode)
    3. Populate tblResult with results
  btnClear.click →
    Clear txtStudentId, txtStudentName, clear tblResult
  btnSelect.click →
    1. Get selected row → Borrowing object
    2. If mode == CONFIRM_BORROW:
         new ConfirmBorrowingFrm(currentUser, selectedBorrowing).setVisible(true)
    3. If mode == RETURN_BOOK:
         Open detail view within this frame (see Module 2 for detail)
    4. dispose() or keep open based on UX choice
  btnBack.click → dispose()

  tblResult.doubleClick → same as btnSelect.click

DATA BINDING:
  - BorrowingDAO.searchBorrowing(String studentId, String studentName) → List<Borrowing>
  - Each Borrowing contains: id, borrowDate, expectedReceiveDate, status, Student, List<BorrowedBook>
```

### Frame 1.2: ConfirmBorrowingFrm

```
CLASS: ConfirmBorrowingFrm extends JFrame
TITLE: "Xác nhận cho mượn sách - Phiếu #{borrowing.id}"
SIZE:  800 × 550, centered
CLOSE: DISPOSE_ON_CLOSE
FIELDS:
  - User currentUser
  - Borrowing borrowing

LAYOUT (BorderLayout):
┌──────────────────────────────────────────────────┐
│            pnlStudentInfo (NORTH)                │
│  Border titled "Thông tin sinh viên"             │
│  GridBagLayout                                   │
│                                                  │
│  Row 0: lblId      "Mã SV:"                     │
│         txtId      JTextField (readonly)          │
│         lblName    "Họ tên:"                     │
│         txtName    JTextField (readonly)          │
│  Row 1: lblEmail   "Email:"                      │
│         txtEmail   JTextField (readonly)          │
│         lblPhone   "SĐT:"                       │
│         txtPhone   JTextField (readonly)          │
├──────────────────────────────────────────────────┤
│            pnlBookList (CENTER)                  │
│  Border titled "Danh sách sách mượn"             │
│  JScrollPane wrapping tblBooks                   │
│                                                  │
│  tblBooks columns:                               │
│  ┌────┬──────────┬──────────┬────────┬───────────┬──────────┐
│  │ #  │ Mã sách  │ Tên sách │ Tác giả│ Ngày hẹn  │ Tình     │
│  │    │ itemId   │ title    │ author │ trả       │ trạng    │
│  └────┴──────────┴──────────┴────────┴───────────┴──────────┘
├──────────────────────────────────────────────────┤
│            pnlBorrowInfo (between CENTER & SOUTH)│
│  Border titled "Thông tin phiếu mượn"            │
│  Row: lblBorrowDate "Ngày mượn:"                 │
│       txtBorrowDate  (readonly, auto = today)     │
│       lblExpectedRecv "Ngày hẹn nhận:"           │
│       txtExpectedRecv (readonly)                  │
├──────────────────────────────────────────────────┤
│            pnlActions (SOUTH)                    │
│  FlowLayout(RIGHT)                               │
│  btnConfirm   "Xác nhận cho mượn"               │
│  btnCancel    "Hủy"                             │
└──────────────────────────────────────────────────┘

ACTIONS:
  Constructor →
    1. Populate student info from borrowing.getStudent()
    2. Populate tblBooks from borrowing.getBooks()
    3. Set txtBorrowDate = today, txtExpectedRecv = borrowing.expectedReceiveDate

  btnConfirm.click →
    1. JOptionPane.showConfirmDialog("Xác nhận cho sinh viên mượn sách?")
    2. If YES:
       a. BorrowingDAO.updateBorrowingStatus(borrowing.id, "borrowed")
          → sets actualReceiveDate = today, status = "borrowed"
       b. For each BorrowedBook in borrowing.books:
          - BorrowedBookDAO.setBorrowedBook(borrowedBook)
            → create BorrowedBook record with expectedReturnDate
       c. For each BookItem:
          - BookItemDAO.updateStatus(bookItem.id, "borrowed")
       d. JOptionPane.showMessageDialog("Xác nhận cho mượn thành công!")
       e. dispose()
       f. Return to LibrarianHomeFrm
    3. If NO → do nothing

  btnCancel.click → dispose()

DATA BINDING:
  - BorrowingDAO.updateBorrowingStatus(int id, String newStatus) → boolean
  - BorrowedBookDAO.setBorrowedBook(BorrowedBook) → boolean
  - BookItemDAO.updateStatus(int id, String status) → boolean
```

---

## MODULE 2 — Book Returning (Xử lý trả sách)

> **Note**: This module reuses `SearchBorrowingFrm` with `mode = RETURN_BOOK`.
> After selecting a borrowing, the flow continues below.

### Frame 2.1: SearchBorrowingFrm (Return Mode — reused)

Same as Frame 1.1, but:
- `mode = RETURN_BOOK`
- `tblResult` filters borrowings with `status IN ('borrowed', 'overdue')`
- `btnSelect` label changes to `"Chọn phiếu trả"`
- On select → instead of ConfirmBorrowingFrm, show **inline detail** or open a detail panel

**Extended behavior for Return Mode**:

When a row is selected in the table:

```
INLINE DETAIL PANEL (replaces or appears below tblResult):
┌──────────────────────────────────────────────────┐
│  pnlBorrowingDetail                              │
│  Border titled "Chi tiết phiếu mượn #{id}"       │
│                                                  │
│  Student info: lblName, lblId (readonly labels)  │
│  Borrow date, Expected return date               │
│                                                  │
│  tblBorrowedBooks columns:                       │
│  ┌────┬─────────┬──────────┬─────────┬───────┬────────────┬───────────┐
│  │ #  │ Mã sách │ Tên sách │ Hạn trả │ Trạng │ Lỗi phạt   │ Thao tác  │
│  │    │ itemId  │ title    │ expDate │ thái  │ (tóm tắt)  │ [+Phạt]   │
│  └────┴─────────┴──────────┴─────────┴───────┴────────────┴───────────┘
│                                                  │
│  btnAddFine    "Thêm lỗi phạt"  (per-row)       │
│  btnContinue   "Tiếp tục →"                     │
│  btnBack       "Quay lại"                       │
└──────────────────────────────────────────────────┘
```

**Action: btnAddFine.click** → opens `AddFineDlg` (dialog)

### Dialog 2.2: AddFineDlg

```
CLASS: AddFineDlg extends JDialog
TITLE: "Thêm lỗi phạt - Sách #{bookItem.id}"
SIZE:  500 × 350, modal, centered relative to parent
CLOSE: DISPOSE_ON_CLOSE
FIELDS:
  - BorrowedBook borrowedBook

LAYOUT (BorderLayout):
┌──────────────────────────────────────────────────┐
│             pnlBookInfo (NORTH)                  │
│  "Sách: {title} (Mã: {bookItem.id})"            │
├──────────────────────────────────────────────────┤
│             pnlFineForm (CENTER)                 │
│  Border titled "Chọn lỗi phạt"                  │
│  GridBagLayout                                   │
│                                                  │
│  Row 0: lblFineType   "Loại lỗi:"               │
│         cmbFineType   JComboBox<Fine>            │
│         (populated from FineDAO.findAll())       │
│  Row 1: lblFineRate   "Tỷ lệ phạt:"             │
│         txtFineRate   JTextField (auto-filled)    │
│  Row 2: lblNote       "Ghi chú chi tiết:"       │
│         txtNote       JTextArea(3, 30)            │
│         (wrapped in JScrollPane)                 │
├──────────────────────────────────────────────────┤
│             pnlActions (SOUTH)                   │
│  FlowLayout(RIGHT)                               │
│  btnAdd     "Thêm"                              │
│  btnCancel  "Hủy"                               │
└──────────────────────────────────────────────────┘

ACTIONS:
  Constructor →
    1. Load fine types: FineDAO.findAll() → List<Fine>
    2. Populate cmbFineType with Fine objects (display: fine.name)

  cmbFineType.change →
    txtFineRate.text = selectedFine.fineRate

  btnAdd.click →
    1. Validate: a Fine must be selected
    2. Create BorrowedBookFine object:
       - fineRate = selectedFine.fineRate
       - fine = selectedFine
       - borrowedBook = this.borrowedBook
    3. BorrowedBookDAO.setBorrowedBookFine(borrowedBookFine)
    4. Update parent's tblBorrowedBooks to reflect the added fine
    5. dispose()

  btnCancel.click → dispose()

DATA BINDING:
  - FineDAO.findAll() → List<Fine>
  - BorrowedBookDAO.setBorrowedBookFine(BorrowedBookFine) → boolean
```

### Frame 2.3: ReturnConfirmFrm

```
CLASS: ReturnConfirmFrm extends JFrame
TITLE: "Xác nhận trả sách - Phiếu #{borrowing.id}"
SIZE:  850 × 600, centered
CLOSE: DISPOSE_ON_CLOSE
FIELDS:
  - User currentUser
  - Borrowing borrowing
  - Bill bill  (pre-calculated by BillDAO.calculateFine)

LAYOUT (BorderLayout):
┌──────────────────────────────────────────────────┐
│            pnlStudentInfo (NORTH)                │
│  Border titled "Thông tin sinh viên"             │
│  (same layout as ConfirmBorrowingFrm student     │
│   info panel — readonly fields)                  │
├──────────────────────────────────────────────────┤
│            pnlBookDetail (CENTER)                │
│  Border titled "Chi tiết sách trả"               │
│  JScrollPane wrapping tblReturnBooks             │
│                                                  │
│  tblReturnBooks columns:                         │
│  ┌────┬─────────┬──────────┬─────────┬───────┬──────────┬──────────┬──────────┐
│  │ #  │ Mã sách │ Tên sách │ Hạn trả │ Trạng │ Lỗi phạt │ Tiền     │ Ghi chú  │
│  │    │ itemId  │ title    │ expDate │ thái  │ (list)   │ phạt     │          │
│  └────┴─────────┴──────────┴─────────┴───────┴──────────┴──────────┴──────────┘
├──────────────────────────────────────────────────┤
│            pnlBillSummary (below CENTER)         │
│  Border titled "Tóm tắt hóa đơn"                │
│  GridBagLayout                                   │
│                                                  │
│  Row 0: lblBorrowDate    "Ngày mượn:"            │
│         txtBorrowDate    (readonly)               │
│  Row 1: lblReturnDate    "Ngày trả thực tế:"    │
│         txtReturnDate    (readonly, = today)      │
│  Row 2: lblOverdueDays   "Số ngày quá hạn:"     │
│         txtOverdueDays   (readonly, calculated)   │
│  Row 3: lblOverdueFine   "Tiền phạt quá hạn:"   │
│         txtOverdueFine   (readonly)               │
│  Row 4: lblDamageFine    "Phí bồi thường:"      │
│         txtDamageFine    (readonly)               │
│  Row 5: lblTotalAmount   "TỔNG TIỀN:"           │
│         txtTotalAmount   (readonly, bold, red)    │
│  Row 6: lblPaymentType   "Hình thức thanh toán:" │
│         cmbPaymentType   JComboBox               │
│         (values: "Tiền mặt", "Chuyển khoản")    │
│  Row 7: lblNote          "Ghi chú:"             │
│         txtNote          JTextField              │
├──────────────────────────────────────────────────┤
│            pnlActions (SOUTH)                    │
│  FlowLayout(RIGHT)                               │
│  btnSaveBill   "Lưu hóa đơn & Xác nhận trả"    │
│  btnCancel     "Hủy"                            │
└──────────────────────────────────────────────────┘

ACTIONS:
  Constructor →
    1. Populate student info from borrowing.student
    2. Populate tblReturnBooks from borrowing.books
       (including each book's BorrowedBookFine list)
    3. bill = BillDAO.calculateFine(borrowing)
       → computes: overdueDays, overdueFine, damageFine, totalAmount
    4. Fill bill summary fields from bill object

  btnSaveBill.click →
    1. JOptionPane.showConfirmDialog("Xác nhận trả sách và lưu hóa đơn?")
    2. If YES:
       a. For each BorrowedBook:
          - BorrowedBookDAO.updateBorrowedBookStatus(bb.id, bb.status)
       b. For each BookItem:
          - BookItemDAO.updateStatus(bookItem.id, newStatus)
            (newStatus based on BorrowedBook.status: "good"→"good", "damaged"→"damaged", "lost"→"lost")
       c. BorrowingDAO.updateBorrowingStatus(borrowing.id, "returned")
          (set actualReceiveDate if all books returned)
       d. bill.paymentType = cmbPaymentType.selectedItem
          bill.note = txtNote.text
          bill.paymentDate = today
          bill.user = currentUser
          BillDAO.createBill(bill)
       e. JOptionPane.showMessageDialog("Trả sách thành công! Hóa đơn đã được lưu.")
       f. dispose()
       g. Return to LibrarianHomeFrm

  btnCancel.click → dispose()

DATA BINDING:
  - BillDAO.calculateFine(Borrowing) → Bill
  - BorrowedBookDAO.updateBorrowedBookStatus(int id, String status) → boolean
  - BookItemDAO.updateStatus(int id, String status) → boolean
  - BorrowingDAO.updateBorrowingStatus(int id, String status) → boolean
  - BillDAO.createBill(Bill) → boolean
```

---

## Navigation Flow Summary

```
                    ┌──────────┐
                    │ LoginFrm │
                    └────┬─────┘
                         │ (authenticate)
                         ▼
               ┌──────────────────┐
               │ LibrarianHomeFrm │
               └───┬──────────┬───┘
                   │          │
      ┌────────────┘          └───────────────┐
      ▼                                       ▼
  MODULE 1: NHẬN SÁCH                   MODULE 2: TRẢ SÁCH
      │                                       │
      ▼                                       ▼
┌─────────────────┐                  ┌─────────────────┐
│SearchBorrowingFrm│                  │SearchBorrowingFrm│
│(CONFIRM_BORROW) │                  │(RETURN_BOOK)    │
└───────┬─────────┘                  └───────┬─────────┘
        │ (select borrowing)                 │ (select borrowing)
        ▼                                    │
┌──────────────────┐                         │ (show inline detail
│ConfirmBorrowingFrm│                         │  + add fines per book)
│                  │                         │
│ - show student   │                    ┌────┴────┐
│ - show books     │                    │AddFineDlg│ (modal, per book)
│ - confirm borrow │                    └────┬────┘
└──────────────────┘                         │
                                             ▼
                                    ┌─────────────────┐
                                    │ ReturnConfirmFrm │
                                    │                 │
                                    │ - bill summary  │
                                    │ - save bill     │
                                    └─────────────────┘
```

---

## Component-to-Class Mapping

| Swing Frame Class | Module | DAO Dependencies | Model Dependencies |
|---|---|---|---|
| `LoginFrm` | Shared | `UserDAO` | `User` |
| `LibrarianHomeFrm` | Shared | — | `User` |
| `SearchBorrowingFrm` | Both | `BorrowingDAO` | `Borrowing`, `Student`, `BorrowedBook` |
| `ConfirmBorrowingFrm` | M1 | `BorrowingDAO`, `BorrowedBookDAO`, `BookItemDAO` | `Borrowing`, `Student`, `BorrowedBook`, `BookItem` |
| `AddFineDlg` | M2 | `FineDAO`, `BorrowedBookDAO` | `Fine`, `BorrowedBookFine`, `BorrowedBook` |
| `ReturnConfirmFrm` | M2 | `BorrowingDAO`, `BorrowedBookDAO`, `BookItemDAO`, `BillDAO` | `Borrowing`, `Student`, `BorrowedBook`, `BookItem`, `Bill`, `Fine`, `BorrowedBookFine` |

---

## DAO Method Signatures Required

```java
// UserDAO
User checkLogin(String username, String password);

// BorrowingDAO
List<Borrowing> searchBorrowing(String studentId, String studentName);
boolean updateBorrowingStatus(int borrowingId, String newStatus);

// BorrowedBookDAO
boolean setBorrowedBook(BorrowedBook borrowedBook);
boolean setBorrowedBookFine(BorrowedBookFine fine);
boolean updateBorrowedBookStatus(int borrowedBookId, String status);

// BookItemDAO
boolean updateStatus(int bookItemId, String status);

// BillDAO
Bill calculateFine(Borrowing borrowing);
boolean createBill(Bill bill);

// FineDAO
List<Fine> findAll();
```
