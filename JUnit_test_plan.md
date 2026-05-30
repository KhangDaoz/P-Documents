Kiểm thử đơn vị - JUnit test
a. Lập kế hoạch test
Các trường hợp phải test cho modul Quản lý sách (Thêm, sửa, xóa sách):
TT | Chức năng | Lớp DAO | Hàm/phương thức | Các trường hợp cần kiểm thử
--- | --- | --- | --- | ---
1 | Tìm kiếm sách | BookDAO | searchBook() | Tìm sách với từ khóa tồn tại
2 | Tìm kiếm sách | BookDAO | searchBook() | Tìm sách với từ khóa không tồn tại
3 | Thêm thông tin sách | BookDAO | addBook() | Thêm sách với ISBN chưa có trong CSDL
4 | Thêm thông tin sách | BookDAO | addBook() | Thêm sách với ISBN đã tồn tại trong CSDL
5 | Sửa thông tin sách | BookDAO | updateBook() | Sửa sách đã có trong CSDL
6 | Sửa thông tin sách | BookDAO | updateBook() | Sửa sách chưa có trong CSDL
7 | Xóa thông tin sách | BookDAO | deleteBook() | Xóa sách tồn tại trong CSDL
8 | Xóa thông tin sách | BookDAO | deleteBook() | Xóa sách không tồn tại trong CSDL
9 | Thêm bản sách vật lý | BookItemDAO | addBookItem() | Thêm bản sách hợp lệ cho một ISBN
10 | Thêm bản sách vật lý | BookItemDAO | addBookItem() | Thêm bản sách thất bại do thiếu hoặc sai ISBN
11 | Xóa bản sách vật lý | BookItemDAO | deleteBookItem() | Xóa thành công các bản sách của một ISBN
12 | Xóa bản sách vật lý | BookItemDAO | deleteBookItem() | Xóa thất bại khi tham số ISBN không đúng

b. Triển khai mã kiểm thử (12 trường hợp)
+ Lớp BookDaoTest.java:
```java
package test.unit;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import com.ptit.p.documents.dao.DAO;
import com.ptit.p.documents.dao.BookDAO;
import com.ptit.p.documents.model.Book;

public class BookDaoTest {
    BookDAO bd = new BookDAO();

    @Test
    public void testSearchBookException(){
        String key = "xxxxxxxxxx";
        List<Book> listBook = bd.searchBook(key);
        Assert.assertNotNull(listBook);
        Assert.assertEquals(0, listBook.size());
        return;
    }

    @Test
    public void testSearchBookStandard(){
        String key = "Java";
        List<Book> listBook = bd.searchBook(key);
        Assert.assertNotNull(listBook);
        Assert.assertTrue(listBook.size() > 0);
        for(int i=0; i<listBook.size(); i++){
            boolean match = listBook.get(i).getTitle().toLowerCase().contains(key.toLowerCase()) || 
                            listBook.get(i).getAuthor().toLowerCase().contains(key.toLowerCase()) ||
                            listBook.get(i).getISBN().toLowerCase().contains(key.toLowerCase());
            Assert.assertTrue(match);
        }
        return;
    }

    @Test
    public void testUpdateBook(){
        Connection con = bd.getConnection();
        String newTitle = "Java Core Updated";
        double newPrice = 250000;
        String key = "ISBN001";
        try{
            con.setAutoCommit(false);
            List<Book> lb = bd.searchBook(key);
            lb.get(0).setTitle(newTitle);
            lb.get(0).setPrice(newPrice);
            bd.updateBook(lb.get(0));
            
            //test the new updated row
            lb.clear();
            lb = bd.searchBook(key);
            Assert.assertEquals(newTitle, lb.get(0).getTitle());
            Assert.assertEquals(newPrice, lb.get(0).getPrice(), 0.000001);
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            try{
                con.rollback();
                con.setAutoCommit(true);
            } catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return;
    }

    @Test
    public void testUpdateBookException() {
        Connection con = bd.getConnection();
        Book book = new Book();
        book.setISBN("ISBN_NOT_EXIST");
        book.setTitle("Sách Cập Nhật Ảo");
        try {
            con.setAutoCommit(false);
            boolean result = bd.updateBook(book);
            Assert.assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.rollback();
                con.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Test
    public void testAddBookStandard() {
        Connection con = bd.getConnection();
        Book book = new Book();
        book.setISBN("ISBN999");
        book.setTitle("Sách Mới Thêm");
        book.setAuthor("Tác Giả A");
        book.setPrice(100000);
        try {
            con.setAutoCommit(false);
            boolean result = bd.addBook(book);
            Assert.assertTrue(result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.rollback();
                con.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Test
    public void testAddBookException() {
        Connection con = bd.getConnection();
        Book book = new Book();
        book.setISBN("ISBN001"); // Đã tồn tại
        book.setTitle("Sách Trùng ISBN");
        try {
            con.setAutoCommit(false);
            boolean result = bd.addBook(book);
            Assert.assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.rollback();
                con.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Test
    public void testDeleteBookStandard() {
        Connection con = bd.getConnection();
        String isbn = "ISBN001";
        try {
            con.setAutoCommit(false);
            boolean result = bd.deleteBook(isbn);
            Assert.assertTrue(result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.rollback();
                con.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Test
    public void testDeleteBookException() {
        Connection con = bd.getConnection();
        String isbn = "ISBN_NOT_EXIST";
        try {
            con.setAutoCommit(false);
            boolean result = bd.deleteBook(isbn);
            Assert.assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.rollback();
                con.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
```

