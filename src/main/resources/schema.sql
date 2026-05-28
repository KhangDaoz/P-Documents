SET FOREIGN_KEY_CHECKS = 0;
DROP DATABASE IF EXISTS P_Documents;
CREATE DATABASE P_Documents;
USE P_Documents;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE tblUser (
    ID INT(10) NOT NULL AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    fullName VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE tblStudent (
    ID VARCHAR(255) NOT NULL,
    fullName VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE tblBook (
    ISBN INT(10) NOT NULL,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    genre VARCHAR(255) NOT NULL,
    pulisher VARCHAR(255) NOT NULL,
    pulishYear INT(10) NOT NULL,
    price FLOAT NOT NULL,
    description VARCHAR(255) NOT NULL,
    availableCopies INT(10) NOT NULL,
    PRIMARY KEY (ISBN)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE tblBookItem (
    ID INT(10) NOT NULL AUTO_INCREMENT,
    status VARCHAR(255) NOT NULL,
    tblBookISBN INT(10) NOT NULL,
    PRIMARY KEY (ID),
    FOREIGN KEY (tblBookISBN) REFERENCES tblBook(ISBN) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE tblBorrowing (
    ID INT(10) NOT NULL AUTO_INCREMENT,
    borrowDate DATE NOT NULL,
    expectedReceiveDate DATE NOT NULL,
    actualReceiveDate DATE NULL,
    status VARCHAR(255) NOT NULL,
    tblStudentID VARCHAR(255) NOT NULL,
    tblUserID INT(10) NOT NULL,
    PRIMARY KEY (ID),
    FOREIGN KEY (tblStudentID) REFERENCES tblStudent(ID) ON DELETE CASCADE,
    FOREIGN KEY (tblUserID) REFERENCES tblUser(ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE tblBorrowedBook (
    ID INT(10) NOT NULL AUTO_INCREMENT,
    expectedReturnDate DATE NOT NULL,
    actualReturnDate DATE NULL,
    status VARCHAR(255) NOT NULL,
    note VARCHAR(255),
    price INT(10) NOT NULL,
    tblBookItemID INT(10) NOT NULL,
    tblBorrowingID INT(10) NOT NULL,
    PRIMARY KEY (ID),
    FOREIGN KEY (tblBookItemID) REFERENCES tblBookItem(ID) ON DELETE CASCADE,
    FOREIGN KEY (tblBorrowingID) REFERENCES tblBorrowing(ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE tblBill (
    ID INT(10) NOT NULL AUTO_INCREMENT,
    bookStatus VARCHAR(255) NOT NULL,
    paymentDate DATE NOT NULL,
    overdueDay INT(10) NOT NULL,
    fine INT(10),
    amount INT(10) NOT NULL,
    paymentType VARCHAR(255) NOT NULL,
    note VARCHAR(255),
    tblBorrowingID INT(10) NOT NULL,
    tblUserID INT(10) NOT NULL,
    PRIMARY KEY (ID),
    FOREIGN KEY (tblBorrowingID) REFERENCES tblBorrowing(ID) ON DELETE CASCADE,
    FOREIGN KEY (tblUserID) REFERENCES tblUser(ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Fine (
    ID INT(10) NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    fineRate FLOAT NOT NULL,
    description VARCHAR(255) NOT NULL,
    PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE tblBorrowedBookFine (
    ID INT(10) NOT NULL AUTO_INCREMENT,
    fineRate FLOAT NOT NULL,
    totalFine FLOAT NOT NULL,
    tblBorrowedBookID INT(10) NOT NULL,
    FineID INT(10) NOT NULL,
    PRIMARY KEY (ID),
    FOREIGN KEY (tblBorrowedBookID) REFERENCES tblBorrowedBook(ID) ON DELETE CASCADE,
    FOREIGN KEY (FineID) REFERENCES Fine(ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


USE P_Documents;

INSERT INTO tblUser (username, password, fullName, phone, role) VALUES  
('admin', '123', 'System Administrator', '0123456789', 'admin'),
('thangnt', 't123', 'Nguyen Tien Thang', '0987654321', 'manager'),
('lanpt', 'l234', 'Pham Thi Lan', '0977777777', 'librarian'),
('minhbq', 'm123', 'Bui Quang Minh', '0944444444', 'librarian'),
('truongnv', 't456', 'Nguyen Van Truong', '0912345678', 'librarian'),
('dungnt', 'd789', 'Nguyen Tuan Dung', '0909090909', 'librarian'),
('namlh', 'n012', 'Le Hoang Nam', '0988888888', 'librarian'),
('tuanva', 't567', 'Vu Anh Tuan', '0966666666', 'librarian'),
('huongnt', 'h890', 'Ngo Thu Huong', '0955555555', 'librarian'),
('quangdt', 'q234', 'Dang Tuan Quang', '0933333333', 'librarian');
