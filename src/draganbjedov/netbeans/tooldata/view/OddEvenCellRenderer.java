/**
 * modified by Herbert Roider <herbert@roider.at>
 */
package draganbjedov.netbeans.tooldata.view;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Dragan Bjedov
 */
public class OddEvenCellRenderer extends DefaultTableCellRenderer {

    protected Color oddRowColor = new Color(128, 128, 128, 35);

    public OddEvenCellRenderer() {
    }

    public OddEvenCellRenderer(Color oddRowColor) {
        this.oddRowColor = oddRowColor;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (!isSelected) {
            if (row % 2 == 0) {
                comp.setBackground(table.getBackground());
            } else {
                comp.setBackground(oddRowColor);
            }
        }
        JLabel l = (JLabel) comp;
        l.setToolTipText(null);
        if (column == 3) {

            String lbl = "<html><p><img src=\"" + getClass().getResource("/draganbjedov/netbeans/tooldata/icons/cutting_edge.png") + "\"></p></html>\n";
            l.setToolTipText(lbl);
        }
        if (column == 2) {

            String lbl = "<html><p>";
            lbl += "120  end mill<br>\n";
            lbl += "200  twist drill<br>\n";
            lbl += "500  roughing turning tool<br>\n";
            lbl += "510  finishing turning tool<br>\n";
            lbl += "520  grooving turning tool<br>\n";
            lbl += "530  parting turning tool<br>\n";
            lbl += "540  thread turning<br>\n";
            lbl += "</p></html>";
            l.setToolTipText(lbl);

        }

        return comp;
    }

}
