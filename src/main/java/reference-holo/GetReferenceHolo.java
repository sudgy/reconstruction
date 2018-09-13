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

import ij.ImagePlus;
import ij.gui.Roi;
import ij.process.FloatProcessor;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import unal.od.jdiffraction.cpu.utils.ArrayUtils;

/**
 * A command that takes in a hologram and outputs the real and imaginary parts
 * of the reference image for its use as a reference hologram.
 *
 * @see ReferenceHolo
 * @author David Cohoe
 */
@Plugin(type = Command.class, menuPath = "PDX>Get Reference Hologram")
public class GetReferenceHolo implements Command {
    @Parameter(label = "Hologram", autoFill = false)
    private ImagePlus P_real;
    @Override
    public void run()
    {
        float[][] hologram = P_real.getProcessor().getFloatArray();
        Roi roi = FilterImageComplex.get_roi(hologram, null);
        if (roi == null) return;
        float[][] result = ReferenceHolo.get(hologram, roi);
        float[][] real = ArrayUtils.real(result);
        float[][] imaginary = ArrayUtils.imaginary(result);
        new ImagePlus("Reference Real", new FloatProcessor(real)).show();
        new ImagePlus("Reference Imaginary", new FloatProcessor(imaginary)).show();
    }
}
