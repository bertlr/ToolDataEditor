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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.netbeans.api.actions.Openable;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
@ActionID(
        category = "File",
        id = "draganbjedov.netbeans.tooldata.TOOLDATANewFileAction")
@ActionRegistration(
        displayName = "#CTL_NewFile",
        iconBase = "draganbjedov/netbeans/tooldata/icons/tooldata.png")
@ActionReference(
        path = "Menu/File", 
        position = 0)
/**
 *
 * @author Herbert Roider <herbert@roider.at>
 */
public final class TOOLDATANewFileAction implements ActionListener {
    private static AtomicInteger _integer = new AtomicInteger(0);
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            DataObject gdo = getDataObject();
            Openable openable = gdo.getLookup().lookup(Openable.class);
            openable.open();
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    protected DataObject getDataObject() throws DataObjectNotFoundException, IOException {
        String templateName = getTemplate();
        FileObject fo = FileUtil.getConfigRoot().getFileObject(templateName);
        DataObject template = DataObject.find(fo);
        FileSystem memFS = FileUtil.createMemoryFileSystem();
        FileObject root = memFS.getRoot();
        DataFolder dataFolder = DataFolder.findFolder(root);
        DataObject gdo = template.createFromTemplate(
               dataFolder, "newfile" + getNextCount());       
        return gdo;
    }
    protected String getTemplate() {
        return "Templates/Other/tooldata.tooldata";
    }
    private static int getNextCount() {
        return _integer.incrementAndGet();
    }
}