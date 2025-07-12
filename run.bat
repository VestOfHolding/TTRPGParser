@echo off
java --module-path "target/lib" --add-modules javafx.controls,javafx.fxml -cp "target/lib/*;target/TTRPGParser-1.0-SNAPSHOT.jar" org.voh.TtrpgParserApp
pause