# Bill Saving Workflow (Return Book Module)

## Purpose
Describe the ordered flow of functions when a librarian confirms book returns and saves the bill.

## Entry Points
- UI flow starts from ReturnConfirmFrm, usually opened from SearchBorrowingFrm in RETURN_BOOK mode.

## Ordered Workflow (Function Call Order)
1. UI opens ReturnConfirmFrm
   - Constructor runs `initComponents()` then `loadBorrowingData()`.
2. `loadBorrowingData()`
   - Loads missing borrowed books using BorrowingDAO if needed.
   - Fills student info fields.
   - Calls `populateBookTable()` then `calculateSummary()`.
3. `populateBookTable()`
   - For each BorrowedBook:
     - Resolve book title via BookDAO.
     - Compute overdue days and overdue fine.
     - Sum damage fines from BorrowedBookFine list.
     - Store per-book fine total in memory.
4. `calculateSummary()`
   - Calls BillDAO.calculateFine(borrowing) to compute a baseline bill.
   - Updates summary fields with totals for display.
5. User clicks "Luu hoa don & Xac nhan tra"
   - Triggers `saveBillAction()`.
6. `saveBillAction()`
   - Shows confirm dialog.
   - For each BorrowedBook:
     - Set actual return date.
     - Ensure status is not empty..
     - Set price to per-book fine total.
     - Persist via BorrowedBookDAO.updateReturnStatus(...)..
     - Update BookItem status via BookItemDAO.updateStatus(...).
   - Update Borrowing status via BorrowingDAO.updateBorrowingStatus(...).
   - Build Bill fields (payment date, type, note, totals, user).
   - Persist bill via BillDAO.saveBill(...).
   - Show success dialog and close the frame.

## Key Functions and Files
- ReturnConfirmFrm: [src/main/java/com/ptit/p/documents/view/ReturnConfirmFrm.java](../src/main/java/com/ptit/p/documents/view/ReturnConfirmFrm.java)
- BillDAO: [src/main/java/com/ptit/p/documents/dao/BillDAO.java](../src/main/java/com/ptit/p/documents/dao/BillDAO.java)
- BorrowingDAO: [src/main/java/com/ptit/p/documents/dao/BorrowingDAO.java](../src/main/java/com/ptit/p/documents/dao/BorrowingDAO.java)
- BorrowedBookDAO: [src/main/java/com/ptit/p/documents/dao/BorrowedBookDAO.java](../src/main/java/com/ptit/p/documents/dao/BorrowedBookDAO.java)
- BookItemDAO: [src/main/java/com/ptit/p/documents/dao/BookItemDAO.java](../src/main/java/com/ptit/p/documents/dao/BookItemDAO.java)
- BookDAO: [src/main/java/com/ptit/p/documents/dao/BookDAO.java](../src/main/java/com/ptit/p/documents/dao/BookDAO.java)

## Notes
- The per-book fine totals are kept in memory to update borrowed book price and to compute final totals.
- Bill totals are written to tblBill through BillDAO.saveBill(...).
