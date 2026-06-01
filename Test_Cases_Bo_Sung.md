# TÀI LIỆU KIỂM THỬ: ĐẶT SÁCH VÀ HỦY ĐẶT SÁCH
*(Đã được phân tích và bổ sung theo 2 phương pháp: Phân vùng tương đương & Phân tích giá trị biên)*

---

# PHẦN 1: CHỨC NĂNG ĐẶT SÁCH

## 1. Kiểm thử đơn vị - JUnit Test

| TT | Chức năng | Lớp DAO | Hàm/phương thức | Các trường hợp cần kiểm thử |
|----|-----------|---------|-----------------|------------------------------|
| 1  | Đặt sách  | StudentDAO | `searchStudent()` | Tìm thấy sinh viên với từ khóa hợp lệ (Chuẩn / Valid EP) |
| 2  | Đặt sách  | StudentDAO | `searchStudent()` | Không tìm thấy sinh viên với từ khóa không tồn tại (Invalid EP) |
| 3  | Đặt sách  | BookDAO | `searchBook()` | Tìm thấy sách theo ISBN / Thể loại hợp lệ (Chuẩn / Valid EP) |
| 4  | Đặt sách  | BookDAO | `searchBook()` | Không tìm thấy sách với từ khóa không khớp (Invalid EP) |
| 5  | Đặt sách  | BookDAO | `searchBook()` | Tìm sách và tính toán chính xác số lượng bản sao khả dụng rảnh (BVA) |
| 6  | Đặt sách  | BookDAO | `searchBook()` | Kiểm tra sách đã hết bản sao rảnh (Available Copies = 0) (BVA - Biên 0) |
| 7  | Đặt sách  | BorrowingDAO | `addBorrowing()` | Lưu phiếu đặt sách thành công với thông tin hợp lệ (Chuẩn / Valid EP) |
| 8  | Đặt sách  | BorrowingDAO | `addBorrowing()` | Không lưu được khi đối tượng sách không tồn tại (Invalid EP) |
| 9  | Đặt sách  | BorrowingDAO | `addBorrowing()` | Không lưu được khi sinh viên không tồn tại (Invalid EP) |
| 10 | Đặt sách  | BorrowingDAO | `addBorrowing()` | Không lưu được khi danh sách sách mượn rỗng (size = 0) (Invalid BVA) |
| 11 | Đặt sách  | BorrowingDAO | `addBorrowing()` | Không lưu được khi ngày nhận sách là một ngày trong quá khứ (Invalid BVA) |
| 12 | Đặt sách  | BorrowingDAO | `addBorrowing()` | Lưu thành công khi ngày nhận sách dự kiến bằng chính ngày hiện tại (Valid BVA) |

## 2. Kiểm thử chức năng - Blackbox test

### a. Lập kế hoạch test:
| TT | Chức năng | Các trường hợp cần kiểm thử |
|----|-----------|------------------------------|
| 1  | Đặt sách  | Mọi thông tin hợp lệ (Sinh viên tồn tại, Sách tồn tại trong kho, số lượng >=1) → Đặt sách thành công |
| 2  | Đặt sách  | Không chọn sách nào vào danh sách mượn (0 cuốn) → Cảnh báo lỗi, không cho đặt (BVA) |
| 3  | Đặt sách  | Nhập ngày hẹn lấy sách là ngày hôm qua (quá khứ) → Cảnh báo lỗi ngày tháng (BVA) |
| 4  | Đặt sách  | Nhập từ khóa tìm kiếm sinh viên không tồn tại → Báo lỗi không tìm thấy (Invalid EP) |
| 5  | Đặt sách  | Bỏ trống từ khóa tìm kiếm sách (chuỗi rỗng "") → Hiển thị toàn bộ sách (BVA - Biên dưới) |
| 6  | Đặt sách  | Chọn sách có số lượng bản sao rảnh (Available Copies) = 0 → Cảnh báo lỗi (BVA - Biên 0) |

### b. Các test case cho chức năng Đặt Sách

**+ Test case 1: Đặt sách thành công (Test case chuẩn - Valid EP, bỏ qua phần login)**

CSDL trước khi test:

