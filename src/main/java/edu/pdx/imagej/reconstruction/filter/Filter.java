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

package edu.pdx.imagej.reconstruction.filter;

import java.awt.Point;
import java.awt.Rectangle;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.gui.WaitForUserDialog;
import ij.process.FloatProcessor;

import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin;
import edu.pdx.imagej.reconstruction.plugin.AbstractReconstructionPlugin;
import edu.pdx.imagej.reconstruction.plugin.MainReconstructionPlugin;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.ConstReconstructionField;

@Plugin(type = ReconstructionPlugin.class, priority = Priority.EXTREMELY_LOW)
public class Filter extends AbstractReconstructionPlugin
                    implements MainReconstructionPlugin {
    public void set_filter(Roi roi)
    {
        M_roi = roi;
        M_filtered = true;
    }
    @Override
    public void process_original_hologram(ConstReconstructionField field)
    {
        if (!M_filtered) {
            get_filter(field, "Please select the ROI and then press OK.");
            M_filtered = true;
        }
    }
    public void get_filter(ConstReconstructionField field, String message)
    {
        double[][] fourier = field.fourier().get_amp();
        float[][] array = new float[fourier.length][fourier[0].length];
        for (int x = 0; x < fourier.length; ++x) {
            for (int y = 0; y < fourier[0].length; ++y) {
                array[x][y] = (float)fourier[x][y];
            }
        }
        FloatProcessor proc = new FloatProcessor(array);
        proc.log();
        ImagePlus imp = new ImagePlus("FFT", proc);
        imp.show();
        WaitForUserDialog dialog = new WaitForUserDialog(message);
        dialog.show();
        if (dialog.escPressed()) {
            imp.hide();
            M_error = true;
            return;
        }
        M_roi = imp.getRoi();
        imp.hide();
    }
    @Override
    public void process_hologram(ReconstructionField field, int t)
    {
        filter_field(field);
    }
    // This is separate so that other plugins can filter by the same roi
    public void filter_field(ReconstructionField field)
    {
        if (M_roi == null) return; // If the user didn't select any roi
        double[][] fourier = field.fourier().get_field();
        double[][] filtered = new double[fourier.length][fourier[0].length];
        Rectangle rect = M_roi.getBounds();
        int center_x = (int)rect.getCenterX();
        int center_y = (int)rect.getCenterY();
        int xp = field.fourier().width() / 2 - center_x;
        int yp = field.fourier().height() / 2 - center_y;
        for (Point p : M_roi) {
            filtered[p.x + xp][(p.y + yp) * 2] = fourier[p.x][p.y * 2];
            filtered[p.x + xp][(p.y + yp) * 2 + 1] = fourier[p.x][p.y * 2 + 1];
        }
        field.fourier().set_field(filtered);
    }
    @Override public boolean has_error() {return M_error;}

    private Roi M_roi;
    private boolean M_error = false;
    private boolean M_filtered = false;
}
