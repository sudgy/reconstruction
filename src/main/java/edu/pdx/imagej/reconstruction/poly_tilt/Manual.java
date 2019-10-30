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

import edu.pdx.imagej.dynamic_parameters.HoldingParameter;
import edu.pdx.imagej.dynamic_parameters.ImageParameter;
import edu.pdx.imagej.dynamic_parameters.IntParameter;
import edu.pdx.imagej.reconstruction.plugin.HologramPluginParameter;

/** A {@link PolyTiltPlugin} that uses user input to determine the lines.
 */
@Plugin(type = PolyTiltPlugin.class, name = "Manual",
        priority = Priority.HIGH * 0.999)
public class Manual extends AbstractPolyTiltPlugin {
    /** Constructor intended for live use of this plugin.  It will get the lines
     * through a dynamic parameter.
     */
    public Manual()
    {
        M_param = new ManualParameter();
    }
    /** Constructor intended for programmatic use of this plugin.  It gets the
     * lines through these parameters.
     *
     * @param hVal The y pixel value for the horizontal line to be at.
     * @param hStart The x pixel value for the horizontal line to start at.
     * @param hEnd The x pixel value for the horizontal line to end at.
     * @param vVal The x pixel value for the vertical line to be at.
     * @param vStart The y pixel value for the vertical line to start at.
     * @param vEnd The y pixel value for the vertical line to end at.
     */
    public Manual(int hVal, int hStart, int hEnd,
                  int vVal, int vStart, int vEnd)
    {
        Line[] lines = createLines(hVal, hStart, hEnd,
                                    vVal, vStart, vEnd);
        M_hLine = lines[0];
        M_vLine = lines[1];
    }
    private Manual(Line hline, Line vline)
    {
        M_hLine = hline;
        M_vLine = vline;
    }
    @Override
    public Manual duplicate()
    {
        // Not cloning the lines, but they won't change
        return new Manual(getHLine(), getVLine());
    }
    /** {@inheritDoc} */
    @Override
    public Line getHLine()
    {
        if (M_hLine == null) {
            Line[] lines = M_param.getValue();
            M_hLine = lines[0];
            M_vLine = lines[1];
        }
        return M_hLine;
    }
    /** {@inheritDoc} */
    @Override
    public Line getVLine()
    {
        if (M_vLine == null) {
            Line[] lines = M_param.getValue();
            M_hLine = lines[0];
            M_vLine = lines[1];
        }
        return M_vLine;
    }
    /** {@inheritDoc} */
    @Override public ManualParameter param()
    {
        return M_param;
    }

    private Line M_hLine;
    private Line M_vLine;
    private ManualParameter M_param;

    private static Line[] createLines(int hVal, int h1, int h2,
                                       int vVal, int v1, int v2)
    {
        int hStart = Math.min(h1, h2);
        int hEnd = Math.max(h1, h2);
        int vStart = Math.min(v1, v2);
        int vEnd = Math.max(v1, v2);
        Line[] result = new Line[2];
        result[0] = new Line(hStart, hVal, hEnd, hVal);
        result[1] = new Line(vVal, vStart, vVal, vEnd);
        return result;
    }

    static class ManualParameter extends HoldingParameter<Line[]>
                                         implements HologramPluginParameter
    {
        public ManualParameter() {super("ManualCenterParams");}
        @Override
        public void setHologram(ImageParameter hologram)
        {
            M_holo = hologram;
            setDimensions1();
            if (M_hVal == null) {
                M_hVal = addParameter(new IntParameter(M_height / 2,
                                        "Pixel_value_for_horizontal_line"));
                M_hStart = addParameter(new IntParameter(0,
                                          "Horizontal_line_start"));
                M_hEnd = addParameter(new IntParameter(M_width - 1,
                                        "Horizontal_line_end"));
                M_vVal = addParameter(new IntParameter(M_width / 2,
                                        "Pixel_value_for_vertical_line"));
                M_vStart = addParameter(new IntParameter(0,
                                          "Vertical_line_start"));
                M_vEnd = addParameter(new IntParameter(M_height - 1,
                                        "Vertical_line_end"));
            }
            setDimensions2();
        }
        @Override
        public void readFromDialog()
        {
            if (M_holo != null) {
                int[] dimensions = M_holo.getValue().getDimensions();
                if (M_width != dimensions[0] || M_height != dimensions[1]) {
                    setDimensions();
                }
            }
            super.readFromDialog();
        }
        @Override
        public void readFromPrefs(Class<?> c, String name)
        {
            if (M_holo != null) setDimensions();
            super.readFromPrefs(c, name);
        }
        @Override
        public Line[] getValue()
        {
            int hVal = M_hVal.getValue();
            int h1 = M_hStart.getValue();
            int h2 = M_hEnd.getValue();
            int hStart = Math.min(h1, h2);
            int hEnd = Math.max(h1, h2);
            int vVal = M_vVal.getValue();
            int v1 = M_vStart.getValue();
            int v2 = M_vEnd.getValue();
            int vStart = Math.min(v1, v2);
            int vEnd = Math.max(v1, v2);
            Line[] result = new Line[2];
            result[0] = new Line(hStart, hVal, hEnd, hVal);
            result[1] = new Line(vVal, vStart, vVal, vEnd);
            return createLines(
                M_hVal.getValue(), M_hStart.getValue(), M_hEnd.getValue(),
                M_vVal.getValue(), M_vStart.getValue(), M_vEnd.getValue()
            );
        }
        private void setDimensions()
        {
            setDimensions1();
            setDimensions2();
        }
        private void setDimensions1()
        {
            int[] dimensions = M_holo.getValue().getDimensions();
            M_width = dimensions[0];
            M_height = dimensions[1];
        }
        private void setDimensions2()
        {
            M_hVal  .setBounds(0, M_height - 1);
            M_hStart.setBounds(0, M_width  - 1);
            M_hEnd  .setBounds(0, M_width  - 1);
            M_vVal  .setBounds(0, M_width  - 1);
            M_vStart.setBounds(0, M_height - 1);
            M_vEnd  .setBounds(0, M_height - 1);
        }
        private IntParameter M_hVal;
        private IntParameter M_hStart;
        private IntParameter M_hEnd;
        private IntParameter M_vVal;
        private IntParameter M_vStart;
        private IntParameter M_vEnd;
        private int          M_width;
        private int          M_height;

        private ImageParameter M_holo;
    }
}
