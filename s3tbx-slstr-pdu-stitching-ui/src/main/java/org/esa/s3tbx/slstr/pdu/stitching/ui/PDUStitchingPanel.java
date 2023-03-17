package org.esa.s3tbx.slstr.pdu.stitching.ui;

import org.esa.s3tbx.slstr.pdu.stitching.PDUStitchingOp;
import org.esa.s3tbx.slstr.pdu.stitching.Validator;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.util.SystemUtils;
import org.esa.snap.rcp.util.Dialogs;
import org.esa.snap.ui.AppContext;
import org.esa.snap.ui.WorldMapPane;
import org.esa.snap.ui.WorldMapPaneDataModel;
import org.esa.snap.ui.product.SourceProductList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author Tonio Fincke
 */
class PDUStitchingPanel extends JPanel {

    private static final String INPUT_PRODUCT_DIR_KEY = "gpf.slstr.pdu.stitching.input.product.dir";
    private static final String INPUT_PRODUCT_LAST_FORMAT_KEY = "gpf.slstr.pdu.stitching.input.product.lastFormat";
    private static final String INPUT_PRODUCT_FORMAT_NAMES_KEY = "gpf.slstr.pdu.stitching.input.product.formatNames";
    private static final String NO_SOURCE_PRODUCTS_TEXT = "No Product Dissemination Units selected";
    private static final String VALID_SOURCE_PRODUCTS_TEXT = "Selection of Product Dissemination Units is valid";
    private static final String INVALID_SELECTION_TEXT = "Selection of Product Dissemination Units is invalid: ";

    private final AppContext appContext;
    private final PDUStitchingModel model;
    private JLabel statusLabel;
    private boolean isReactingToChange;
    private SourceProductList sourceProductList;
    private PDUBoundariesProvider boundariesProvider;
    private WorldMapPane worldMapPane;

