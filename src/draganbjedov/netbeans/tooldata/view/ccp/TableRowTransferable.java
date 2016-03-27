/**
 * modified by Herbert Roider <herbert@roider.at>
 */

package draganbjedov.netbeans.tooldata.view.ccp;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * TableRowTransferable.java
 *
 * Created on Apr 17, 2013, 10:52:43 PM
 *
 * @author Dragan Bjedov
 */
public class TableRowTransferable implements Transferable {

	public static final DataFlavor TOOLDATA_ROWS_DATA_FLAVOR = new DataFlavor(List.class, "TOOLDATATableRows");
	private final List<Pair<Integer, ArrayList<String>>> rows;

	public TableRowTransferable(List<Pair<Integer, ArrayList<String>>> rows) {
		this.rows = rows;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[]{TOOLDATA_ROWS_DATA_FLAVOR};
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor == TOOLDATA_ROWS_DATA_FLAVOR;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (flavor == TOOLDATA_ROWS_DATA_FLAVOR) {
			return rows;
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}
}
