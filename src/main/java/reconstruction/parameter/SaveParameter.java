/* Copyright (C) 2018 Portland State University
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

package edu.pdx.imagej.reconstruction.parameter;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Label;
import java.awt.Panel;
import javax.swing.JButton;
import javax.swing.ImageIcon;

import ij.IJ;
import ij.gui.GenericDialog;

import edu.pdx.imagej.dynamic_parameters.Harvester;
import edu.pdx.imagej.dynamic_parameters.AbstractDParameter;
import edu.pdx.imagej.dynamic_parameters.BoolParameter;

public class SaveParameter extends AbstractDParameter<Boolean> implements ActionListener {
    public SaveParameter()
    {
        JButton button = new JButton("", new ImageIcon(SaveParameter.class.getResource("/folder.png")));
        button.addActionListener(this);
        M_folder_button = new Panel();
        M_folder_button.add(button);
    }
    @Override
    public Boolean get_value() {return M_save_to_file.get_value();}
    public String get_directory() {return M_directory;}
    @Override
    public void add_to_dialog(GenericDialog gd)
    {
        M_gd = gd;
        M_save_to_file.add_to_dialog(gd);
        if (M_save_to_file.get_value()) {
            gd.addPanel(M_folder_button);
            gd.addMessage(M_directory == null ? "Select a directory..." : M_directory);
            M_directory_label = (Label)gd.getMessage();
        }
    }
    @Override
    public void read_from_dialog(GenericDialog gd)
    {
        boolean old_save_to_file = M_save_to_file.get_value();
        M_save_to_file.read_from_dialog(gd);
        M_reconstruction_needed = old_save_to_file != M_save_to_file.get_value();
        check_for_errors();
    }
    @Override
    public void actionPerformed(ActionEvent e)
    {
        String temp = IJ.getDirectory("");
        if (temp != null) M_directory = temp;
        M_directory_label.setText(M_directory == null ? "Select a directory..." : M_directory);
        if (M_directory != null) {
            M_directory_width = M_gd.getGraphics().getFontMetrics().stringWidth(M_directory) + 64;
        }
        check_for_errors();
        M_harvester.check_for_errors();
    }
    @Override
    public void save_to_prefs(Class<?> c, String name)
    {
        prefs().put(c, name, M_save_to_file.get_value());
    }
    @Override
    public void read_from_prefs(Class<?> c, String name)
    {
        M_save_to_file = new BoolParameter("Save to file", prefs().getBoolean(c, name, false));
        check_for_errors();
    }
    @Override public boolean reconstruction_needed() {return M_reconstruction_needed;}
    @Override public void recreate() {M_reconstruction_needed = false;}
    @Override public int width() {return M_directory_width;}

    private void check_for_errors()
    {
        set_error(M_save_to_file.get_value() && M_directory == null ? "Please input a valid directory." : null);
    }
    private BoolParameter M_save_to_file = new BoolParameter("Save to file", false);
    private Label M_directory_label;
    private Panel M_folder_button;
    private String M_directory;
    private int M_directory_width = 0;
    private GenericDialog M_gd;
    private boolean M_reconstruction_needed = false;
}
