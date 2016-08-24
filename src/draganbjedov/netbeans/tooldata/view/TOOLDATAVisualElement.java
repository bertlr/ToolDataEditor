/**
 * modified by Herbert Roider <herbert@roider.at>
 */
package draganbjedov.netbeans.tooldata.view;

//import draganbjedov.netbeans.tooldata.Bundle;
import draganbjedov.netbeans.tooldata.dataobject.TOOLDATADataObject;
import draganbjedov.netbeans.tooldata.view.ccp.TableRowTransferable;
import draganbjedov.netbeans.tooldata.view.ccp.TableTransferHandler;
import draganbjedov.netbeans.tooldata.view.ccp.TransferActionListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.EventObject;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;
import static org.jdesktop.swingx.JXTable.USE_DTCR_COLORMEMORY_HACK;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.ToolbarWithOverflow;
import org.openide.awt.UndoRedo;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;

@MultiViewElement.Registration(
        displayName = "#LBL_TOOLDATA_VISUAL",
        iconBase = "draganbjedov/netbeans/tooldata/icons/tooldata.png",
        mimeType = "text/x-tooldata",
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        preferredID = "TooldataVisual",
        position = 3000)
@Messages("LBL_TOOLDATA_VISUAL=Table")
public final class TOOLDATAVisualElement extends JPanel implements MultiViewElement, TableModelListener {

    private static final Clipboard CLIPBOARD = Toolkit.getDefaultToolkit().getSystemClipboard();

    private final TOOLDATADataObject obj;
    private final JToolBar toolbar = new ToolbarWithOverflow();

    private transient MultiViewElementCallback callback;

    private final transient TOOLDATATableModel tableModel;
    private boolean activated;

    private AbstractAction addRowAction;
    private AbstractAction deleteRowAction;

    private AbstractAction moveTopAction;
    private AbstractAction moveUpAction;
    private AbstractAction moveDownAction;
    private AbstractAction moveBottomAction;

    private AbstractAction cutAction;
    private AbstractAction copyAction;
    private AbstractAction pasteAction;

    private final Lookup lookup;
    private final InstanceContent instanceContent;

    @SuppressWarnings("LeakingThisInConstructor")
    public TOOLDATAVisualElement(Lookup objLookup) {
        obj = objLookup.lookup(TOOLDATADataObject.class);
        assert obj != null;
        instanceContent = new InstanceContent();
        lookup = new ProxyLookup(objLookup, new AbstractLookup(instanceContent));

        tableModel = new TOOLDATATableModel();

        initActions();
        initComponents();
        init();
        createToolBar();

        obj.setVisualEditor(this);

        //updateTable();
    }

    @Override
    public String getName() {
        return "TOOLDATAVisualElement";
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tablePopUpMenu = new javax.swing.JPopupMenu();
        copyPopUp = new javax.swing.JMenuItem();
        cutPopUp = new javax.swing.JMenuItem();
        pastePopUp = new javax.swing.JMenuItem();
        separator1 = new javax.swing.JPopupMenu.Separator();
        addRowPopUp = new javax.swing.JMenuItem();
        deleteRowPopUp = new javax.swing.JMenuItem();
        separator3 = new javax.swing.JPopupMenu.Separator();
        moveTopPopUp = new javax.swing.JMenuItem();
        moveUpPopUp = new javax.swing.JMenuItem();
        moveDownPopUp = new javax.swing.JMenuItem();
        moveBottomPopUp = new javax.swing.JMenuItem();
        tableScrollPane = new javax.swing.JScrollPane();
        table = new org.jdesktop.swingx.JXTable(){

            @Override
            public Component prepareEditor(TableCellEditor editor, int row, int column) {
                final Component c = super.prepareEditor(editor, row, column);

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run(){
                        if (c instanceof JTextComponent) {
                            ((JTextComponent) c).selectAll();
                        }
                    }
                });

                addRowAction.setEnabled(false);
                deleteRowAction.setEnabled(false);

                moveTopAction.setEnabled(false);
                moveUpAction.setEnabled(false);
                moveDownAction.setEnabled(false);
                moveBottomAction.setEnabled(false);

                cutAction.setEnabled(false);
                copyAction.setEnabled(false);
                pasteAction.setEnabled(false);

                //separators.setEnabled(false);
                return c;
            }