-tblUser:
| ID | username | password | fullName | phone | role |
|----|----------|----------|----------|-------|------|
| 1 | admin | admin123 | Nguyễn Văn Admin | 0900000001 | admin |
| 2 | manager1 | manager123 | Lê Thị Quản Lý | 0900000002 | manager |
| 3 | librarian1 | lib123 | Trần Thị Thư | 0900000003 | librarian |

-tblStudent:
| ID | fullName | email | phone | address |
|----|----------|-------|-------|---------|
| SV001 | Đỗ Huy Hoàng | hoang@ptit.edu.vn | 0911111111 | Hà Nội |
| SV002 | Nguyễn Minh Kiên | kien@ptit.edu.vn | 0922222222 | Hà Nội |
| SV003 | Vũ Minh Sáng | sang@ptit.edu.vn | 0933333333 | Đà Nẵng |
| SV004 | Lê Đức Hiếu | hieu@ptit.edu.vn | 0944444444 | TP.HCM |
| SV005 | Trần Đắc Mạnh | manh@ptit.edu.vn | 0955555555 | Hải Phòng |

-tblBookItem (trích xuất một số bản ghi sách đang rảnh):
| ID | status | tblBookISBN |
|----|--------|-------------|
| 7 | good | ISBN-CS-04 |
| 8 | good | ISBN-LIT-01 |

-tblBorrowing:
| ID | tblStudentID | tblUserID | status | expectedReceiveDate |
|----|--------------|-----------|--------|---------------------|
| 1 | SV001 | 3 | returned | 2026-01-10 |
| 2 | SV002 | 3 | returned | 2026-01-20 |
| 3 | SV003 | 3 | returned | 2026-02-05 |
| 4 | SV004 | 3 | borrowed | 2026-02-25 |
| 5 | SV005 | 3 | borrowed | 2026-03-10 |
| 6 | SV001 | 3 | overdue | 2026-03-22 |
| 7 | SV002 | 3 | borrowed | 2026-04-05 |
| 8 | SV003 | 3 | returned | 2026-04-20 |
| 9 | SV004 | 3 | pending | 2026-05-08 |
| 10 | SV005 | 3 | borrowed | 2026-05-10 |

-tblBorrowedBook:
| ID | tblBorrowingID | tblBookItemID | expectedReturnDate | status |
|----|----------------|---------------|--------------------|--------|
| 1 | 1 | 1 | 2026-01-24 | good |
| 2 | 2 | 2 | 2026-02-03 | good |
| 3 | 3 | 1 | 2026-02-19 | damaged |
| 4 | 6 | 2 | 2026-04-05 | good |
| 5 | 10 | 1 | 2026-05-24 | good |
| 6 | 2 | 4 | 2026-02-03 | good |
| 7 | 4 | 5 | 2026-03-11 | good |
| 8 | 8 | 4 | 2026-05-04 | good |
| 9 | 5 | 8 | 2026-03-24 | good |
| 10 | 7 | 8 | 2026-04-19 | good |
| 11 | 9 | 10 | 2026-05-22 | good |
| 12 | 3 | 7 | 2026-02-19 | good |

| Các bước thực hiện | Kết quả mong đợi |
|--------------------|------------------|
| 1. Nhân viên (Thủ thư id=3) đã đăng nhập. Chọn chức năng Đặt sách từ menu chính. | Giao diện Đặt sách hiển thị ra. |
| 2. Nhập mã SV `SV001` vào ô tìm kiếm | Kết quả hiện ra thông tin khách hàng (Đỗ Huy Hoàng) bên trong giao diện. |
| 3. Chọn sách có mã `ISBN-CS-04` vào danh sách mượn. | Sách xuất hiện trong bảng danh sách cần đặt. |
| 4. Nhập vào: <br> - Ngày đến lấy = 15/06/2026 | Các ô thông tin được điền đủ. |
| 5. Click nút "Xác nhận đặt sách" | Thông báo: "Đặt sách thành công!\nMã phiếu mượn: 11" |
| 6. Click nút OK của thông báo | Quay trở về trang chủ của thủ thư. |

CSDL sau khi test:

- Các bảng khác không thay đổi, chỉ có bảng tblBorrowing và tblBorrowedBook thay đổi:

