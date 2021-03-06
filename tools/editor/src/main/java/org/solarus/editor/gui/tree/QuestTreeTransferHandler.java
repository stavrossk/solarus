package org.solarus.editor.gui.tree;

import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.solarus.editor.Project;
import org.solarus.editor.QuestEditorException;
import org.solarus.editor.ResourceElement;
/**
 * Transfer handler to allow drag&drop on the QuestTree
 */
public class QuestTreeTransferHandler extends TransferHandler {
    private static final long serialVersionUID = 1L;

    @Override
    public int getSourceActions(JComponent c) {

        return TransferHandler.MOVE;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JTree tree = (JTree)c;
        DefaultMutableTreeNode selected =
                (DefaultMutableTreeNode )tree.getLastSelectedPathComponent();
        ResourceTransferable transferable = null;

        // check if the node is a lua script file or a resource
        if (selected.getUserObject() instanceof ResourceElement ||
                (selected.getUserObject() instanceof QuestTree.FileElement &&
                !((QuestTree.FileElement) selected.getUserObject()).isDirectory())) {
            transferable = new ResourceTransferable(selected);
        }

        return transferable;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        return support.isDrop() &&support.getTransferable().isDataFlavorSupported(
                ResourceTransferable.getResourceflavor());
    }

    @Override
    public boolean importData(TransferSupport support) {

        if (!canImport(support)) {
            return false;
        }
        try {
            //Retrieve the transferable / the selected node
            Transferable t = support.getTransferable();
            DefaultMutableTreeNode dmt = (DefaultMutableTreeNode)t.getTransferData(ResourceTransferable.resourceFlavor);

            JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
            TreePath tp = dl.getPath();

            if (tp == null) {
                return false;
            }
            // Retrieve where we have dropped the element
            DefaultMutableTreeNode droppedIn = (DefaultMutableTreeNode)tp.getLastPathComponent();
            Object dmtUserObject = dmt.getUserObject();
            //Check that we aren't dropping the node in itself
            if (droppedIn == dmt) {
                return false;
            }

            // Get the destination directory
            if (droppedIn.getUserObject() instanceof ResourceElement) {

                droppedIn = (DefaultMutableTreeNode) droppedIn.getParent();
            }
            if (!(droppedIn.getUserObject() instanceof QuestTree.FileElement)) {
                return false;
            }
            QuestTree.FileElement droppedInElement = (QuestTree.FileElement)droppedIn.getUserObject();
            if (!droppedInElement.isDirectory()) {

                droppedIn = (DefaultMutableTreeNode) droppedIn.getParent();
                droppedInElement = (QuestTree.FileElement)droppedIn.getUserObject();
            }
            String newPath = droppedInElement.getPath();

            // trying to move a resource
            if (dmtUserObject instanceof ResourceElement) {

                ResourceElement dmtResource = (ResourceElement) dmtUserObject;

                // check that we aren't trying to move a resource to
                // an another resource type directory
                if (!newPath.startsWith(dmtResource.type.getDirName())) {
                    return false;
                }

                if (newPath.contains("/")) {
                    newPath = newPath.substring(newPath.indexOf("/") + 1);
                } else {
                    newPath = "";
                }

                // get the new id
                String newId = dmtResource.id;
                if (newId.contains("/")) {
                    newId = newId.substring(newId.lastIndexOf("/") + 1);
                }
                if (!newPath.isEmpty()) {
                    newId = newPath + "/" + newId;
                }

                if (dmtResource.id.equals(newId)) {
                    return false;
                }

                int answer = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to change the id of the " +
                            dmtResource.type.getName() + " '" + dmtResource.id +
                            "' in '" + newId + "' ?",
                    "Are you sure?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                if (answer != JOptionPane.YES_OPTION) {
                    return false;
                }

                // change the resource id
                try {
                    Project.moveElement(dmtResource.type, dmtResource.id, newId);
                }
                catch (QuestEditorException ex) {
                    // error on change the resource id
                    return false;
                }
            }
            // trying to move a file
            else if (dmtUserObject instanceof QuestTree.FileElement) {

                QuestTree.FileElement dmtFile = (QuestTree.FileElement) dmtUserObject;

                // check that we aren't trying to move a directory
                if (dmtFile.isDirectory()) {
                    return false;
                }

                if (!newPath.isEmpty()) {
                    newPath += "/";
                }

                String path = dmtFile.getPath();
                if (path.contains("/")) {
                    newPath += path.substring(path.lastIndexOf("/") + 1);
                } else {
                    newPath += path;
                }
                QuestTree tree = (QuestTree)support.getComponent();
                tree.moveFileElement(path, newPath);
            }

            return true;
        }
        catch(Exception ex) {

            ex.printStackTrace();
        }

        return false;
    }
}
