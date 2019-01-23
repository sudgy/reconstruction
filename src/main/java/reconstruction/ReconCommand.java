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

import java.io.File;
import java.util.AbstractList;
import java.nio.file.Paths;

import unal.od.jdiffraction.cpu.utils.ArrayUtils;

import org.scijava.Initializable;
import org.scijava.app.StatusService;
import org.scijava.command.Command;
import org.scijava.command.ContextCommand;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;
import ij.measure.Calibration;
import ij.process.ImageProcessor;
import ij.process.FloatProcessor;

import edu.pdx.imagej.dynamic_parameters.Harvester;
import edu.pdx.imagej.dynamic_parameters.IntParameter;
import edu.pdx.imagej.dynamic_parameters.DoubleParameter;
import edu.pdx.imagej.dynamic_parameters.BoolParameter;
import edu.pdx.imagej.dynamic_parameters.ImageParameter;
import edu.pdx.imagej.reconstruction.parameter.ZParameter;
import edu.pdx.imagej.reconstruction.parameter.TParameter;
import edu.pdx.imagej.reconstruction.parameter.RefParameter;
import edu.pdx.imagej.reconstruction.parameter.SaveParameter;

@Plugin(type = Command.class, menuPath = "PDX>Reconstruct")
public class ReconCommand extends ContextCommand implements Initializable {
    @Parameter private UIService P_ui;
    @Parameter private StatusService P_status;
    @Parameter private UnitService P_units;

    @Parameter private ImageParameter P_hologram;
    @Parameter private DoubleParameter P_wavelength;
    @Parameter private DoubleParameter P_width;
    @Parameter private DoubleParameter P_height;
    @Parameter private ZParameter P_z;
    @Parameter private TParameter P_t;
    @Parameter private RefParameter P_ref;
    @Parameter private BoolParameter P_center;
    @Parameter private BoolParameter P_amplitude;
    @Parameter private BoolParameter P_phase;
    @Parameter private BoolParameter P_real;
    @Parameter private BoolParameter P_imaginary;
    @Parameter private SaveParameter P_save;

    private Calibration M_cal;
    private boolean M_save_to_file;
    private String M_directory;
    private int M_pixel_width;
    private int M_pixel_height;
    private AbstractList<Double> M_zs;
    private AbstractList<Integer> M_ts;