            public boolean editCellAt(int row, int column, EventObject e){
                if(e instanceof KeyEvent){
                    int i = ((KeyEvent) e).getModifiers();
                    String s = KeyEvent.getModifiersExText(((KeyEvent) e).getModifiers());
                    //any time Control is used, disable cell editing
                    if(i == InputEvent.CTRL_MASK){
                        return false;
                    }
                }
                return super.editCellAt(row, column, e);
            }
        };

        tablePopUpMenu.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                tablePopUpMenuPopupMenuWillBecomeVisible(evt);
            }
        });

        copyPopUp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        copyPopUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/draganbjedov/netbeans/tooldata/icons/copy.gif"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(copyPopUp, org.openide.util.NbBundle.getMessage(TOOLDATAVisualElement.class, "TOOLDATAVisualElement.copyPopUp.text")); // NOI18N
        tablePopUpMenu.add(copyPopUp);

        cutPopUp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        cutPopUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/draganbjedov/netbeans/tooldata/icons/cut.gif"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(cutPopUp, org.openide.util.NbBundle.getMessage(TOOLDATAVisualElement.class, "TOOLDATAVisualElement.cutPopUp.text")); // NOI18N
        tablePopUpMenu.add(cutPopUp);

        pastePopUp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        pastePopUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/draganbjedov/netbeans/tooldata/icons/paste.gif"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(pastePopUp, org.openide.util.NbBundle.getMessage(TOOLDATAVisualElement.class, "TOOLDATAVisualElement.pastePopUp.text")); // NOI18N
        tablePopUpMenu.add(pastePopUp);
        tablePopUpMenu.add(separator1);

        addRowPopUp.setAction(addRowAction);
        addRowPopUp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_INSERT, 0));
        addRowPopUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/draganbjedov/netbeans/tooldata/icons/add-row.gif"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(addRowPopUp, org.openide.util.NbBundle.getMessage(TOOLDATAVisualElement.class, "TOOLDATAVisualElement.addRowButton.text")); // NOI18N
        addRowPopUp.setActionCommand(org.openide.util.NbBundle.getMessage(TOOLDATAVisualElement.class, "TOOLDATAVisualElement.addRowPopUp.actionCommand")); // NOI18N
        tablePopUpMenu.add(addRowPopUp);
        addRowPopUp.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TOOLDATAVisualElement.class, "TOOLDATAVisualElement.addRowPopUp.AccessibleContext.accessibleDescription")); // NOI18N

        deleteRowPopUp.setAction(deleteRowAction);
        deleteRowPopUp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        deleteRowPopUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/draganbjedov/netbeans/tooldata/icons/remove-row.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(deleteRowPopUp, org.openide.util.NbBundle.getMessage(TOOLDATAVisualElement.class, "TOOLDATAVisualElement.deleteRowButton.text")); // NOI18N
        tablePopUpMenu.add(deleteRowPopUp);
        tablePopUpMenu.add(separator3);

        moveTopPopUp.setAction(moveTopAction);
        moveTopPopUp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_UP, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        moveTopPopUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/draganbjedov/netbeans/tooldata/icons/go-top.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(moveTopPopUp, org.openide.util.NbBundle.getMessage(TOOLDATAVisualElement.class, "TOOLDATAVisualElement.moveTopPopUp.text")); // NOI18N
        tablePopUpMenu.add(moveTopPopUp);

        moveUpPopUp.setAction(moveUpAction);
        moveUpPopUp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_UP, java.awt.event.InputEvent.CTRL_MASK));
        moveUpPopUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/draganbjedov/netbeans/tooldata/icons/go-up.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(moveUpPopUp, org.openide.util.NbBundle.getMessage(TOOLDATAVisualElement.class, "TOOLDATAVisualElement.moveUpPopUp.text")); // NOI18N
        tablePopUpMenu.add(moveUpPopUp);

        moveDownPopUp.setAction(moveDownAction);
        moveDownPopUp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DOWN, java.awt.event.InputEvent.CTRL_MASK));
        moveDownPopUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/draganbjedov/netbeans/tooldata/icons/go-down.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(moveDownPopUp, org.openide.util.NbBundle.getMessage(TOOLDATAVisualElement.class, "TOOLDATAVisualElement.moveDownPopUp.text")); // NOI18N
        tablePopUpMenu.add(moveDownPopUp);

        moveBottomPopUp.setAction(moveBottomAction);
        moveBottomPopUp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DOWN, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        moveBottomPopUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/draganbjedov/netbeans/tooldata/icons/go-bottom.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(moveBottomPopUp, org.openide.util.NbBundle.getMessage(TOOLDATAVisualElement.class, "TOOLDATAVisualElement.moveBottomPopUp.text")); // NOI18N
        tablePopUpMenu.add(moveBottomPopUp);

        setLayout(new java.awt.BorderLayout());

        table.setAutoCreateRowSorter(false);
        table.setModel(tableModel);
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        table.setDragEnabled(true);
        table.setDropMode(javax.swing.DropMode.ON_OR_INSERT_ROWS);
        table.setPreferredScrollableViewportSize(new java.awt.Dimension(0, 800));
        table.setRowHeight(25);
        table.setRowSorter(null);
        table.setSearchable(null);
        table.setSortable(false);
        table.setSortsOnUpdates(false);
        tableScrollPane.setViewportView(table);

        add(tableScrollPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void tablePopUpMenuPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_tablePopUpMenuPopupMenuWillBecomeVisible
        boolean enabled = table.getSelectedRowCount() > 0;
        copyAction.setEnabled(enabled);
        cutAction.setEnabled(enabled);

        pasteAction.setEnabled(CLIPBOARD.isDataFlavorAvailable(TableRowTransferable.TOOLDATA_ROWS_DATA_FLAVOR));
    }//GEN-LAST:event_tablePopUpMenuPopupMenuWillBecomeVisible

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem addRowPopUp;
    private javax.swing.JMenuItem copyPopUp;
    private javax.swing.JMenuItem cutPopUp;
    private javax.swing.JMenuItem deleteRowPopUp;
    private javax.swing.JMenuItem moveBottomPopUp;
    private javax.swing.JMenuItem moveDownPopUp;
    private javax.swing.JMenuItem moveTopPopUp;
    private javax.swing.JMenuItem moveUpPopUp;
    private javax.swing.JMenuItem pastePopUp;
    private javax.swing.JPopupMenu.Separator separator1;
    private javax.swing.JPopupMenu.Separator separator3;
    private org.jdesktop.swingx.JXTable table;
    private javax.swing.JPopupMenu tablePopUpMenu;
    private javax.swing.JScrollPane tableScrollPane;
    // End of variables declaration//GEN-END:variables
	private JButton addRowButton;
    private JButton deleteRowButton;
    private JButton moveTop;
    private JButton moveUp;
    private JButton moveDown;
    private JButton moveBottom;

    @Override
    public JComponent getVisualRepresentation() {
        return this;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        return toolbar;
    }

    @Override
    public Action[] getActions() {
        if (callback != null) {
            return callback.createDefaultActions();
        }
        return new Action[0];
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public void componentOpened() {
        if (callback != null) {
            callback.updateTitle(obj.getPrimaryFile().getNameExt());
        }
    }

    @Override
    public void componentClosed() {
    }

    @Override
    public void componentShowing() {
        updateTable();
    }

    @Override
    public void componentHidden() {
    }

    @Override
    public void componentActivated() {
        activated = true;
        if (callback != null) {
            callback.updateTitle(obj.getPrimaryFile().getNameExt());
        }
        pasteAction.setEnabled(CLIPBOARD.isDataFlavorAvailable(TableRowTransferable.TOOLDATA_ROWS_DATA_FLAVOR));

        //tableModel.fireTableDataChanged();
    }

    @Override
    public void componentDeactivated() {
        activated = false;
    }

    public boolean isActivated() {
        return activated;
    }

    @Override
    public UndoRedo getUndoRedo() {
        return obj.getUndoRedoManager();
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
        this.callback = callback;
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }

    private void createToolBar() {
        toolbar.setFloatable(false);

        addRowButton = new JButton(addRowAction);
        addRowButton.setToolTipText(NbBundle.getMessage(TOOLDATAVisualElement.class, "TOOLDATAVisualElement.addRowButton.text") + " (Insert)");
        toolbar.add(addRowButton);

        deleteRowButton = new JButton(deleteRowAction);
        deleteRowButton.setToolTipText(NbBundle.getMessage(TOOLDATAVisualElement.class, "TOOLDATAVisualElement.deleteRowButton.text") + " (Delete)");
        toolbar.add(deleteRowButton);

        toolbar.addSeparator();

        //Move row actions
        moveTop = new JButton(moveTopAction);
        moveTop.setToolTipText(NbBundle.getMessage(TOOLDATAVisualElement.class, "TOOLDATAVisualElement.moveTopPopUp.text") + " (Ctrl+Shift+Up)");
        toolbar.add(moveTop);

        moveUp = new JButton(moveUpAction);
        moveUp.setToolTipText(NbBundle.getMessage(TOOLDATAVisualElement.class, "TOOLDATAVisualElement.moveUpPopUp.text") + " (Ctrl+Up)");
        toolbar.add(moveUp);

        moveDown = new JButton(moveDownAction);
        moveDown.setToolTipText(NbBundle.getMessage(TOOLDATAVisualElement.class, "TOOLDATAVisualElement.moveDownPopUp.text") + " (Ctrl+Down)");
        toolbar.add(moveDown);

        moveBottom = new JButton(moveBottomAction);
        moveBottom.setToolTipText(NbBundle.getMessage(TOOLDATAVisualElement.class, "TOOLDATAVisualElement.moveBottomPopUp.text") + " (Ctrl+Shift+Down)");
        toolbar.add(moveBottom);

    }

    public void updateTable() {
        obj.readFile(tableModel);

//		updateColumnsWidths();
        table.packAll();
        setActiveActions();

    }

    private void setActiveActions() {
        addRowAction.setEnabled(true);
        final boolean hasSelectedRow = table.getSelectedRowCount() >= 1;
        deleteRowAction.setEnabled(hasSelectedRow);
        cutAction.setEnabled(hasSelectedRow);
        copyAction.setEnabled(hasSelectedRow);
        pasteAction.setEnabled(CLIPBOARD.isDataFlavorAvailable(TableRowTransferable.TOOLDATA_ROWS_DATA_FLAVOR));

        int[] rows = table.getSelectedRows();
        if (moveTop != null && moveUp != null && moveDown != null && moveBottom != null) {
            switch (rows.length) {
                case 0:
                    moveTopAction.setEnabled(false);
                    moveUpAction.setEnabled(false);
                    moveDownAction.setEnabled(false);
                    moveBottomAction.setEnabled(false);
                    break;
                case 1:
                    moveTopAction.setEnabled(rows[0] != 0);
                    moveUpAction.setEnabled(rows[0] != 0);
                    moveDownAction.setEnabled(rows[0] != table.getRowCount() - 1);
                    moveBottomAction.setEnabled(rows[0] != table.getRowCount() - 1);
                    break;
                default:
                    moveTopAction.setEnabled(true);
                    moveBottomAction.setEnabled(true);
                    int prev = rows[0];
                    for (int i = 1; i < rows.length; i++) {
                        if (prev != rows[i] - 1) {
                            moveUpAction.setEnabled(false);
                            moveDownAction.setEnabled(false);
                            return;
                        } else {
                            prev = rows[i];
                        }
                    }	//Continious top rows
                    final boolean topRows = rows[0] != 0;
                    moveTopAction.setEnabled(topRows);
                    moveUpAction.setEnabled(topRows);
                    //Continious rows at bottom
                    final boolean bottomRows = rows[rows.length - 1] != table.getRowCount() - 1;
                    moveDownAction.setEnabled(bottomRows);
                    moveBottomAction.setEnabled(bottomRows);
                    break;
            }
        }

    }

    private void initActions() {
        addRowAction = new AbstractAction("", new ImageIcon(getClass().getResource("/draganbjedov/netbeans/tooldata/icons/add-row.gif"))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    tableModel.addRow();
                    selectRow(table.getRowCount() - 1);
                } else {
                    tableModel.insertRow(selectedRow + 1);
                    selectRow(selectedRow + 1);
                }
            }
        };
        deleteRowAction = new AbstractAction("", new ImageIcon(getClass().getResource("/draganbjedov/netbeans/tooldata/icons/remove-row.png"))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] rows = table.getSelectedRows();
                if (rows.length > 0) {
                    tableModel.removeRows(rows);
                    int row = rows[0] - 1;
                    if (row < 0) {
                        if (table.getRowCount() > 0) {
                            selectRow(0);
                        }
                    } else {
                        selectRow(row);
                    }
                }
            }
        };

        moveTopAction = new AbstractAction("", new ImageIcon(getClass().getResource("/draganbjedov/netbeans/tooldata/icons/go-top.png"))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rows[] = table.getSelectedRows();
                for (int i = 0; i < rows.length; i++) {
                    int row = rows[i];
                    int to = i;
                    tableModel.moveRow(row, to);
                }

                tableModel.fireTableRowsUpdated(0, table.getRowCount() - 1);
                selectRowInterval(0, rows.length - 1);
            }
        };
        moveUpAction = new AbstractAction("", new ImageIcon(getClass().getResource("/draganbjedov/netbeans/tooldata/icons/go-up.png"))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rows[] = table.getSelectedRows();
                if (rows.length > 0) {
                    int row = rows[0] - 1;
                    int to = rows[rows.length - 1] + 1;
                    if (row >= 0) {
                        tableModel.moveRow(row, to);
                        tableModel.fireTableRowsUpdated(rows[0] - 1, rows[0] + rows.length - 1);
                        selectRowInterval(rows[0] - 1, rows[0] - 1 + rows.length - 1);
                    }
                }
            }
        };
        moveDownAction = new AbstractAction("", new ImageIcon(getClass().getResource("/draganbjedov/netbeans/tooldata/icons/go-down.png"))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rows[] = table.getSelectedRows();
                if (rows.length > 0) {
                    int row = rows[rows.length - 1] + 1;
                    int to = table.getSelectedRow();
                    if (row <= table.getRowCount() - 1) {
                        tableModel.moveRow(row, to);
                        tableModel.fireTableRowsUpdated(rows[0], rows[0] + 1 + rows.length - 1);
                        selectRowInterval(rows[0] + 1, rows[0] + 1 + rows.length - 1);
                    }
                }
            }
        };
        moveBottomAction = new AbstractAction("", new ImageIcon(getClass().getResource("/draganbjedov/netbeans/tooldata/icons/go-bottom.png"))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rows[] = table.getSelectedRows();
                for (int i = 0; i < rows.length; i++) {
                    int row = rows[i] - i;
                    tableModel.moveRow(row, table.getRowCount());
                }

                tableModel.fireTableRowsUpdated(0, table.getRowCount() - 1);
                selectRowInterval(table.getRowCount() - rows.length, table.getRowCount() - 1);
            }
        };

    }

    @NbBundle.Messages({
        "ACTION_CUT=Cut",
        "ACTION_COPY=Copy",
        "ACTION_PASTE=Paste"
    })
    private void init() {
        tableScrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, table.getTableHeader());
        tableScrollPane.setRowHeaderView(table);

        final ListSelectionListener listSelectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                setActiveActions();
            }
        };
        table.getSelectionModel().addListSelectionListener(listSelectionListener);
        table.setRowSelectionAllowed(true);

        /* Popravljalje visine redova zbog editovanja. Windows i Metal LAF nemaju margine u tekst poljima, a ostali imaju */
        LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
        boolean lafNotExpand = lookAndFeel.getID().toLowerCase().contains("windows") || lookAndFeel.getID().toLowerCase().contains("metal");

        /* 
		 * Da se ne bi ponistavala boja koju postavi renderer posto ne koristimo Highlighter iz SwingX-a
		 * Videti JXTable.prepareRenderer() i 
		 * JXTable.resetDefaultTableCellRendererColors()
         */
        table.putClientProperty(USE_DTCR_COLORMEMORY_HACK, false);

        // enable printing support for the table component:
        table.putClientProperty("print.printable", true); // NOI18N

        table.setRowHeight(lafNotExpand ? 25 : 27);

        table.setShowGrid(true);
        table.setGridColor(new Color(128, 128, 128, 85));

        table.getDefaultEditor(String.class).addCellEditorListener(new CellEditorListener() {
            @Override
            public void editingStopped(ChangeEvent e) {
                setActiveActions();
            }

            @Override
            public void editingCanceled(ChangeEvent e) {
                setActiveActions();
            }
        });
        table.setDefaultRenderer(String.class, new OddEvenCellRenderer());

        final String lafName = UIManager.getLookAndFeel().getName();
        boolean setBackground = lafName.equals("Nimbus");

        if (setBackground) {
            table.getTableHeader().setBackground(Color.WHITE);
        }

        if (!lafName.startsWith("GTK")) {
            tableScrollPane.getRowHeader().setBackground(Color.WHITE);
            tableScrollPane.getViewport().setBackground(Color.WHITE);
            tableScrollPane.setBackground(Color.WHITE);
        } else {
            table.setBackground(table.getTableHeader().getBackground());
        }

        final JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setReorderingAllowed(false);

        //tableModel.addTableModelListener((TableModelEvent e) -> obj.updateFile(tableModel));
        tableModel.addTableModelListener(this);
        table.setModel(tableModel);
        tableScrollPane.setViewportView(table);

        KeyStroke strokeAddRow = KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0);
        KeyStroke strokeRemoveRow = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        KeyStroke strokeEscape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

        this.getInputMap().put(strokeAddRow, "INSERT_ROW_COMMAND");
        this.getActionMap().put("INSERT_ROW_COMMAND", addRowAction);
        this.getInputMap().put(strokeRemoveRow, "DELETE_ROW_COMMAND");
        this.getActionMap().put("DELETE_ROW_COMMAND", deleteRowAction);

        table.getInputMap().put(strokeAddRow, "INSERT_ROW_COMMAND");
        table.getActionMap().put("INSERT_ROW_COMMAND", addRowAction);
        table.getInputMap().put(strokeRemoveRow, "DELETE_ROW_COMMAND");
        table.getActionMap().put("DELETE_ROW_COMMAND", deleteRowAction);

        //Move rows shortcuts
        table.getInputMap().put(moveTopPopUp.getAccelerator(), "MOVE_TOP");
        table.getActionMap().put("MOVE_TOP", moveTopAction);

        table.getInputMap().put(moveUpPopUp.getAccelerator(), "MOVE_UP");
        table.getActionMap().put("MOVE_UP", moveUpAction);

        table.getInputMap().put(moveDownPopUp.getAccelerator(), "MOVE_DOWN");
        table.getActionMap().put("MOVE_DOWN", moveDownAction);

        table.getInputMap().put(moveBottomPopUp.getAccelerator(), "MOVE_BOTTOM");
        table.getActionMap().put("MOVE_BOTTOM", moveBottomAction);

        Action escapeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table.isEditing()) {
                    int row = table.getEditingRow();
                    int col = table.getEditingColumn();
                    table.getCellEditor(row, col).cancelCellEditing();
                }
            }
        };
        table.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(strokeEscape, "ESCAPE");
        table.getActionMap().put("ESCAPE", escapeAction);

        table.setComponentPopupMenu(tablePopUpMenu);
        tableScrollPane.setComponentPopupMenu(tablePopUpMenu);

        //Cut, Copy, Paste
        table.setTransferHandler(new TableTransferHandler());

        ActionMap map = table.getActionMap();

        map.put(TransferHandler.getCutAction().getValue(Action.NAME), TransferHandler.getCutAction());
        map.put(TransferHandler.getCopyAction().getValue(Action.NAME), TransferHandler.getCopyAction());
        map.put(TransferHandler.getPasteAction().getValue(Action.NAME), TransferHandler.getPasteAction());

        TransferActionListener ccpAction = new TransferActionListener();

        cutAction = new CCPAction(Bundle.ACTION_CUT(), ccpAction);
        cutAction.putValue(Action.ACTION_COMMAND_KEY, (String) TransferHandler.getCutAction().getValue(Action.NAME));
        cutAction.putValue(Action.ACCELERATOR_KEY, cutPopUp.getAccelerator());
        cutPopUp.setAction(cutAction);
        cutPopUp.setIcon(ImageUtilities.loadImageIcon("org/openide/resources/actions/cut.gif", false));

        copyAction = new CCPAction(Bundle.ACTION_COPY(), ccpAction);
        copyAction.putValue(Action.ACTION_COMMAND_KEY, (String) TransferHandler.getCopyAction().getValue(Action.NAME));
        copyAction.putValue(Action.ACCELERATOR_KEY, copyPopUp.getAccelerator());
        copyPopUp.setAction(copyAction);
        copyPopUp.setIcon(ImageUtilities.loadImageIcon("org/openide/resources/actions/copy.gif", false));

        pasteAction = new CCPAction(Bundle.ACTION_PASTE(), ccpAction);
        pasteAction.putValue(Action.ACTION_COMMAND_KEY, (String) TransferHandler.getPasteAction().getValue(Action.NAME));
        pasteAction.putValue(Action.ACCELERATOR_KEY, pastePopUp.getAccelerator());
        pastePopUp.setAction(pasteAction);
        pastePopUp.setIcon(ImageUtilities.loadImageIcon("org/openide/resources/actions/paste.gif", false));

        CLIPBOARD.addFlavorListener(new FlavorListener() {

            @Override
            public void flavorsChanged(FlavorEvent e) {
                final boolean dataFlavorAvailable = CLIPBOARD.isDataFlavorAvailable(TableRowTransferable.TOOLDATA_ROWS_DATA_FLAVOR);
                pasteAction.setEnabled(dataFlavorAvailable);
            }
        });

        cutAction.setEnabled(false);
        copyAction.setEnabled(false);
        pasteAction.setEnabled(false);
    }

    private void selectRow(int row) {
        Rectangle rect = table.getCellRect(row, 0, true);
        table.scrollRectToVisible(rect);
        table.clearSelection();
        table.addRowSelectionInterval(row, row);
        table.addColumnSelectionInterval(0, table.getColumnCount() - 1);
        table.requestFocusInWindow();
    }

    private void selectRowInterval(int row1, int row2) {
        Rectangle rect = table.getCellRect(row1, 0, true);
        table.scrollRectToVisible(rect);
        table.clearSelection();
        table.addRowSelectionInterval(row1, row2);
        table.addColumnSelectionInterval(0, table.getColumnCount() - 1);
        table.requestFocusInWindow();
    }

    private class CCPAction extends AbstractAction {

        private final TransferActionListener ccpAction;

        public CCPAction(String name, TransferActionListener ccpAction) {
            super(name);
            this.ccpAction = ccpAction;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ActionEvent evt = new ActionEvent(table, e.getID(), (String) getValue(Action.ACTION_COMMAND_KEY), e.getWhen(), e.getModifiers());
            ccpAction.actionPerformed(evt);
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        System.out.println(e.getType() + e.getColumn() + " " + e.getFirstRow() + " " + e.getLastRow());
        //this.setEnabled(false);
        //this.obj.readFile((TOOLDATATableModel) e.getSource());
        if (e.getColumn() < 0 && e.getFirstRow() < 0 && e.getLastRow() < 0) {
            return;
        }

        this.obj.updateFile(this.tableModel);
        //this.setEnabled(true);

    }
}
