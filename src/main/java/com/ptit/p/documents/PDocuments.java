package com.ptit.p.documents;

import com.ptit.p.documents.view.LibrarianHomeFrm;
import com.ptit.p.documents.view.LoginFrm;
import javax.swing.SwingUtilities;
import com.ptit.p.documents.model.User;


public class PDocuments {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            User user = new User();
            user.setId(2); // Must match a valid ID in tblUser
            user.setUsername("Librarian1");
            user.setPassword("123456");
            user.setFullName("Librarian 1");
            LibrarianHomeFrm librarianHomeFrm = new LibrarianHomeFrm(user);
            librarianHomeFrm.setVisible(true);
        });
    }
}
