/*
 * Copyright (C) 2020 by Herbert Roider <herbert@roider.at>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
    //private TOOLDATATableModel model;
    private TOOLDATAVisualElement visualEditor;

    private ArrayList<String> headerlines;
    // private boolean notify_table = true;

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
                    //visualEditor.updateTable();
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
            }
            if (document == null) {
                return;
            }
            

            //if (document != null) {
            initDocument(document);
            int length = document.getLength();
           
            this.headerlines = new ArrayList<>();

            if (length > 0) {
                ArrayList<ArrayList<Double>> tools = new ArrayList<>();
                ArrayList<Double> tool;
                String text = document.getText(0, length);
                InputStream is = new ByteArrayInputStream(text.getBytes());
                List<List<String>> values = new ArrayList<>();

                BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

                String ss;
                while ((ss = br.readLine()) != null) {

                    if (ss.startsWith("%")) {
                        this.headerlines.add(ss);
                        continue;

                    }
                    if (ss.trim().length() == 0) {
                        continue;
                    }

                    Field f = this.parse_field(ss);
                    if (f == null) {
                        continue;
                    }
                    boolean found = false;
                    for (int i = 0; i < tools.size(); i++) {
                        tool = tools.get(i);
                        if (tool.get(0) == f.tool && tool.get(1) == f.edge) {
                            tool.set(f.field + 1, f.value);
                            found = true;
                            break;
                        }

                    }
                    if (!found) {
                        ArrayList<Double> nt = new ArrayList<>();
                        for (int i = 0; i < 28; i++) {
                            nt.add((double) 0);
                        }
                        nt.set(0, (double) f.tool);
                        nt.set(1, (double) f.edge);
                        nt.set(f.field + 1, f.value);
                        tools.add(nt);
                    }
                }
                for (int i = 0; i < tools.size(); i++) {
                    List<String> v = new ArrayList<>();
                    tool = tools.get(i);
                    for (int j = 0; j < tool.size(); j++) {
                        if (j < 4 || j == 26) {
                            v.add(String.valueOf(tool.get(j).intValue()));
                        } else {
                            v.add(String.valueOf(tool.get(j)));
                        }
                    }
                    values.add(v);

                }
                model.setValues(values);
            } else {

                model.setValues(new ArrayList<>());
            }

        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
        }
        /**
         * save a copy of the table model because to compare if data has
         * changed.
         *
         */
        //this.model = model.clone();
        model.fireTableStructureChanged();
    }

    public void updateFile(TOOLDATATableModel model) {
        if (model == null) {
            return;
        }

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
        int tool = -1;
        int edge = -1;

        try {
            for (int i = 0; i < model.getRowCount(); i++) {
                tool = Integer.parseInt(model.getValueAt(i, 0));
                edge = Integer.parseInt(model.getValueAt(i, 1));

                for (int j = 1; j <= 25; j++) {

                    new_text.append("$TC_DP");
                    new_text.append(String.valueOf(j));
                    new_text.append("[");
                    new_text.append(String.valueOf(tool));
                    new_text.append(",");
                    new_text.append(String.valueOf(edge));
                    new_text.append("]=");
                    if (j < 3 || j == 25) {
                        new_text.append(String.valueOf(Integer.parseInt(model.getValueAt(i, j + 1))));
                    } else {
                        new_text.append(String.valueOf(Double.parseDouble(model.getValueAt(i, j + 1))));
                    }
                    new_text.append("\n");
                }

            }
        } catch (NumberFormatException nfe) {
            //Exceptions.printStackTrace(nfe);
            JOptionPane.showMessageDialog(org.openide.windows.WindowManager.getDefault().getMainWindow(), "Error: (tool=" + tool + ") " + nfe.getMessage());
            return;

        }
        new_text.append("M17\n");
        String s = new_text.toString();
        try {

            int length = document.getLength();

            if (!document.getText(0, length).equals(s)) {
                //this.notify_table = false;
                document.replace(0, length, s, null);
                //this.notify_table = true;
                //this.model = model.clone();
            }
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
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
