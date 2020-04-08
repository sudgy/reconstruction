/* Copyright (C) 2019 Portland State University
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of version 3 of the GNU Lesser General Public License
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * For any questions regarding the license, please contact the Free Software
 * Foundation.  For any other questions regarding this program, please contact
 * David Cohoe at dcohoe@pdx.edu.
 */

package edu.pdx.imagej.reconstruction.result;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Label;
import java.awt.Panel;
import javax.swing.JButton;
import javax.swing.ImageIcon;

import ij.IJ;

import edu.pdx.imagej.dynamic_parameters.Harvester;
import edu.pdx.imagej.dynamic_parameters.DPDialog;
import edu.pdx.imagej.dynamic_parameters.AbstractDParameter;
import edu.pdx.imagej.dynamic_parameters.HoldingParameter;
import edu.pdx.imagej.dynamic_parameters.BoolParameter;
import edu.pdx.imagej.dynamic_parameters.ChoiceParameter;

class SaveParameter extends HoldingParameter<Boolean> {
    public SaveParameter() {super("SaveToFile");}
    @Override
    public void initialize()
    {
        M_saveToFile = addParameter(new BoolParameter("Save to file", false));
        M_directory = addParameter(new DirectoryParameter());
        M_dirStructure = addParameter(
            new ChoiceParameter("Directory structure", SC_dirStructures)
        );
        M_directory.setNewVisibility(false);
        M_dirStructure.setNewVisibility(false);
    }
    @Override
    public Boolean getValue() {return M_saveToFile.getValue();}
    public String getDirectory() {return M_directory.getValue();}
    public String getDirStructure() {return M_dirStructure.getValue();}
    @Override
    public void readFromDialog()
    {
        super.readFromDialog();
        M_directory.setNewVisibility(M_saveToFile.getValue());
        M_dirStructure.setNewVisibility(M_saveToFile.getValue());
        checkForErrors();
    }
    @Override
    public void readFromPrefs(Class<?> c, String name)
    {
        super.readFromPrefs(c, name);
        M_directory.setNewVisibility(M_saveToFile.getValue());
        M_dirStructure.setNewVisibility(M_saveToFile.getValue());
        checkForErrors();
    }

    private void checkForErrors()
    {
        setError(M_saveToFile.getValue() && M_directory.getValue() == null ? "Please input a valid directory." : null);
    }
    private BoolParameter M_saveToFile = new BoolParameter("Save to file", false);
    private DirectoryParameter M_directory;
    private ChoiceParameter M_dirStructure;
    private static final String[] SC_dirStructures = {
        "z/t.tiff",
        "t/z.tiff",
        "t.tiff",
        "z.tiff"
    };

    public class DirectoryParameter extends AbstractDParameter<String> implements ActionListener {
        public DirectoryParameter()
        {
            super("DirectoryParameter");
            JButton button = new JButton("", new ImageIcon(SaveParameter.class.getResource("/folder.png")));
            button.addActionListener(this);
            M_folderButton = new Panel();
            M_folderButton.add(button);
        }
        @Override
        public void addToDialog(DPDialog dialog)
        {
            M_dialog = dialog;
            dialog.addPanel(M_folderButton);
            M_directoryLabel = dialog.addMessage(M_directory == null ? "Select a directory..." : M_directory);
        }
        @Override public void readFromDialog() {}
        @Override public void saveToPrefs(Class<?> c, String name) {}
        @Override public void readFromPrefs(Class<?> c, String name) {}
        @Override public int width() {return M_directoryWidth;}
        @Override public String getValue() {return M_directory;}
        @Override
        public void actionPerformed(ActionEvent e)
        {
            String temp = IJ.getDirectory("");
            if (temp != null) M_directory = temp;
            M_directoryLabel.setText(M_directory == null ? "Select a directory..." : M_directory);
            if (M_directory != null) {
                M_directoryWidth = M_dialog.stringWidth(M_directory) + 64;
            }
            checkForErrors();
            M_harvester.checkForErrors();
        }

        private Label M_directoryLabel;
        private Panel M_folderButton;
        private String M_directory;
        private int M_directoryWidth = 0;
        private DPDialog M_dialog;
    }
}
