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

package edu.pdx.imagej.reconstruction.propagation;

import java.util.HashMap;

import ij.IJ;
import ij.ImagePlus;

import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import edu.pdx.imagej.reconstruction.ConstReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.units.DistanceUnitValue;

@Plugin(type = PropagationPlugin.class,
        name = "Angular Spectrum",
        priority = Priority.VERY_HIGH)
public class AngularSpectrum extends AbstractPropagationPlugin {
    @Override
    public void process_beginning(ImagePlus hologram,
                                  DistanceUnitValue wavelength,
                                  DistanceUnitValue width,
                                  DistanceUnitValue height)
    {
        int hwidth = hologram.getWidth();
        int hheight = hologram.getHeight();
        // Eight for sizeof(double), two for real and imaginary
        M_image_memory_size = hwidth * hheight * 8 * 2;

        double lambda = wavelength.as_micro();

        M_core = new double[hwidth][hheight];
        double k = 2.0 * Math.PI / lambda;
        double l2 = lambda * lambda;
        double dx = 1.0 / width.as_micro();
        double dx2 = dx * dx;
        double dy = 1.0 / height.as_micro();
        double dy2 = dy * dy;
        int width2 = hwidth / 2;
        int height2 = hheight / 2;
        int xbound = width2  + (hwidth  % 2 == 0 ? 0 : 1);
        int ybound = height2 + (hheight % 2 == 0 ? 0 : 1);

        for (int x = 0; x < xbound; ++x) {
            int fx = x - width2 + 1;
            double val1 = fx * fx * dx2;
            for (int y = 0; y < ybound; ++y) {
                int fy = y - height2 + 1;
                double val2 = val1 + fy * fy * dy2;
                val2 = 1 - l2 * val2;
                if (val2 < 0) val2 = 0;
                else val2 = k*Math.sqrt(val2);
                M_core[x][y]                    =
                M_core[x][hheight-y-1]          =
                M_core[hwidth-x-1][y]           =
                M_core[hwidth-x-1][hheight-y-1] = val2;
            }
        }
    }
    @Override
    public void propagate(ConstReconstructionField original_field,
                          ReconstructionField current_field,
                          DistanceUnitValue z_from, DistanceUnitValue z_to)
    {
        double dz = z_to.as_micro() - z_from.as_micro();
        int key = (int)Math.round(dz * 1000);
        double[][] kernel = M_kernels.get(key);
        if (kernel == null) {
            int width = M_core.length;
            int height = M_core[0].length;
            int width2 = width / 2;
            int height2 = height / 2;
            int xbound = width2  + (width  % 2 == 0 ? 0 : 1);
            int ybound = height2 + (height % 2 == 0 ? 0 : 1);
            kernel = new double[width][height*2];
            for (int x = 0; x < xbound; ++x) {
                for (int y = 0; y < ybound; ++y) {
                    kernel[x][2*y]                    =
                    kernel[x][2*(height-y-1)]         =
                    kernel[width-x-1][2*y]            =
                    kernel[width-x-1][2*(height-y-1)] =
                        Math.cos(dz * M_core[x][y]);
                    kernel[x][2*y+1]                    =
                    kernel[x][2*(height-y-1)+1]         =
                    kernel[width-x-1][2*y+1]            =
                    kernel[width-x-1][2*(height-y-1)+1] =
                        Math.sin(dz * M_core[x][y]);
                }
            }
            if (M_kernels.size() * M_image_memory_size < IJ.maxMemory() / 2) {
                M_kernels.put(key, kernel);
            }
        }
        current_field.fourier().multiply_in_place(kernel);
    }

    // The angular spectrum equation is generally
    // IFFT(FFT(U_0) exp(zik*sqrt(...)))

    // M_core holds the "k*sqrt(...)" as it is constant no matter what
    double[][] M_core; // Package private for testing
    // M_kernels holds the exp(zik*sqrt(...)) part for different z values, but
    // will stop storing these if it is starting to use up too much memory.
    // ("too much memory" is half of what ImageJ has set as maximum)
    private HashMap<Integer, double[][]> M_kernels = new HashMap<>();
    // The size of a single image, in bytes.
    private long M_image_memory_size;
}
