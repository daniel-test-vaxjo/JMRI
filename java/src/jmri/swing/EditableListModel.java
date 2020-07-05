/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmri.swing;

import javax.swing.ListModel;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;

/**
 *
 * @author Randall Wood
 */
@API(status = EXPERIMENTAL)
public interface EditableListModel<E> extends ListModel<E> {

    public boolean isCellEditable(int index);

    public void setValueAt(E value, int index);
}