-tblBorrowing:
| ID | tblStudentID | tblUserID | status | expectedReceiveDate |
|----|--------------|-----------|--------|---------------------|
| ...| ... | ... | ... | ... |
| 9 | SV004 | 3 | pending | 2026-05-08 |
| 10 | SV005 | 3 | borrowed | 2026-05-10 |
| 11 | SV001 | 3 | pending | 2026-06-15 |

-tblBorrowedBook:
| ID | tblBorrowingID | tblBookItemID | expectedReturnDate | status |
|----|----------------|---------------|--------------------|--------|
| ...| ... | ... | ... | ... |
| 11 | 9 | 10 | 2026-05-22 | good |
| 12 | 3 | 7 | 2026-02-19 | good |
| 13 | 11 | 7 | 2026-06-29 | good |


**+ Test case 2: Đặt sách khi danh sách sách rỗng (Invalid BVA)**

CSDL trước khi test: 
(Giống hệt bảng CSDL trước khi test của Test case 1)

| Các bước thực hiện | Kết quả mong đợi |
|--------------------|------------------|
| 1. Thủ thư (id=3) đăng nhập, chọn chức năng Đặt sách | Giao diện Đặt sách hiển thị ra. |
| 2. Nhập mã SV `SV001` vào ô tìm kiếm | Giao diện hiển thị thông tin sinh viên Đỗ Huy Hoàng. |
| 3. KHÔNG chọn sách nào vào danh sách mượn | Bảng danh sách muốn mượn rỗng (0 cuốn). |
| 4. Click nút "Xác nhận đặt sách" | Hệ thống báo lỗi: "Vui lòng chọn ít nhất 1 cuốn sách để đặt." |

CSDL sau khi test: 
Không thay đổi (Không có bản ghi nào bị cập nhật hay thêm mới vào CSDL).


**+ Test case 3: Đặt sách với ngày hẹn lấy là ngày quá khứ (Invalid BVA)**

CSDL trước khi test: 
(Giống hệt bảng CSDL trước khi test của Test case 1)

| Các bước thực hiện | Kết quả mong đợi |
|--------------------|------------------|
| 1. Thủ thư (id=3) đăng nhập, chọn chức năng Đặt sách | Giao diện Đặt sách hiển thị ra. |
| 2. Nhập mã SV `SV001` và chọn sách `ISBN-CS-04` | Giao diện hiển thị đủ thông tin SV và Sách. |
| 3. Nhập ngày hẹn nhận sách (expectedReceiveDate) về 2 ngày trước (ngày trong quá khứ). | Ngày hẹn hiển thị trên form là ngày quá khứ. |
| 4. Click "Xác nhận đặt sách" | Hệ thống báo lỗi: "Ngày hẹn nhận sách không hợp lệ. Phải lớn hơn hoặc bằng ngày hiện tại." |

CSDL sau khi test: 
Không thay đổi.


**+ Test case 4: Tìm sinh viên không tồn tại (Invalid EP)**

CSDL trước khi test: 
(Giống hệt bảng CSDL trước khi test của Test case 1)

| Các bước thực hiện | Kết quả mong đợi |
|--------------------|------------------|
| 1. Thủ thư (id=3) đăng nhập, chọn chức năng Đặt sách | Giao diện Đặt sách hiển thị ra. |
| 2. Nhập mã SV `SV999` vào ô tìm kiếm sinh viên và click Tìm kiếm | Bảng kết quả rỗng. Hệ thống hiển thị thông báo: "Không tìm thấy sinh viên nào." |

CSDL sau khi test: 
Không thay đổi.


**+ Test case 5: Tìm kiếm sách với từ khóa rỗng (BVA - Biên dưới)**

CSDL trước khi test: 
(Giống hệt bảng CSDL trước khi test của Test case 1)

| Các bước thực hiện | Kết quả mong đợi |
|--------------------|------------------|
| 1. Thủ thư (id=3) đăng nhập, chọn chức năng Đặt sách | Giao diện Đặt sách hiển thị ra. |
| 2. Nhập mã SV `SV001` để chọn sinh viên | Thông tin sinh viên hiện ra. |
| 3. Để trống ô tìm kiếm sách (chuỗi rỗng `""`) và click Tìm kiếm | Bảng kết quả hiển thị TOÀN BỘ danh sách các đầu sách hiện có trong hệ thống. |

