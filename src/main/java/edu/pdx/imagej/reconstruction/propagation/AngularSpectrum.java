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
import org.scijava.prefs.PrefService;

import edu.pdx.imagej.reconstruction.ConstReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.units.DistanceUnitValue;
import edu.pdx.imagej.reconstruction.plugin.MemoryParameter;

/** A {@link PropagationPlugin} that uses the angular spectrum algorithm to
 * propagate.
 */
@Plugin(type = PropagationPlugin.class,
        name = "Angular Spectrum",
        priority = Priority.VERY_HIGH)
public class AngularSpectrum extends AbstractPropagationPlugin {
    @Override
    public void process_hologram_param(ImagePlus hologram)
    {
        M_pixel_width = hologram.getWidth();
        M_pixel_height = hologram.getHeight();
    }
    @Override
    public void process_wavelength_param(DistanceUnitValue wavelength)
    {
        M_wavelength = wavelength.as_micro();
    }
    @Override
    public void process_dimensions_param(DistanceUnitValue width,
                                         DistanceUnitValue height)
    {
        M_width = width.as_micro();
        M_height = height.as_micro();
    }
    /** Calculate the "core" of the propagation, which is basically everything
     * within the square root of the equation.
     */
    @Override
    public void process_beginning()
    {
        // Eight for sizeof(double), two for real and imaginary
        M_image_memory_size = M_pixel_width * M_pixel_height * 8 * 2;
        if (P_prefs != null) {
            if (P_prefs.getBoolean(AngularSpectrum.class, "do_cache", true)) {
                if (P_prefs.getBoolean(AngularSpectrum.class,"percent", true)) {
                    double percent = P_prefs.getDouble(AngularSpectrum.class,
                                                       "percent_value", 50.0);
                    M_max_cache = (long)(IJ.maxMemory() * percent * 0.01);
                }
                else {
                    long flat = P_prefs.getInt(AngularSpectrum.class,
                                               "flat_value", 1024)
                                * (1024L * 1024L);
                    M_max_cache = flat;
                }
            }
        }

        M_core = new double[M_pixel_width][M_pixel_height];
        double k = 2.0 * Math.PI / M_wavelength;
        double l2 = M_wavelength * M_wavelength;
        double dx = 1.0 / M_width;
        double dx2 = dx * dx;
        double dy = 1.0 / M_height;
        double dy2 = dy * dy;
        int width2 = M_pixel_width / 2;
        int height2 = M_pixel_height / 2;
        int xbound = width2  + (M_pixel_width  % 2 == 0 ? 0 : 1);
        int ybound = height2 + (M_pixel_height % 2 == 0 ? 0 : 1);

        // The calculations of fx and fy are not perfectly exact.  Is that okay?
        // As the size of images gets bigger, the error is less and less, so
        // maybe it is okay.
        for (int x = 0; x < xbound; ++x) {
            int fx = x - xbound + 1;
            double val1 = fx * fx * dx2;
            for (int y = 0; y < ybound; ++y) {
                int fy = y - ybound + 1;
                double val2 = val1 + fy * fy * dy2;
                val2 = 1 - l2 * val2;
                if (val2 < 0) val2 = 0;
                else val2 = k*Math.sqrt(val2);
                M_core[x][y]                                  =
                M_core[x][M_pixel_height-y-1]                 =
                M_core[M_pixel_width-x-1][y]                  =
                M_core[M_pixel_width-x-1][M_pixel_height-y-1] = val2;
            }
        }
    }
    /** Perform the propagation.  It uses <code>field</code> and <code>last_z
     * </code> to propagate, and caches the kernel for this z value if enough
     * memory is available.
     *
     * @param original_field {@inheritDoc}
     * @param z {@inheritDoc}
     * @param field {@inheritDoc}
     * @param last_z {@inheritDoc}
     */
    @Override
    public void propagate(ConstReconstructionField original_field,
                          DistanceUnitValue z,
                          ReconstructionField field,
                          DistanceUnitValue last_z)
    {
        double dz = z.as_micro() - last_z.as_micro();
        int key = (int)Math.round(dz * 1000);
        double[][] kernel = M_kernels.get(key);
        if (kernel == null) {
            int width2 = M_pixel_width / 2;
            int height2 = M_pixel_height / 2;
            int xbound = width2  + (M_pixel_width  % 2 == 0 ? 0 : 1);
            int ybound = height2 + (M_pixel_height % 2 == 0 ? 0 : 1);
            kernel = new double[M_pixel_width][M_pixel_height*2];
            for (int x = 0; x < xbound; ++x) {
                for (int y = 0; y < ybound; ++y) {
                    kernel[x][2*y]                                    =
                    kernel[x][2*(M_pixel_height-y-1)]                 =
                    kernel[M_pixel_width-x-1][2*y]                    =
                    kernel[M_pixel_width-x-1][2*(M_pixel_height-y-1)] =
                        Math.cos(dz * M_core[x][y]);
                    kernel[x][2*y+1]                                    =
                    kernel[x][2*(M_pixel_height-y-1)+1]                 =
                    kernel[M_pixel_width-x-1][2*y+1]                    =
                    kernel[M_pixel_width-x-1][2*(M_pixel_height-y-1)+1] =
                        Math.sin(dz * M_core[x][y]);
                }
            }
            if (M_kernels.size() * M_image_memory_size < M_max_cache) {
                M_kernels.put(key, kernel);
            }
        }
        field.fourier().multiply_in_place(kernel);
    }
    @Override
    public MemoryParameter options_param()
    {
        if (M_options_param == null) {
            boolean do_cache = P_prefs.getBoolean(AngularSpectrum.class,
                                                  "do_cache", true);
            boolean initial_percent = P_prefs.getBoolean(AngularSpectrum.class,
                                                         "percent",
                                                         true);
            double percent_value = P_prefs.getDouble(AngularSpectrum.class,
                                                     "percent_value", 50.0);
            int flat_value = P_prefs.getInt(AngularSpectrum.class, "flat_value",
                                            1024);
            M_options_param = new MemoryParameter("AngularSpectrumOptions",
                                                  do_cache, initial_percent,
                                                  percent_value, flat_value);
        }
        return M_options_param;
    }
    @Override
    public void read_options()
    {
        Long val = M_options_param.get_value();
        boolean do_cache = val != null;
        boolean percent       = M_options_param.percent();
        double  percent_value = M_options_param.percent_value();
        int    flat_value     = M_options_param.flat_value();
        P_prefs.put(AngularSpectrum.class, "do_cache",      do_cache);
        P_prefs.put(AngularSpectrum.class, "percent",       percent);
        P_prefs.put(AngularSpectrum.class, "percent_value", percent_value);
        P_prefs.put(AngularSpectrum.class, "flat_value",    flat_value);
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
    private long M_max_cache;

    int M_pixel_width;
    int M_pixel_height;
    double M_wavelength;
    double M_width;
    double M_height;

    private MemoryParameter M_options_param;
    @Parameter private PrefService P_prefs;
}
