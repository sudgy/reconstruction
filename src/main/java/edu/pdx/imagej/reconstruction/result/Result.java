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

package edu.pdx.imagej.reconstruction.result;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.process.ImageProcessor;
import ij.process.FloatProcessor;

import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

import edu.pdx.imagej.dynamic_parameters.DParameter;
import edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin;
import edu.pdx.imagej.reconstruction.plugin.AbstractReconstructionPlugin;
import edu.pdx.imagej.reconstruction.plugin.MainReconstructionPlugin;
import edu.pdx.imagej.reconstruction.ConstReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.units.DistanceUnitValue;

@Plugin(type = ReconstructionPlugin.class, priority = Priority.LAST)
public class Result extends AbstractReconstructionPlugin
                    implements MainReconstructionPlugin {
    @Parameter UIService P_ui;

    @Override
    public DParameter param() {return M_param;}
    @Override
    public void process_before_param()
    {
        M_options = M_param.get_value();
    }
    @Override
    public void process_hologram_param(ImagePlus hologram)
    {
        M_hologram = hologram;
        M_pixel_width = hologram.getProcessor().getWidth();
        M_pixel_height = hologram.getProcessor().getHeight();
    }
    @Override
    public void process_dimensions_param(DistanceUnitValue width,
                                         DistanceUnitValue height)
    {
        M_cal = new Calibration();
        M_cal.pixelWidth = width.value() / M_pixel_width;
        M_cal.pixelHeight = height.value() / M_pixel_height;
        M_cal.setUnit(width.unit().toString());
    }
    @Override
    public void process_ts_param(List<Integer> ts)
    {
        M_t_size = ts.size();
    }
    @Override
    public void process_zs_param(List<DistanceUnitValue> zs)
    {
        M_z_size = zs.size();
        if (M_options.save_to_file) {
            try {
                for (DistanceUnitValue z : zs) {
                    if (M_options.amplitude) {
                        new File(Paths.get(M_options.save_directory,
                            "Amplitude", format_z(z)).toString()).mkdirs();
                    }
                    if (M_options.phase) {
                        new File(Paths.get(M_options.save_directory,
                            "Phase", format_z(z)).toString()).mkdirs();
                    }
                    if (M_options.real) {
                        new File(Paths.get(M_options.save_directory,
                            "Real", format_z(z)).toString()).mkdirs();
                    }
                    if (M_options.imaginary) {
                        new File(Paths.get(M_options.save_directory,
                            "Imaginary", format_z(z)).toString()).mkdirs();
                    }
                }
            }
            catch (SecurityException e) {
                P_ui.showDialog("Unable to create directories: "
                    + e.getMessage(), "Error");
                M_error = true;
                return;
            }
        }
    }
    @Override
    public void process_beginning()
    {
        if (!M_options.save_to_file) {
            if (M_options.amplitude) {
                M_amplitude = new ImageStack(M_pixel_width, M_pixel_height);
            }
            if (M_options.phase) {
                M_phase = new ImageStack(M_pixel_width, M_pixel_height);
            }
            if (M_options.real) {
                M_real = new ImageStack(M_pixel_width, M_pixel_height);
            }
            if (M_options.imaginary) {
                M_imaginary = new ImageStack(M_pixel_width, M_pixel_height);
            }
        }
    }
    @Override
    public void process_propagated_field(ReconstructionField field,
                                         int t, DistanceUnitValue z)
    {
        if (M_options.amplitude) {
            process_particular(field.field().get_amp(), t, z,
                               M_amplitude, "Amplitude", get_slice_label(t));
        }
        if (M_options.phase) {
            process_particular(field.field().get_arg(), t, z,
                               M_phase, "Phase", get_slice_label(t));
        }
        if (M_options.real) {
            process_particular(field.field().get_real(), t, z,
                               M_real, "Real", get_slice_label(t));
        }
        if (M_options.imaginary) {
            process_particular(field.field().get_imag(), t, z,
                               M_imaginary, "Imaginary", get_slice_label(t));
        }
    }
    private void process_particular(double[][] d_result, int t,
                                    DistanceUnitValue z, ImageStack stack,
                                    String type, String label)
    {
        float[][] result = new float[d_result.length][d_result[0].length];
        for (int x = 0; x < result.length; ++x) {
            for (int y = 0; y < result[0].length; ++y) {
                result[x][y] = (float)d_result[x][y];
            }
        }
        ImageProcessor proc = new FloatProcessor(result);
        if (M_options.type == ResultOptions.Type.Type8Bit) {
            proc = proc.convertToByteProcessor();
        }
        else if (M_options.type == ResultOptions.Type.Type16Bit) {
            proc = proc.convertToShortProcessor();
        }

        if (M_options.save_to_file) {
            ImagePlus temp_img = new ImagePlus("", proc);
            temp_img.setCalibration(M_cal);
            IJ.saveAsTiff(temp_img, Paths.get(M_options.save_directory, type,
                format_z(z), format_t(t)).toString());
            temp_img.close();
        }
        // Not save to file
        else {
            stack.addSlice(label + ", z = " + format_z(z), proc);
        }
    }
    @Override
    public void process_ending()
    {
        almost_process_ending();
        if (!M_options.save_to_file) {
            if (M_options.amplitude) M_amplitude_imp.show();
            if (M_options.phase) M_phase_imp.show();
            if (M_options.real) M_real_imp.show();
            if (M_options.imaginary) M_imaginary_imp.show();
        }
    }
    void almost_process_ending() // Package private for testing
    {
        if (!M_options.save_to_file) {
            if (M_options.amplitude) {
                M_amplitude_imp = create_imp(M_amplitude, "Amplitude");
            }
            if (M_options.phase) M_phase_imp = create_imp(M_phase, "Phase");
            if (M_options.real) M_real_imp = create_imp(M_real, "Real");
            if (M_options.imaginary) {
                M_imaginary_imp = create_imp(M_imaginary, "Imaginary");
            }
        }
    }
    private ImagePlus create_imp(ImageStack stack, String label)
    {
        int bit_depth = 8;
        if (M_options.type == ResultOptions.Type.Type16Bit) bit_depth = 16;
        else if (M_options.type == ResultOptions.Type.Type32Bit) bit_depth = 32;
        ImagePlus imp = IJ.createHyperStack(label, M_pixel_width,
                                            M_pixel_height, 1, M_z_size,
                                            M_t_size, bit_depth);
        imp.setStack(stack);
        imp.setCalibration(M_cal);
        return imp;
    }
    @Override public boolean has_error() {return M_error;}

    private String format_z(DistanceUnitValue z)
        {return String.format("%.3f", z.value());}
    private String format_t(int t) {return String.format("%05d", t);}
    private String get_slice_label(int t)
    {
        String result = M_hologram.getImageStack().getSliceLabel(t);
        if (result == null) result = M_hologram.getTitle();
        return result;
    }

    private ResultParameter M_param = new ResultParameter();
    ResultOptions M_options; // Package private for testing
    private ImageStack M_amplitude;
    private ImageStack M_phase;
    private ImageStack M_real;
    private ImageStack M_imaginary;
    ImagePlus M_amplitude_imp; // Package private for testing
    ImagePlus M_phase_imp;
    ImagePlus M_real_imp;
    ImagePlus M_imaginary_imp;
    private boolean M_error = false;
    private ImagePlus M_hologram;
    private Calibration M_cal;
    private int M_z_size;
    private int M_t_size;
    private int M_pixel_width;
    private int M_pixel_height;
}