CSDL sau khi test: 
Không thay đổi.


**+ Test case 6: Chọn sách đã hết bản sao rảnh - Available Copies = 0 (BVA - Biên 0)**

CSDL trước khi test: 
(Giống hệt bảng CSDL trước khi test của Test case 1)

| Các bước thực hiện | Kết quả mong đợi |
|--------------------|------------------|
| 1. Thủ thư (id=3) đăng nhập, chọn chức năng Đặt sách | Giao diện Đặt sách hiển thị ra. |
| 2. Nhập mã SV `SV001` để chọn sinh viên | Thông tin sinh viên hiện ra. |
| 3. Tìm kiếm sách `ISBN-CS-03` (giả sử đầu sách này đã bị mượn hết) và click Tìm kiếm | Bảng kết quả hiện sách `ISBN-CS-03` với cột Available Copies = 0. |
| 4. Click chọn cuốn sách này để đưa vào danh sách chờ đặt | Hệ thống hiện cảnh báo lỗi: "Sách này hiện đã hết bản sao có sẵn!" và từ chối thêm vào danh sách. |

CSDL sau khi test: 
Không thay đổi.

---

# PHẦN 2: CHỨC NĂNG HỦY ĐẶT SÁCH

## 1. Kiểm thử đơn vị - JUnit Test

| TT | Chức năng | Lớp DAO | Hàm/phương thức | Các trường hợp cần kiểm thử |
|----|-----------|---------|-----------------|------------------------------|
| 1  | Hủy đặt sách | BorrowingDAO | `searchBorrowing()` | Tìm thấy phiếu đang chờ nhận (pending) tương ứng với sinh viên (Valid EP) |
| 2  | Hủy đặt sách | BorrowingDAO | `searchBorrowing()` | Không có phiếu nào đang chờ nhận của sinh viên này (Invalid EP) |
| 3  | Hủy đặt sách | BorrowingDAO | `cancelBorrowing()` | Hủy phiếu đang ở trạng thái pending thành công (Chuẩn - Valid EP) |
| 4  | Hủy đặt sách | BorrowingDAO | `cancelBorrowing()` | Không hủy được vì ID phiếu không tồn tại trong CSDL (Invalid EP) |
| 5  | Hủy đặt sách | BorrowingDAO | `cancelBorrowing()` | Không hủy được vì phiếu không ở trạng thái pending (VD: Đã hủy hoặc đã mượn) (Invalid EP) |
| 6  | Hủy đặt sách | BorrowingDAO | `cancelBorrowing()` | Không hủy được khi ID phiếu = 0 (Invalid BVA - Biên dưới) |
| 7  | Hủy đặt sách | BorrowingDAO | `cancelBorrowing()` | Không hủy được khi ID phiếu = -1 (Invalid BVA - Giá trị âm) |

## 2. Kiểm thử chức năng - Blackbox test

### a. Lập kế hoạch test:
| TT | Chức năng | Các trường hợp cần kiểm thử |
|----|-----------|------------------------------|
| 1  | Hủy đặt sách | Tìm phiếu pending tồn tại → Hủy thành công, phiếu biến mất khỏi danh sách chờ |
| 2  | Hủy đặt sách | Tìm sinh viên bằng từ khóa không khớp → Danh sách rỗng, không cho Hủy |
| 3  | Hủy đặt sách | Chọn vào một phiếu mượn nhưng phiếu đó đã bị đổi trạng thái ở tab khác → Hủy thất bại (EP) |

### b. Các test case cho chức năng Hủy Đặt Sách

**+ Test case 1: Tìm và hủy phiếu mượn thành công (Test case chuẩn, bỏ qua phần login)**

CSDL trước khi test:

-tblStudent:
| ID | fullName | email | phone | address |
|----|----------|-------|-------|---------|
| SV004 | Lê Đức Hiếu | hieu@ptit.edu.vn | 0944444444 | TP.HCM |

