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
import java.util.List;

import ij.ImagePlus;
import ij.gui.Line;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;

import edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin;

/** A {@link PolyTiltPlugin} that determines the lines automatically through a
 * least-squares method.  It picks several lines, finds the polynomial fit for
 * all of them, then figures out which of those lines has the least sum of the
 * squares of the differences between the polynomial and the original phase, and
 * picks that line.
 */
@Plugin(type = PolyTiltPlugin.class, name = "Auto",
        priority = Priority.VERY_HIGH)
public class Auto extends AbstractPolyTiltPlugin {
    /** Get the {@link PolyTilt} plugin, so that we can use its polynomial
     * fitting for our purposes.
     */
    @Override
    public void readPlugins(List<ReconstructionPlugin> plugins)
    {
        for (ReconstructionPlugin plugin : plugins) {
            if (plugin instanceof PolyTilt) M_polyTilt = (PolyTilt)plugin;
        }
    }
    @Override
    public Auto duplicate()
    {
        return new Auto();
    }
    /** {@inheritDoc} */
    @Override
    public Iterable<Point> getHLine()
    {
        if (M_hLine == null) calculateLines();
        return M_hLine;
    }
    /** {@inheritDoc} */
    @Override
    public Iterable<Point> getVLine()
    {
        if (M_vLine == null) calculateLines();
        return M_vLine;
    }

    private void calculateLines()
    {
        calculateH();
        calculateV();
    }
    private void calculateH()
    {
        final int width = M_polyTilt.width();
        final int height = M_polyTilt.height();
        final int start = (width >= 8) ? (width / 8) : 1;
        final int end = width - start - 1;
        final int numLines = Math.max(1, Math.min(10, height - 2));
        final int vertSpace = height / (numLines + 1);
        Line[] lines = new Line[numLines];
        for (int y = 0; y < numLines; ++y) {
            final int thisY = (y + 1) * vertSpace;
            lines[y] = new Line(start, thisY, end, thisY);
        }
        M_hLine = bestFit(lines);
    }
    private void calculateV()
    {
        final int width = M_polyTilt.width();
        final int height = M_polyTilt.height();
        final int start = (height >= 8) ? (height / 8) : 1;
        final int end = height - start - 1;
        final int numLines = Math.max(1, Math.min(10, height - 2));
        final int horSpace = width / (numLines + 1);
        Line[] lines = new Line[numLines];
        for (int x = 0; x < numLines; ++x) {
            final int thisX = (x + 1) * horSpace;
            lines[x] = new Line(thisX, start, thisX, end);
        }
        M_vLine = bestFit(lines);
    }
    private Line bestFit(Line[] lines)
    {
        double[][] fits = new double[lines.length][];
        double[] squares = new double[lines.length];
        for (int i = 0; i < lines.length; ++i) {
            fits[i] = M_polyTilt.fitAlongIncludingConstant(lines[i]);
            int x = 0;
            for (Point p : lines[i]) {
                double polyVal = M_polyTilt.polyEval(fits[i], x);
                double val = polyVal - M_polyTilt.getLastPhase()[x];
                squares[i] += val*val;
                ++x;
            }
        }
        int leastIndex = 0;
        double least = Double.MAX_VALUE;
        for (int i = 0; i < lines.length; ++i) {
            if (squares[i] < least) {
                least = squares[i];
                leastIndex = i;
            }
        }
        return lines[leastIndex];
    }

    private Line M_hLine;
    private Line M_vLine;
    private PolyTilt M_polyTilt;
}