    PDUStitchingPanel(AppContext appContext, PDUStitchingModel model) {
        this.appContext = appContext;
        this.model = model;
        isReactingToChange = false;
        appContext.getPreferences().setPropertyString(INPUT_PRODUCT_FORMAT_NAMES_KEY, "Sen3");
        setLayout(new BorderLayout());
        final JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, createSourceProductsPanel(), createWorldMapPanel());
        pane.setDividerLocation(0.35);
        pane.setResizeWeight(0.35);
        pane.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        add(pane, BorderLayout.CENTER);
        add(createTargetDirPanel(), BorderLayout.SOUTH);
    }

    private JPanel createSourceProductsPanel() {
        sourceProductList = new SourceProductList(appContext);
        sourceProductList.setPropertyNameLastOpenInputDir(INPUT_PRODUCT_DIR_KEY);
        sourceProductList.setPropertyNameLastOpenedFormat(INPUT_PRODUCT_LAST_FORMAT_KEY);
        sourceProductList.setPropertyNameFormatNames(INPUT_PRODUCT_FORMAT_NAMES_KEY);
        sourceProductList.addChangeListener(new SourceListDataListener());
        sourceProductList.addSelectionListener(new SourceListSelectionListener());
        sourceProductList.setXAxis(false);
        sourceProductList.setDefaultPattern("S3A_SL_1*.SEN3");
        sourceProductList.setProductFilter(product -> SlstrL1bFileNameValidator.isValidDirectoryName(product.getName()));
        model.getBindingContext().bind(PDUStitchingModel.PROPERTY_SOURCE_PRODUCT_PATHS, sourceProductList);
        JComponent[] panels = sourceProductList.getComponents();

        final JPanel sourceProductPanel = new JPanel(new BorderLayout());
        sourceProductPanel.add(panels[0], BorderLayout.CENTER);
        sourceProductPanel.add(panels[1], BorderLayout.EAST);

        statusLabel = new JLabel(NO_SOURCE_PRODUCTS_TEXT);
        sourceProductPanel.add(statusLabel, BorderLayout.SOUTH);
        sourceProductPanel.setBorder(BorderFactory.createTitledBorder("Product Dissemination Units"));

        return sourceProductPanel;
    }

    private JPanel createWorldMapPanel() {
        boundariesProvider = new PDUBoundariesProvider();
        final PDUBoundaryOverlay pduBoundaryOverlay = new PDUBoundaryOverlay(boundariesProvider);
        worldMapPane = new PDUWorldMapPane(new WorldMapPaneDataModel(), boundariesProvider, pduBoundaryOverlay);
        worldMapPane.setEnabled(false);
        return worldMapPane;
    }

    private JPanel createTargetDirPanel() {
        final JPanel targetDirPanel = new JPanel(new BorderLayout(2, 2));
        targetDirPanel.setBorder(BorderFactory.createTitledBorder("Target Directory"));
        final JTextField textField = new JTextField();
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                model.setPropertyValue(PDUStitchingModel.PROPERTY_TARGET_DIR, new File(textField.getText()));
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                model.setPropertyValue(PDUStitchingModel.PROPERTY_TARGET_DIR, new File(textField.getText()));
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                model.setPropertyValue(PDUStitchingModel.PROPERTY_TARGET_DIR, new File(textField.getText()));
            }
        });
        textField.setText(SystemUtils.getUserHomeDir().getPath());
        targetDirPanel.add(textField, BorderLayout.CENTER);
        final JButton etcButton = new JButton("...");
        final Dimension size = new Dimension(26, 16);
        etcButton.setPreferredSize(size);
        etcButton.setMinimumSize(size);
        etcButton.addActionListener(e -> {
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);
            File currentFile = (File) model.getPropertyValue(PDUStitchingModel.PROPERTY_TARGET_DIR);
            if (currentFile != null) {
                fileChooser.setSelectedFile(currentFile);
            }
            int i = fileChooser.showDialog(targetDirPanel, "Select");
            final File selectedFile = fileChooser.getSelectedFile();
            if (i == JFileChooser.APPROVE_OPTION && selectedFile != null) {
                textField.setText(selectedFile.getAbsolutePath());
            }
        });
        targetDirPanel.add(etcButton, BorderLayout.EAST);
        final JCheckBox openInAppCheckBox = new JCheckBox("Open in " + appContext.getApplicationName());
        openInAppCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setOpenInApp(openInAppCheckBox.isSelected());
            }
        });
        targetDirPanel.add(openInAppCheckBox, BorderLayout.SOUTH);
        return targetDirPanel;
    }

    private class SourceListSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getSource() instanceof JList) {
                final JList list = (JList) e.getSource();
                final List selectedValuesList = list.getSelectedValuesList();
                boundariesProvider.setSelected(selectedValuesList);
                worldMapPane.repaint();
            }
        }

    }

    private class SourceListDataListener implements ListDataListener {

        @Override
        public void contentsChanged(ListDataEvent event) {
            final SwingWorker worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    if (!isReactingToChange) {
                        isReactingToChange = true;
                        boundariesProvider.clear();
                        final Product[] sourceProducts = sourceProductList.getSourceProducts();
                        final String[] filePaths = (String[]) model.getPropertyValue(PDUStitchingModel.PROPERTY_SOURCE_PRODUCT_PATHS);
                        File[] productFiles = new File[sourceProducts.length];
                        for (int i = 0; i < sourceProducts.length; i++) {
                            productFiles[i] = sourceProducts[i].getFileLocation();
                        }
                        final Logger logger = Logger.getLogger(PDUStitchingPanel.class.getName());
                        final Set<File> dissolvedFilePaths = PDUStitchingOp.getSourceProductsPathFileSet(filePaths, logger);
                        File[] pathFiles = dissolvedFilePaths.toArray(new File[dissolvedFilePaths.size()]);
                        Product[] validatedProducts = new Product[0];
                        String[] validatedPaths = new String[0];
                        List<String> validatedFileNamesList = new ArrayList<>();
                        List<Product> validatedProductsList = new ArrayList<>();
                        List<String> validatedPathsList = new ArrayList<>();
                        List<File> validatedFilesList = new ArrayList<>();
                        for (int i = 0; i < productFiles.length; i++) {
                            File adjustedFile;
                            if (productFiles[i].getName().equals("xfdumanifest.xml")) {
                                adjustedFile = productFiles[i];
                            } else {
                                adjustedFile = new File(productFiles[i], "xfdumanifest.xml");
                            }
                            String adjustedFileName = adjustedFile.getAbsolutePath();
                            final String origFileName = productFiles[i].getAbsolutePath();
                            if (validatedFileNamesList.contains(adjustedFileName)) {
                                Dialogs.showInformation("Removed duplicate occurence of " + origFileName + " from selection.");
                            } else if (!SlstrL1bFileNameValidator.isValidSlstrL1BFile(productFiles[i])) {
                                Dialogs.showInformation(origFileName + " is not a valid SLSTR L1B product. Removed from selection.");
                            } else {
                                validatedFileNamesList.add(adjustedFileName);
                                validatedProductsList.add(sourceProducts[i]);
                                validatedFilesList.add(adjustedFile);
                                final boolean selected = sourceProductList.isSelected(sourceProducts[i]);
                                boundariesProvider.extractBoundaryFromFile(productFiles[i], sourceProducts[i], selected);
                            }
                        }
                        for (File file : pathFiles) {
                            File adjustedFile;
                            if (file.getName().equals("xfdumanifest.xml")) {
                                adjustedFile = file;
                            } else {
                                adjustedFile = new File(file, "xfdumanifest.xml");
                            }
                            final String adjustedFileName = adjustedFile.getAbsolutePath();
                            final String origFileName = file.getAbsolutePath();
                            if (validatedFileNamesList.contains(adjustedFileName)) {
                                Dialogs.showInformation("Removed duplicate occurence of " + origFileName + " from selection.");
                            } else if (!SlstrL1bFileNameValidator.isValidSlstrL1BFile(file)) {
                                Dialogs.showInformation(origFileName + " is not a valid SLSTR L1B product. Removed from selection.");
                            } else {
                                validatedFileNamesList.add(adjustedFileName);
                                validatedPathsList.add(origFileName);
                                validatedFilesList.add(adjustedFile);
                                final boolean selected = sourceProductList.isSelected(file);
                                boundariesProvider.extractBoundaryFromFile(file, file, selected);
                            }
                        }
                        if (validatedProductsList.size() > 0) {
                            validatedProducts = validatedProductsList.toArray(new Product[validatedProductsList.size()]);
                        }
                        if (validatedPathsList.size() > 0) {
                            validatedPaths = validatedPathsList.toArray(new String[validatedPathsList.size()]);
                        }
                        if (validatedFilesList.size() == 0) {
                            statusLabel.setForeground(Color.BLACK);
                            statusLabel.setText(NO_SOURCE_PRODUCTS_TEXT);
                            worldMapPane.setEnabled(false);
                        } else {
                            worldMapPane.setEnabled(true);
                            try {
                                Validator.validate(validatedFilesList.toArray(new File[validatedFilesList.size()]));
                                statusLabel.setForeground(Color.GREEN.darker());
                                statusLabel.setText(VALID_SOURCE_PRODUCTS_TEXT);
                            } catch (IOException e) {
                                statusLabel.setForeground(Color.RED);
                                statusLabel.setText(INVALID_SELECTION_TEXT + e.getMessage());
                            }
                        }
                        model.setPropertyValue(PDUStitchingModel.PROPERTY_SOURCE_PRODUCTS, validatedProducts);
                        if (validatedPaths.length != filePaths.length) {
                            model.setPropertyValue(PDUStitchingModel.PROPERTY_SOURCE_PRODUCT_PATHS, validatedPaths);
                        }
                        sourceProductList.bindComponents();
                        isReactingToChange = false;
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        worldMapPane.repaint();
                    } catch (Exception e) {
                        final String msg = String.format("Cannot display source product files.\n%s", e.getMessage());
                        appContext.handleError(msg, e);
                    }
                }
            };
            worker.execute();
        }

        @Override
        public void intervalAdded(ListDataEvent e) {
            contentsChanged(e);
        }

        @Override
        public void intervalRemoved(ListDataEvent e) {
            contentsChanged(e);
        }

    }

}