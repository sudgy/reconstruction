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

package edu.pdx.imagej.reconstruction;

import java.awt.Rectangle;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.gui.WaitForUserDialog;

/**
 * GetRoi is a simple static utility class to get an Roi from an image.
 *
 * @author David Cohoe
 */
public class GetRoi {
    /**
     * [Say something]
     *
     * The string used on the message will be "Please select the ROI and then
     * press OK."
     */
    public static Roi get(ImagePlus imp) {return get(imp, null);}
    /**
     * [Say something]
     */
    public static Roi get(ImagePlus imp, String s)
    {
        imp.show();
        if (s == null) s = "Please select the ROI and then press OK.";
        WaitForUserDialog dialog = new WaitForUserDialog(s);
        dialog.show();
        if (dialog.escPressed()) {
            imp.hide();
            return null;
        }
        Roi result = imp.getRoi();
        imp.hide();
        if (result == null) {
            // They broke this at some point?
            return new ShapeRoi(new Rectangle(0, 0, imp.getDimensions()[0], imp.getDimensions()[1]));
        }
        else return result;
    }
}
