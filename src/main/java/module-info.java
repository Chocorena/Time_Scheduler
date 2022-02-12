module G13 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.java;
    requires commons.validator;
    requires java.desktop;
    requires java.mail;
    requires itextpdf;


    opens G13 to javafx.fxml;
    exports G13;
    exports G13.database;
    opens G13.database to javafx.fxml;
    exports G13.methods;
    opens G13.methods to javafx.fxml;
    exports G13.security;
    opens G13.security to javafx.fxml;
}