    @Override
    public void initialize()
    {
        P_hologram = new ImageParameter("Hologram(s)");
        P_wavelength = new DoubleParameter(0.0, "Wavelength", P_units.wavelength().toString());
        P_width = new DoubleParameter(0.0, "Image_Width", P_units.image().toString());
        P_height = new DoubleParameter(0.0, "Image_Height", P_units.image().toString());
        P_z = new ZParameter();
        P_t = new TParameter(P_hologram, TParameter.PossibleTypes.All);
        P_ref = new RefParameter(P_hologram);
        P_center = new BoolParameter("Automatic Centering of ROI", false);
        P_amplitude = new BoolParameter("Amplitude", false);
        P_phase = new BoolParameter("Phase", false);
        P_real = new BoolParameter("Real", false);
        P_imaginary = new BoolParameter("Imaginary", false);
        P_save = new SaveParameter();
        P_wavelength.set_bounds(Double.MIN_VALUE, Double.MAX_VALUE);
        P_width.set_bounds(Double.MIN_VALUE, Double.MAX_VALUE);
        P_height.set_bounds(Double.MIN_VALUE, Double.MAX_VALUE);
    }
    @Override
    public void run()
    {
        ImagePlus hologram = P_hologram.get_value();
        double wavelength = P_wavelength.get_value();
        double width = P_width.get_value();
        double height = P_height.get_value();
        M_zs = P_z.get_value();
        M_ts = P_t.get_value();
        DynamicReferenceHolo reference = P_ref.get_value();
        boolean center = P_center.get_value();
        boolean centered = false;
        boolean amplitude_enabled = P_amplitude.get_value();
        boolean phase_enabled = P_phase.get_value();
        boolean real_enabled = P_real.get_value();
        boolean imaginary_enabled = P_imaginary.get_value();
        M_save_to_file = P_save.get_value();
        M_directory = P_save.get_directory();
        ReconRef recon = new ReconRef();

        M_pixel_width = hologram.getProcessor().getWidth();
        M_pixel_height = hologram.getProcessor().getHeight();
        recon.set_input_images(M_pixel_width, M_pixel_height, hologram.getProcessor().getFloatArray(), null);
        recon.set_parameters((float)wavelength, 0, (float)width, (float)height);

        ImageStack amplitude = null, phase = null, real = null, imaginary = null;
        if (!M_save_to_file) {
            if (amplitude_enabled) amplitude = new ImageStack(M_pixel_width, M_pixel_height);
            if (phase_enabled) phase = new ImageStack(M_pixel_width, M_pixel_height);
            if (real_enabled) real = new ImageStack(M_pixel_width, M_pixel_height);
            if (imaginary_enabled) imaginary = new ImageStack(M_pixel_width, M_pixel_height);
        }
        M_cal = new Calibration();
        M_cal.pixelWidth = width / M_pixel_width;
        M_cal.pixelHeight = height / M_pixel_height;
        M_cal.setUnit(P_units.image().toString());
        int result_size = M_zs.size() * M_ts.size();

        Roi roi = FilterImageComplex.get_roi(hologram.getProcessor().getFloatArray());
        if (roi == null) return;

        if (M_save_to_file) {
            for (double z : M_zs) {
                try {
                    if (amplitude_enabled) new File(Paths.get(M_directory, "Amplitude", format_z(z)).toString()).mkdirs();
                    if (phase_enabled) new File(Paths.get(M_directory, "Phase", format_z(z)).toString()).mkdirs();
                    if (real_enabled) new File(Paths.get(M_directory, "Real", format_z(z)).toString()).mkdirs();
                    if (imaginary_enabled) new File(Paths.get(M_directory, "Imaginary", format_z(z)).toString()).mkdirs();
                }
                catch (SecurityException e) {
                    P_ui.showDialog("Unable to create directories: " + e.getMessage(), "Error");
                    return;
                }
            }
        }
        int total_size = M_ts.size() * M_zs.size();
        int current = 0;
        for (int t : M_ts) {
            float[][] holo = hologram.getStack().getProcessor(t).getFloatArray();
            recon.set_input_images(M_pixel_width, M_pixel_height, holo, null);
            recon.calculateFFT();
            recon.filter(roi);
            recon.set_reference_holo(reference.result(hologram, t, roi));
            boolean already_propagated = false;
            if (center && !centered) {
                centered = already_propagated = true;
                recon.center();
            }
            for (double z : M_zs) {
                if (IJ.escapePressed()) {
                    P_status.showStatus(1, 1, "Command canceled");
                    return;
                }
                P_status.showStatus(current, total_size, "Processing " + hologram.getImageStack().getSliceLabel(t) + " at z = " + format_z(z));
                ++current;
                recon.set_distance((float)z);
                if (already_propagated) recon.propagate(false);
                else {
                    recon.propagate(true);
                    already_propagated = true;
                }
                float[][] field = recon.get_result();
                if (amplitude_enabled) finish_result(ArrayUtils.modulus(field), z, t, amplitude, "Amplitude", hologram.getImageStack().getSliceLabel(t));
                if (phase_enabled) finish_result(ArrayUtils.phase(field), z, t, phase, "Phase", hologram.getImageStack().getSliceLabel(t));
                if (real_enabled) finish_result(ArrayUtils.real(field), z, t, real, "Real", hologram.getImageStack().getSliceLabel(t));
                if (imaginary_enabled) finish_result(ArrayUtils.imaginary(field), z, t, imaginary, "Imaginary", hologram.getImageStack().getSliceLabel(t));
            }
        }
        P_status.showStatus(1, 1, "Done!");
        if (!M_save_to_file) {
            if (amplitude_enabled) show(amplitude, "Amplitude");
            if (phase_enabled) show(phase, "Phase");
            if (real_enabled) show(real, "Real");
            if (imaginary_enabled) show(imaginary, "Imaginary");
        }
    }
    private void finish_result(float[][] result_as_array, double z, int t, ImageStack stack, String type, String label)
    {
        ImageProcessor result = new FloatProcessor(result_as_array).convertToByteProcessor();
        if (M_save_to_file) {
            ImagePlus temp_img = new ImagePlus("", result);
            temp_img.setCalibration(M_cal);
            IJ.saveAsTiff(temp_img, Paths.get(M_directory, type, format_z(z), label).toString() + ".tiff");
            temp_img.close();
        }
        else {
            stack.addSlice(label + ", z = " + format_z(z), result);
        }
    }
    private void show(ImageStack stack, String label)
    {
        ImagePlus imp = IJ.createHyperStack(label, M_pixel_width, M_pixel_height, 1, M_zs.size(), M_ts.size(), 8);
        imp.setStack(stack);
        imp.setCalibration(M_cal);
        imp.show();
    }
    private String format_z(double z) {return String.format("%.3f", z);}
}
