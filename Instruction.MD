Here is a specific, step-by-step plan to implement the DAO layer for Module 2: Return Book & Calculate Fines.

When a user returns books, the system must update the borrowing records, check conditions to apply fines, and generate a final bill. To support this, we will add 3 new DAO classes and update 2 existing DAO classes.

Step 1: Create FineDAO
We need to load the penalty rules (e.g., late fees, missing book charges) so the librarian can apply them at the UI level.

Action: Create FineDAO.java
Methods to implement:
List<Fine> findAll(): Queries tblFine to get all penalty types and their rates.
Step 2: Update BorrowedBookDAO and BookItemDAO
When reviewing the returned books, the librarian marks the condition of each book and its return date.

Action: Edit BorrowedBookDAO.java
Methods to implement:
boolean updateReturnStatus(BorrowedBook bb): Updates actualReturnDate, status (good/damaged/lost), note, and calculated price in tblBorrowedBook.
(Note: BookItemDAO already has updateStatus() or we will ensure it does, so it can change the physical book's status to 'damaged' or 'lost' if needed).
Step 3: Create BorrowedBookFineDAO
If a book is returned late or damaged, the librarian applies the fines retrieved from Step 1 carefully to that specific borrowed book item.

Action: Create BorrowedBookFineDAO.java
Methods to implement:
boolean save(BorrowedBookFine fine): Inserts a record into tblBorrowedBookFine mapping the applied fine to the specific tblBorrowedBookID along with the captured fineRate.
Step 4: Update BorrowingDAO
Once all books in the slip are processed, the overarching borrowing session is closed.

Action: Edit BorrowingDAO.java
Methods to implement:
boolean completeBorrowing(int borrowingId, LocalDate actualReceiveDate): Updates tblBorrowing to set actualReceiveDate and changes its status from pending to returned.
Step 5: Create BillDAO
Finally, the system logs the transaction and payment details into a final receipt.

Action: Create BillDAO.java
Methods to implement:
boolean saveBill(Bill bill): Inserts a record into tblBill tracking the total payment (paymentDate, paymentType, note), tying it to tblBorrowingID and the librarian tblUserID.