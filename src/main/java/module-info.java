module org.ecommerce {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.ecommerce to javafx.fxml;
    exports org.ecommerce;
}