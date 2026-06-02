

Ung dung quan ly thu vien viet bang Java (Maven).

```
P-Documents/
|-- pom.xml
|-- README.md
|-- src/
|   |-- main/
|   |   |-- java/
|   |   |   `-- com/ptit/p/documents/
|   |   |       |-- PDocuments.java     # diem vao chuong trinh
|   |   |       |-- model/              # lop du lieu (Book, User, Borrowing...)
|   |   |       |-- dao/                # truy xuat du lieu
|   |   |       `-- view/               # hien thi giao dien (console/view class)
|   `-- test/
|       `-- java/
`-- target/                             # thu muc sinh ra khi build
```

- model: chi chua entity, DTO, POJO
- dao: chi chua CRUD va truy van du lieu
- view: chi chua logic hien thi, khong chua nghiep vu
- (de mo rong) service: nghiep vu va transaction
- (de mo rong) controller: nhan request, goi service, dieu huong view

Khi mo rong thanh web app MVC, nen bo sung:

```
src/main/java/com/ptit/p/documents/
|-- controller/
|-- service/
|-- dao/
|`-- model/

src/main/resources/
|-- templates/
|   |-- book/
|   |-- user/
|   |-- borrowing/
|   `-- stat/
`-- static/
    |-- css/
    |-- js/
    `-- images/
```

```
mvn clean compile
mvn exec:java -Dexec.mainClass="com.ptit.p.documents.PDocuments"
```
