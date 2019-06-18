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
    public void process_hologram_param(ImagePlus hologram)
    {
        int[] dimensions = hologram.getDimensions();
        int width = dimensions[0];
        int height = dimensions[1];
        int max_x = Math.max(width * 7 / 8 - 1, width / 8);
        int max_y = Math.max(height * 7 / 8 - 1, height / 8);
        M_h_line = new Line(width / 8, height / 2, max_x, height / 2);
        M_v_line = new Line(width / 2, height / 8, width / 2, max_y);
    }
    /** {@inheritDoc} */
    @Override
    public Iterable<Point> get_h_line() {return M_h_line;}
    /** {@inheritDoc} */
    @Override
    public Iterable<Point> get_v_line() {return M_v_line;}
    private Line M_h_line;
    private Line M_v_line;
}
