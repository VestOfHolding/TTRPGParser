package org.voh;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.voh.domain.PublisherConfig;
import org.voh.domain.SupportedSystems;
import org.voh.postprocess.PdfGeneralPostProcessor;
import org.voh.postprocess.PlainInputPostProcessor;
import org.voh.postprocess.Dnd5ePostProcessor;
import org.voh.postprocess.pf2e.Pf2ePostProcessor;
import org.voh.stripper.RegionalPdfHtmlStripper;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainAppController {
    private static final String TAG_PATTERN = "(</?\\w+>)";
    private static final String ATTR_NAME_PATTERN = "(\\w+)(?=\\=)";
    private static final String ATTR_VALUE_PATTERN = "=\"[^\"]*\"";
    private static final Pattern HTML_PATTERN = Pattern.compile(
            "(?<TAG>" + TAG_PATTERN + ")"
                    + "|(?<ANAME>" + ATTR_NAME_PATTERN + ")"
                    + "|(?<AVAL>" + ATTR_VALUE_PATTERN+ ")"
    );

    @FXML
    public SplitPane mainAppWindow;

    @FXML
    public Button browseButton;
    @FXML
    public Label pdfFileNameLabel;
    @FXML
    public TextField startPageBox;
    @FXML
    public TextField endPageBox;
    @FXML
    public Button processPDFButton;

    @FXML
    public ComboBox<String> systemDropdown;
    @FXML
    public ComboBox<String> publisherDropdown;

    @FXML
    public Button parseInputButton;
    @FXML
    public CodeArea inputBox;

    @FXML
    public CodeArea outputBox;
    @FXML
    public Button outputCopyButton;

    private File pdfFile = null;

    @FXML
    public void onBrowseClicked(ActionEvent actionEvent) {
        FileChooser chooser = createPDFChooser();

        File file = chooser.showOpenDialog(browseButton.getScene().getWindow());
        if (file == null) {
            return;
        }
        pdfFile = file;
        pdfFileNameLabel.setText(file.getName());

        processPDFButton.setDisable(false);
        prepopulatePageBoxes();
    }

    private FileChooser createPDFChooser() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open PDF File");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf")
        );
        chooser.setSelectedExtensionFilter(chooser.getExtensionFilters().getFirst());

        return chooser;
    }

    private void prepopulatePageBoxes() {
        try (PDDocument doc = Loader.loadPDF(pdfFile)) {
            int numPages = doc.getNumberOfPages();
            startPageBox.setText("1");
            endPageBox  .setText(Integer.toString(numPages));
        } catch (IOException ioe) {
            // handle error: maybe show an alert
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Unable to read PDF: " + ioe.getMessage(),
                    ButtonType.OK
            );
            alert.initOwner(browseButton.getScene().getWindow());
            alert.showAndWait();
        }
    }

    @FXML
    public void onProcessPDFClicked(ActionEvent actionEvent) throws IOException {
        int startPage = Integer.parseInt(startPageBox.getText());
        int endPage = Integer.parseInt(endPageBox.getText());

        String text = RegionalPdfHtmlStripper.readTwoColumnStyled(
                pdfFile, startPage, endPage, PublisherConfig.getFromName(publisherDropdown.getValue()));
        text = PdfGeneralPostProcessor.fullProcess(text);

        text = trySystemParsing(text);
        outputBox.replaceText(text);
    }

    @FXML
    public void onParseInputClicked(ActionEvent actionEvent) {
        String inputContent = PlainInputPostProcessor.parseInput(inputBox.getText());

        inputContent = trySystemParsing(inputContent);
        outputBox.replaceText(inputContent);
    }

    private String trySystemParsing(String text) {
        SupportedSystems system = SupportedSystems.fromDisplayName(systemDropdown.getValue());
        switch (system) {
            case DND_5E -> text = Dnd5ePostProcessor.postProcess(text);
            case PF_2E -> text = Pf2ePostProcessor.postProcess(text);
        }
        return text;
    }

    @FXML
    public void onOutputCopyClicked(ActionEvent actionEvent) {
        outputBox.selectAll();
        outputBox.copy();
        outputBox.deselect();
    }

    @FXML
    public void initialize() {
        systemDropdown.setItems(FXCollections.observableArrayList(SupportedSystems.displayNames()));
        systemDropdown.getSelectionModel().select(SupportedSystems.defaultDisplayName());

        publisherDropdown.setItems(FXCollections.observableArrayList(PublisherConfig.displayNames()));
        publisherDropdown.getSelectionModel().select(PublisherConfig.defaultDisplayName());

        attachSubscription(inputBox);
        attachSubscription(outputBox);
    }

    private void attachSubscription(CodeArea codeArea) {
        codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .successionEnds(Duration.ofMillis(300))
                .subscribe(ch -> codeArea.setStyleSpans(
                        0, computeHighlighting(codeArea.getText())));
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = HTML_PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        while(matcher.find()) {
            String styleClass =
                    matcher.group("TAG")  != null ? "tag" :
                            matcher.group("ANAME")!= null ? "attr-name" :
                                    matcher.group("AVAL") != null ? "attr-value" :
                                            null;

            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }


}
