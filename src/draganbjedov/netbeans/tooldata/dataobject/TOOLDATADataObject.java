/**
 * modified by Herbert Roider <herbert@roider.at>
 */
package draganbjedov.netbeans.tooldata.dataobject;

import draganbjedov.netbeans.tooldata.view.TOOLDATATableModel;
import draganbjedov.netbeans.tooldata.view.TOOLDATAVisualElement;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.text.MultiViewEditorElement;
import org.netbeans.lib.editor.util.PriorityListenerList;
import org.netbeans.modules.editor.NbEditorDocument;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.UndoRedo;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.text.DataEditorSupport;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Messages({
    "LBL_TOOLDATA_LOADER=Files of TOOLDATA"
})
@MIMEResolver.ExtensionRegistration(
        displayName = "#LBL_TOOLDATA_LOADER",
        mimeType = "text/x-tooldata",
        extension = {"tooldata", "TOOLDATA"})
@DataObject.Registration(
        mimeType = "text/x-tooldata",
        iconBase = "draganbjedov/netbeans/tooldata/icons/tooldata.png",
        displayName = "#LBL_TOOLDATA_LOADER",
        position = 300)
@ActionReferences({
    @ActionReference(
            path = "Loaders/text/tooldata/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
            position = 100,
            separatorAfter = 200),
    @ActionReference(
            path = "Loaders/text/tooldata/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
            position = 300),
    @ActionReference(
            path = "Loaders/text/tooldata/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
            position = 400,
            separatorAfter = 500),
    @ActionReference(
            path = "Loaders/text/tooldata/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 600),
    @ActionReference(
            path = "Loaders/text/tooldata/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
            position = 700,
            separatorAfter = 800),
    @ActionReference(
            path = "Loaders/text/tooldata/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
            position = 900,
            separatorAfter = 1000),
    @ActionReference(
            path = "Loaders/text/tooldata/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
            position = 1100,
            separatorAfter = 1200),
    @ActionReference(
            path = "Loaders/text/tooldata/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
            position = 1300),
    @ActionReference(
            path = "Loaders/text/tooldata/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400)
})
public class TOOLDATADataObject extends MultiDataObject {

    public class Field {

        int field;
        int tool;
        int edge;
        double value;
    }
    private static final Logger LOG = Logger.getLogger(TOOLDATADataObject.class.getName());

    @MultiViewElement.Registration(
            displayName = "#LBL_TOOLDATA_EDITOR",
            iconBase = "draganbjedov/netbeans/tooldata/icons/tooldata.png",
            mimeType = "text/x-tooldata",
            persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED,
            preferredID = "TOOLDATA",
            position = 2000)
    @Messages("LBL_TOOLDATA_EDITOR=Text")
    public static MultiViewEditorElement createEditor(Lookup lkp) {
        return new MultiViewEditorElement(lkp);
    }
    private final UndoRedo.Manager undoRedoManager;
    private final DocumentListener documentListener;
    private TOOLDATATableModel model;
    private TOOLDATAVisualElement visualEditor;

    private ArrayList<String> headerlines;

    public TOOLDATADataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        undoRedoManager = new UndoRedo.Manager() {
            @Override
            public void undo() throws CannotUndoException {
                super.undo();
                updateTable();
            }

            @Override
            protected void undoTo(UndoableEdit edit) throws CannotUndoException {
                super.undoTo(edit);
                updateTable();
            }

            @Override
            public void redo() throws CannotRedoException {
                super.redo();
                updateTable();
            }

            @Override
            protected void redoTo(UndoableEdit edit) throws CannotRedoException {
                super.redoTo(edit);
                updateTable();
            }

            @Override
            public void undoOrRedo() throws CannotRedoException, CannotUndoException {
                super.undoOrRedo();
                updateTable();
            }

            private void updateTable() {
                if (visualEditor != null && visualEditor.isVisible()) {
                    visualEditor.updateTable();
                }
            }
        };
        documentListener = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateTable();
            }

            private void updateTable() {
                if (visualEditor != null && !visualEditor.isActivated()) {
                    visualEditor.updateTable();
                }
            }
        };
        registerEditor("text/x-tooldata", true);

        this.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(DataObject.PROP_MODIFIED) && ((Boolean) evt.getNewValue())) {
                    initDocument();
                }
            }
        });
    }

    @Override
    protected int associateLookup() {
        return 1;
    }

    @SuppressWarnings({"null", "ConstantConditions"})
    public void readFile(TOOLDATATableModel model) {
        try {
            Lookup lookup = getCookieSet().getLookup();
            DataEditorSupport dataEditorSupport = lookup.lookup(DataEditorSupport.class);
            NbEditorDocument document = null;
            if (dataEditorSupport.isDocumentLoaded()) {
                document = (NbEditorDocument) dataEditorSupport.getDocument();
            } else {
                try {
                    document = (NbEditorDocument) dataEditorSupport.openDocument();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                //<editor-fold defaultstate="collapsed" desc="comment">
                //                File file = FileUtil.toFile(this.getPrimaryFile());
                //                if (file.length() != 0) {
                //                    try {
                //                        List<String> s = this.getPrimaryFile().asLines();
                //                        boolean first = true;
                //                        List<List<String>> values = new ArrayList<List<String>>(s.size());
                //                        for (String ss : s) {
                //                            if (first) {
                //                                String[] split = ss.split(",");
                //                                ArrayList<String> headers = new ArrayList<String>(split.length);
                //                                Collections.addAll(headers, split);
                //                                tableModel.setHeaders(headers);
                //                                first = false;
                //                                continue;
                //                            }
                //                            String[] split = ss.split(",");
                //                            ArrayList<String> rowData = new ArrayList<String>(split.length);
                //                            Collections.addAll(rowData, split);
                //                            values.add(rowData);
                //                        }
                //                        tableModel.setValues(values);
                //                    } catch (IOException ex) {
                //                        Exceptions.printStackTrace(ex);
                //                    }
                //                } else {
                //                    tableModel.setHeaders(new ArrayList<String>());
                //                    tableModel.setValues(new ArrayList<List<String>>());
                //                }
                //</editor-fold>
            }
            if (document != null) {
                initDocument(document);
                int length = document.getLength();

                List<String> header = new ArrayList<>();
                header.add("Channel");
                header.add("T");
                header.add("D");
                header.add("Typ"); // $TC_DP1
                header.add("Edge pos");
                header.add("L1");
                header.add("L2");
                header.add("L3");
                header.add("R1");
                header.add("R2");
                header.add("8");
                header.add("9");
                header.add("10");
                header.add("11");
                header.add("L1 Verschl.");
                header.add("L2 Verschl.");
                header.add("L3 Verschl.");
                header.add("R1 Verschl.");
                header.add("R2 Verschl.");
                header.add("17");
                header.add("18");
                header.add("19");
                header.add("20");
                header.add("Base L1");
                header.add("Base L2");
                header.add("Base L3");
                header.add("Tool clearance angle");
                header.add("Use of tool inverse");

                model.setHeaders(header);
                this.headerlines = new ArrayList<>();

                if (length > 0) {
                    //ArrayList<Double> wkz = new ArrayList<>();
                    ArrayList<ArrayList<Double>> tools = new ArrayList<ArrayList<Double>>();
                    ArrayList<Double> tool;
                    String text = document.getText(0, length);
                    InputStream is = new ByteArrayInputStream(text.getBytes());
                    //String[] s = text.split("\n");
                    //boolean first = true;
                    List<List<String>> values = new ArrayList<>();

                    int T = -1;
                    int D = -1;
                    int field = -1;
                    int channel = -1;

                    BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

                    String ss;
                    while ((ss = br.readLine()) != null) {

                        if (ss.startsWith("CHANDATA(") && ss.endsWith(")")) {
                            String s_channel;
                            s_channel = ss.substring(ss.indexOf("(") + 1, ss.indexOf(")"));
                            channel = Integer.parseInt(s_channel);
                            continue;

                        }

                        // Save the first lines until "CHANDATA"
                        if (channel < 0) {
                            if (ss.trim().length() > 0) {
                                this.headerlines.add(ss);
                            }
                            continue;
                        }

                        Field f = this.parse_field(ss);
                        if (f == null) {
                            continue;
                        }
                        boolean found = false;
                        for (int i = 0; i < tools.size(); i++) {
                            tool = tools.get(i);
                            if (tool.get(0) == channel && tool.get(1) == f.tool && tool.get(2) == f.edge) {
                                tool.set(f.field + 2, f.value);
                                found = true;
                                break;
                            }

                        }
                        if (!found) {
                            ArrayList<Double> nt = new ArrayList<>();
                            for (int i = 0; i < 28; i++) {
                                nt.add((double) 0);
                            }
                            nt.set(0, (double) channel);
                            nt.set(1, (double) f.tool);
                            nt.set(2, (double) f.edge);
                            nt.set(f.field + 2, f.value);
                            tools.add(nt);
                        }

                        //values.add(splitLine(ss, separator, escapeChar));
                    }
                    for (int i = 0; i < tools.size(); i++) {
                        List<String> v = new ArrayList<String>();
                        tool = tools.get(i);
                        for (int j = 0; j < tool.size(); j++) {
                            if (j < 5 || j == 27) {
                                v.add(String.valueOf(tool.get(j).intValue()));
                            } else {
                                v.add(String.valueOf(tool.get(j)));
                            }
                        }
                        values.add(v);

                    }
                    model.setValues(values);
                } else {

                    model.setValues(new ArrayList<List<String>>());
                }
            }
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
        }
        this.model = model.clone();
        model.fireTableStructureChanged();
    }

    public void updateFile(TOOLDATATableModel model) {
        if (!this.model.equals(model)) {
            Lookup lookup = getCookieSet().getLookup();
            DataEditorSupport dataEditorSupport = lookup.lookup(DataEditorSupport.class);
            NbEditorDocument document;
            if (dataEditorSupport.isDocumentLoaded()) {
                document = (NbEditorDocument) dataEditorSupport.getDocument();
            } else {
                try {
                    document = (NbEditorDocument) dataEditorSupport.openDocument();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                    //TODO Unable to open file
                    return;
                }
            }

            initDocument(document);
            StringBuilder new_text = new StringBuilder();
            //String new_text = new String();
            for (int i = 0; i < this.headerlines.size(); i++) {
                new_text.append(this.headerlines.get(i));
                new_text.append("\n");
            }
            new_text.append("\n");
            int channel = -1;
            int tool = -1;
            int edge = -1;

            try {
                for (int i = 0; i < model.getRowCount(); i++) {
                    int new_channel = Integer.parseInt(model.getValueAt(i, 0));
                    if (channel != new_channel) {
                        new_text.append("CHANDATA(" + String.valueOf(new_channel) + ")\n");
                        channel = new_channel;
                    }
                    tool = Integer.parseInt(model.getValueAt(i, 1));
                    edge = Integer.parseInt(model.getValueAt(i, 2));

                    for (int j = 1; j <= 25; j++) {

                        new_text.append("$TC_DP" + String.valueOf(j));
                        new_text.append("[" + String.valueOf(tool) + "," + String.valueOf(edge) + "]=");
                        if (j < 3 || j == 25) {
                            new_text.append(String.valueOf(Integer.parseInt(model.getValueAt(i, j + 2))));
                        } else {
                            new_text.append(String.valueOf(Double.parseDouble(model.getValueAt(i, j + 2))));
                        }
                        new_text.append("\n");
                    }

                }
            } catch (NumberFormatException nfe) {
                //Exceptions.printStackTrace(nfe);
                JOptionPane.showMessageDialog(org.openide.windows.WindowManager.getDefault().getMainWindow(), "Error: " + nfe.getMessage());
                return;

            }
            new_text.append("M17\n");
            String s = new_text.toString();
            try {

                int length = document.getLength();

                if (!document.getText(0, length).equals(s)) {
                    document.replace(0, length, s, null);
                    this.model = model.clone();
                }
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public UndoRedo.Manager getUndoRedoManager() {
        return undoRedoManager;
    }

    public void setVisualEditor(TOOLDATAVisualElement visualEditor) {
        this.visualEditor = visualEditor;
    }

    private void initDocument(NbEditorDocument document) {
        UndoableEditListener[] undoableEditListeners = document.getUndoableEditListeners();
        boolean found = false;
        if (undoableEditListeners.length > 0) {
            for (UndoableEditListener uel : undoableEditListeners) {
                if (uel.equals(undoRedoManager)) {
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            document.addUndoableEditListener(undoRedoManager);
        }

        DocumentListener[] documentListeners = document.getDocumentListeners();
        found = false;
        if (documentListeners.length > 0) {
            loopDocumentListener:
            for (DocumentListener dl : documentListeners) {
                if (dl.equals(documentListener)) {
                    found = true;
                    break;
                } else if (dl instanceof PriorityListenerList) {
                    PriorityListenerList pll = (PriorityListenerList) dl;
                    EventListener[][] listenersArray = pll.getListenersArray();
                    for (EventListener[] row : listenersArray) {
                        for (EventListener el : row) {
                            if (el.equals(documentListener)) {
                                found = true;
                                break loopDocumentListener;
                            }
                        }
                    }
                }
            }
        }
        if (!found) {
            document.addDocumentListener(documentListener);
        }
    }

    /**
     * Init document listeners
     */
    public void initDocument() {
        Lookup lookup = getCookieSet().getLookup();
        DataEditorSupport dataEditorSupport = lookup.lookup(DataEditorSupport.class);
        NbEditorDocument document = null;
        if (dataEditorSupport.isDocumentLoaded()) {
            document = (NbEditorDocument) dataEditorSupport.getDocument();
        } else {
            try {
                document = (NbEditorDocument) dataEditorSupport.openDocument();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        if (document != null) {
            initDocument(document);
        }
    }

    private Field parse_field(String line) {
        Field field = new Field();
        Pattern p = Pattern.compile("\\$TC_DP([0-9]+)(\\[)([0-9]+)(,)([0-9]+)(\\]=)(-)?\\d+(\\.\\d+)?");

        Matcher m = p.matcher(line);
        if (!m.matches()) {
            //System.out.println(" matches ");
            return null;
        }

        String s = line.substring(6, line.indexOf("["));
        field.field = Integer.parseInt(s);
        s = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
        String[] indexes = s.split(",");
        field.tool = Integer.parseInt(indexes[0]);
        field.edge = Integer.parseInt(indexes[1]);
        s = line.substring(line.indexOf("=") + 1);
        field.value = Double.parseDouble(s);

        return field;

    }
}
