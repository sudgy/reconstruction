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

/** A {@link edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin
 * ReconstructionPlugin} that performs filtering of holograms.  Not only does it
 * get the filter from the user and then applies it to every image, but you can
 * also use that same filter on any image you like using {@link filterField}.
 * Its priority is first, so as soon as {@link processOriginalHologram} has
 * started, you are good to use the filter.
 */
@Plugin(type = ReconstructionPlugin.class, name = "Filter",
        priority = Priority.FIRST)
public class Filter extends AbstractReconstructionPlugin
                    implements MainReconstructionPlugin {
    /** Manually set the filter.  Use this if you don't want the filter to be
     * selected through the gui.
     *
     * @param roi The roi to set the filter to be.
     */
    public void setFilter(Roi roi)
    {
        M_roi = roi;
        M_filtered = true;
    }
    public void setDefaultMessage(String message)
    {
        M_message = message;
    }
    /** Get the filter from the user through the gui, if a filter hasn't been
     * set already.
     *
     * @param field The field to acquire the filter from
     */
    @Override
    public void processOriginalHologram(ConstReconstructionField field)
    {
        if (!M_filtered) {
            getFilter(field, M_message);
            M_filtered = true;
        }
    }
    /** Get the filter from a field.
     *
     * @param field The field to acquire the filter from
     * @param message The message to display to the user describing what's going
     *                on.
     */
    public void getFilter(ConstReconstructionField field, String message)
    {
        double[][] fourier = field.fourier().getAmp();
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
    /** Filter a field (just calls {@link filterField filterField}.
     *
     * @param field The field to filter.
     * @param t Unused.
     */
    @Override
    public void processFilteredField(ReconstructionField field, int t)
    {
        filterField(field);
    }
    /** Filter a field.  This is separate from {@link processFilteredField
     * processFilteredField} so that other plugins can filter by the same roi.
     *
     * @param field The field to filter.
     */
    public void filterField(ReconstructionField field)
    {
        if (M_roi == null) return; // If the user didn't select any roi
        double[][] fourier = field.fourier().getField();
        double[][] filtered = new double[fourier.length][fourier[0].length];
        Rectangle rect = M_roi.getBounds();
        int centerX = (int)rect.getCenterX();
        int centerY = (int)rect.getCenterY();
        int xp = field.fourier().width() / 2 - centerX;
        int yp = field.fourier().height() / 2 - centerY;
        for (Point p : M_roi) {
            if (p.x < 0 || p.x >= fourier.length) continue;
            if (p.y < 0 || p.y >= fourier[0].length / 2) continue;
            filtered[p.x + xp][(p.y + yp) * 2] = fourier[p.x][p.y * 2];
            filtered[p.x + xp][(p.y + yp) * 2 + 1] = fourier[p.x][p.y * 2 + 1];
        }
        field.fourier().setField(filtered);
    }
    /** Returns whether or not the user quit when getting the filter.
     *
     * @return Whether or not the user quit.
     */
    @Override public boolean hasError() {return M_error;}
    @Override public Filter duplicate() {return new Filter();}

    private Roi M_roi;
    private boolean M_error = false;
    private boolean M_filtered = false;
    private String M_message = "Please select the ROI and then press OK.";
}