-tblBorrowing (Trích xuất các phiếu pending):
| ID | tblStudentID | tblUserID | status | expectedReceiveDate |
|----|--------------|-----------|--------|---------------------|
| 9 | SV004 | 3 | pending | 2026-05-08 |

-tblBorrowedBook (Trích xuất sách của phiếu ID 9):
| ID | tblBorrowingID | tblBookItemID | expectedReturnDate | status |
|----|----------------|---------------|--------------------|--------|
| 11 | 9 | 10 | 2026-05-22 | good |

| Các bước thực hiện | Kết quả mong đợi |
|--------------------|------------------|
| 1. Nhân viên tên Trần Thị Thư (id=3) đã đăng nhập. Chọn chức năng Hủy đặt sách từ menu chính. | Giao diện Tìm kiếm phiếu chờ nhận hiện ra. Có ô nhập Từ khóa tìm kiếm. |
| 2. Nhập: "SV004" <br> Và click vào nút Tìm kiếm | Bảng kết quả hiển thị 1 dòng phiếu:<br> ID=9 \| Lê Đức Hiếu \| pending |
| 3. Click chọn dòng phiếu ID=9 | Dòng phiếu được đánh dấu chọn. |
| 4. Click nút Hủy phiếu | Giao diện xác nhận hủy hiện ra (Hộp thoại: "Bạn có chắc muốn hủy phiếu mượn này không?") |
| 5. Click nút Có | Thông báo "Hủy đặt sách thành công!\nPhiếu 9 đã chuyển sang trạng thái 'cancelled'." |
| 6. Click nút OK của thông báo | Giao diện quay trở về trang chủ của nhân viên. Dòng ID=9 biến mất khỏi danh sách. |

CSDL sau khi test:

- Các bảng khác không thay đổi, chỉ có bảng tblBorrowing thay đổi:

-tblBorrowing:
| ID | tblStudentID | tblUserID | status | expectedReceiveDate |
|----|--------------|-----------|--------|---------------------|
| 9 | SV004 | 3 | cancelled | 2026-05-08 |


**+ Test case 2: Tìm phiếu với từ khóa không khớp (Ngoại lệ)**

CSDL trước khi test: 
(Giống hệt bảng CSDL trước khi test của Test case 1)

| Các bước thực hiện | Kết quả mong đợi |
|--------------------|------------------|
| 1. Nhân viên (id=3) đăng nhập, chọn Hủy đặt sách | Giao diện Tìm kiếm phiếu chờ nhận hiển thị. |
| 2. Nhập "SV999" và click nút Tìm kiếm | Bảng kết quả rỗng. Nhãn trạng thái báo: "Không tìm thấy phiếu mượn đang chờ." |
| 3. Không chọn phiếu nào (vì bảng rỗng), click Hủy phiếu | Thông báo lỗi: "Vui lòng chọn một phiếu mượn" |

CSDL sau khi test: 
Không thay đổi (Không có dữ liệu nào được tác động).


**+ Test case 3: Hủy phiếu bị lỗi do trạng thái thay đổi đột ngột (Invalid EP)**

CSDL trước khi test:
-tblBorrowing (Gỉa lập phiếu ID 9 đã bị sửa trạng thái bởi quy trình khác):
| ID | tblStudentID | tblUserID | status | expectedReceiveDate |
|----|--------------|-----------|--------|---------------------|
| 9 | SV004 | 3 | cancelled | 2026-05-08 |

| Các bước thực hiện | Kết quả mong đợi |
|--------------------|------------------|
| 1. Giả sử bảng kết quả chưa được tải lại, phiếu ID=9 (thực chất đã bị hủy) vẫn đang hiển thị. Nhân viên click chọn phiếu ID=9. | Dòng phiếu ID=9 được tô đậm. |
| 2. Click nút Hủy phiếu | Hộp thoại xác nhận hủy hiển thị. |
| 3. Click Có | Hệ thống báo lỗi: "Hủy thất bại!\nPhiếu mượn có thể đã thay đổi trạng thái hoặc xảy ra lỗi hệ thống." |

CSDL sau khi test: 
Không thay đổi (Trạng thái ID=9 vẫn là `cancelled`).
