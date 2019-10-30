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

package edu.pdx.imagej.reconstruction.poly_tilt;

import java.awt.Point;

import ij.ImagePlus;
import ij.gui.Line;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;

/** A {@link PolyTiltPlugin} that uses lines in the middle of the image.  It
 * cuts off the outer eigths of the image.
 */
@Plugin(type = PolyTiltPlugin.class, name = "Middle", priority = Priority.HIGH)
public class Middle extends AbstractPolyTiltPlugin {
    /** Create the lines using the hologram's size.
     *
     * @param hologram The original hologram.
     */
    @Override
    public void processHologramParam(ImagePlus hologram)
    {
        int[] dimensions = hologram.getDimensions();
        int width = dimensions[0];
        int height = dimensions[1];
        int maxX = Math.max(width * 7 / 8 - 1, width / 8);
        int maxY = Math.max(height * 7 / 8 - 1, height / 8);
        M_hLine = new Line(width / 8, height / 2, maxX, height / 2);
        M_vLine = new Line(width / 2, height / 8, width / 2, maxY);
    }
    @Override
    public Middle duplicate() {return new Middle();}
    /** {@inheritDoc} */
    @Override
    public Iterable<Point> getHLine() {return M_hLine;}
    /** {@inheritDoc} */
    @Override
    public Iterable<Point> getVLine() {return M_vLine;}
    private Line M_hLine;
    private Line M_vLine;
}