+ Lớp BookItemDaoTest.java:
```java
package test.unit;
import java.sql.Connection;
import org.junit.Assert;
import org.junit.Test;
import com.ptit.p.documents.dao.BookItemDAO;
import com.ptit.p.documents.model.BookItem;

public class BookItemDaoTest {
    BookItemDAO bid = new BookItemDAO();

    @Test
    public void testAddBookItemStandard(){
        Connection con = bid.getConnection();
        BookItem item = new BookItem();
        item.setBookISBN("ISBN001"); // Giả sử ISBN001 đã có trong bảng tblBook
        item.setStatus("good");
        
        try {
            con.setAutoCommit(false);
            boolean result = bid.addBookItem(item);
            Assert.assertTrue(result);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Ném ra ngoại lệ khi thêm BookItem");
        } finally {
            try {
                con.rollback(); // Rollback để không lưu vào CSDL thật
                con.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Test
    public void testAddBookItemException(){
        Connection con = bid.getConnection();
        BookItem item = new BookItem();
        item.setBookISBN("ISBN_NOT_EXIST"); // Mã sách không có thật
        item.setStatus("good");
        
        try {
            con.setAutoCommit(false);
            boolean result = bid.addBookItem(item);
            // Sẽ trả về false vì vi phạm khóa ngoại (Foreign key)
            Assert.assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.rollback();
                con.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Test
    public void testDeleteBookItemStandard() {
        Connection con = bid.getConnection();
        String isbn = "ISBN001";
        try {
            con.setAutoCommit(false);
            boolean result = bid.deleteBookItem(isbn);
            Assert.assertTrue(result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.rollback();
                con.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Test
    public void testDeleteBookItemException() {
        Connection con = bid.getConnection();
        String isbn = "ISBN_NOT_EXIST";
        try {
            con.setAutoCommit(false);
            boolean result = bid.deleteBookItem(isbn);
            Assert.assertFalse(result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.rollback();
                con.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
```

Kiểm thử chức năng - Blackbox test
a. Lập kế hoạch test
Các trường hợp phải test cho modul Quản lý sách:
TT | Chức năng | Các trường hợp cần kiểm thử
--- | --- | ---
1 | Thêm thông tin sách | Thêm một sách với mã ISBN chưa có trong CSDL
2 | Thêm thông tin sách | Thêm một sách với mã ISBN đã tồn tại trong CSDL
3 | Thêm thông tin sách | Thêm sách nhưng bỏ trống các trường bắt buộc
4 | Sửa thông tin sách | Sửa một sách đã có trong CSDL
5 | Sửa thông tin sách | Sửa liên tiếp hai lần cùng một sách đã có trong CSDL
6 | Sửa thông tin sách | Để trống thông tin bắt buộc khi lưu sửa đổi
7 | Xóa thông tin sách | Xóa thành công một sách (khi xác nhận xóa)
8 | Xóa thông tin sách | Hủy thao tác xóa khi popup hiển thị hỏi xác nhận
9 | Tìm kiếm sách | Nhập đúng từ khóa sách tồn tại
10 | Tìm kiếm sách | Nhập từ khóa sách không tồn tại

b. Các test case cho chức năng sửa thông tin sách
+ Test case 1: sửa một sách đã có trong CSDL (test case chuẩn)
CSDL trước khi test:
- tblUser:
id | name | username | password | position
1 | Manager | manager | manager | manager 
2 | Librarian | lib | lib | librarian

- tblBook:
ISBN | title | author | genre | publisher | publishYear | price | description
ISBN001 | Java Cơ Bản | Nguyễn Văn A | IT | NXB IT | 2020 | 150000 | Sách cơ bản
ISBN002 | Lập trình C | Trần Văn B | IT | NXB GD | 2019 | 120000 | Sách cho người mới

Các bước thực hiện | Kết quả mong đợi
--- | ---
1. Khởi tạo phần mềm | Giao diện đăng nhập hiện ra, có ô nhập username, password và nút đăng nhập
2. Nhập username = manager<br>password = manager<br>Click đăng nhập | Giao diện trang chủ nhân viên quản lý hiện ra. Có nút:<br>- Quản lý thông tin sách<br>- Quản lý bạn đọc<br>- ...
3. Click nút Quản lý thông tin sách | Giao diện quản lý sách hiện ra (BookManageFrm). Có 3 nút chọn:<br>- Thêm thông tin sách<br>- Sửa thông tin sách<br>- Xóa thông tin sách
4. Click nút Sửa thông tin sách | Giao diện tìm sách để sửa hiện lên (SearchBookFrm). Có ô nhập từ khóa, nút tìm kiếm.
5. Nhập từ khóa = "Java" và click vào nút tìm kiếm | Kết quả có 1 sách hiện lên:<br>ISBN: ISBN001<br>title: Java Cơ Bản<br>author: Nguyễn Văn A<br>...
6. Click vào dòng của sách "Java Cơ Bản" | Giao diện hiện lên thông tin sách với các ô nhập liệu được điền sẵn thông tin cũ. Ô ISBN bị mờ (không sửa được) và có nút Lưu.
7. Sửa thông tin giá sách (price) = 200000 và click vào nút Lưu | Thông báo hiện lên: Cập nhật thông tin sách thành công!
8. Click vào nút OK của thông báo | Quay về giao diện chính của quản lý thông tin sách (hoặc trang tìm kiếm)

CSDL sau khi test:
- Bảng tblUser giữ nguyên, chỉ có thay đổi ở bảng tblBook (giá tiền của ISBN001 cập nhật thành 200000):
ISBN | title | author | genre | publisher | publishYear | price | description
ISBN001 | Java Cơ Bản | Nguyễn Văn A | IT | NXB IT | 2020 | 200000 | Sách cơ bản
ISBN002 | Lập trình C | Trần Văn B | IT | NXB GD | 2019 | 120000 | Sách cho người mới