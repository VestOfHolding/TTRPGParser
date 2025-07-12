# TTRPG PDF Text Parser

A Java 21 + JavaFX desktop application that extracts and processes rich text from multi-column PDFs.

This is primarily designed as an aid for converting tabletop RPG PDFs into HTML suitable for Foundry VTT. Including enricher text for supported systems.

### Supported Systems
* Generic text with no enrichers.
* D&D 5e
* Pathfinder 2e

---

## Prerequisites

- **JDK 21**
- **Maven 3.8+**

---

## Running in IntelliJ IDEA

1. **Import the project**
    - **File → New → Project from Existing Sources…**
    - Select the folder containing `pom.xml`, choose **Maven**.

2. **Configure the Project SDK**
    - **File → Project Structure → Project**
    - Set **Project SDK** to your installed Java 21.

3. **Run the application**
    - Open the **Maven** tool window (usually on the right).
    - Expand **Plugins → javafx → javafx:run**.
    - Double-click or right-click **javafx:run → Run**.
    - The app will compile, start a JavaFX stage and load `TtrpgParserApp`.

4. **Debug**
    - In `pom.xml`, under the `javafx-maven-plugin` add:
      ```xml
      <options>
        <option>
          -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005
        </option>
      </options>
      ```  
    - Run **javafx:run** in debug mode, then attach IntelliJ’s Remote Debugger to port 5005.

---

## Command-Line Usage

### 1. Run without packaging

```bash
mvn clean package
```

Then run either `run.bat` if you're on Windows or `run.sh` if you're on Mac or Linux.
