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

package edu.pdx.imagej.reconstruction;

import java.util.LinkedHashMap;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;

import ij.IJ;
import ij.process.ImageProcessor;

import org.scijava.Initializable;
import org.scijava.app.StatusService;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import edu.pdx.imagej.dynamic_parameters.DoubleParameter;
import edu.pdx.imagej.dynamic_parameters.ImageParameter;
import edu.pdx.imagej.reconstruction.units.DistanceUnits;
import edu.pdx.imagej.reconstruction.units.DistanceUnitValue;
import edu.pdx.imagej.reconstruction.units.UnitService;

@Plugin(type = Command.class, menuPath = "Plugins > DHM > Reconstruction")
public class ReconstructionCommand implements Command, Initializable {
    @Parameter private ImageParameter  P_hologram;
    @Parameter private DoubleParameter P_wavelength;
    @Parameter private DoubleParameter P_width;
    @Parameter private DoubleParameter P_height;
    @Parameter private TParameter      P_ts;
    @Parameter private ZParameter      P_zs;
    @Parameter private PluginParameter P_plugins;

    @Parameter private StatusService P_status;
    @Parameter private UnitService P_units;

    @Override
    public void initialize()
    {
        P_hologram = new ImageParameter("Hologram(s)");
        P_wavelength = new DoubleParameter(500.0, "Wavelength",
                                           P_units.wavelength().toString());
        P_width = new DoubleParameter(0.0, "Image_Width",
                                      P_units.image().toString());
        P_height = new DoubleParameter(0.0, "Image_Height",
                                       P_units.image().toString());
        P_ts = new TParameter(P_hologram, TParameter.PossibleTypes.All, "Main");
        P_zs = new ZParameter();
        P_plugins = new PluginParameter(P_hologram);

        P_wavelength.set_bounds(Double.MIN_VALUE, Double.MAX_VALUE);
        P_width.set_bounds(Double.MIN_VALUE, Double.MAX_VALUE);
        P_height.set_bounds(Double.MIN_VALUE, Double.MAX_VALUE);
    }
    @Override
    public void run()
    {
        DistanceUnitValue wavelength
            = new DistanceUnitValue(P_wavelength.get_value(),
                                    P_units.wavelength());
        DistanceUnitValue width
            = new DistanceUnitValue(P_width.get_value(), P_units.image());
        DistanceUnitValue height
            = new DistanceUnitValue(P_height.get_value(), P_units.image());
        LinkedHashMap<Class<?>, ReconstructionPlugin> plugins_map
            = P_plugins.get_value();
        ArrayList<ReconstructionPlugin> plugins
            = new ArrayList<>(plugins_map.values());

        // Beginning
        for (ReconstructionPlugin plugin : plugins) {
            plugin.set_beginning_priority();
        }
        Collections.sort(plugins);
        for (ReconstructionPlugin plugin : plugins) {
            plugin.read_plugins(plugins_map);
            plugin.process_before_param();
            plugin.process_hologram_param(P_hologram.get_value());
            plugin.process_wavelength_param(wavelength);
            plugin.process_dimensions_param(width, height);
            plugin.process_ts_param(P_ts.get_value());
            plugin.process_zs_param(P_zs.get_value());
            plugin.process_beginning();
            if (plugin.has_error()) return;
        }

        // Original Hologram
        for (ReconstructionPlugin plugin : plugins) {
            plugin.set_original_hologram_priority();
        }
        Collections.sort(plugins);
        ReconstructionField field = create_field(P_hologram.get_value()
                                                           .getProcessor());
        for (ReconstructionPlugin plugin : plugins) {
            plugin.process_original_hologram(field);
            if (plugin.has_error()) return;
        }

        AbstractList<Integer> ts = P_ts.get_value();
        AbstractList<DistanceUnitValue> zs = P_zs.get_value();
        for (int t : ts) {
            // Hologram
            for (ReconstructionPlugin plugin : plugins) {
                plugin.set_hologram_priority();
            }
            Collections.sort(plugins);
            field = create_field(
                P_hologram.get_value().getStack().getProcessor(t));
            for (ReconstructionPlugin plugin : plugins) {
                plugin.process_hologram(field, t);
                if (plugin.has_error()) return;
            }

            // Filtered Field
            for (ReconstructionPlugin plugin : plugins) {
                plugin.set_filtered_field_priority();
            }
            Collections.sort(plugins);
            for (ReconstructionPlugin plugin : plugins) {
                plugin.process_filtered_field(field, t);
                if (plugin.has_error()) return;
            }

            // Propagated Field
            for (ReconstructionPlugin plugin : plugins) {
                plugin.set_propagated_field_priority();
            }
            Collections.sort(plugins);
            ReconstructionField propagating_field = field.copy();
            DistanceUnitValue z_from
                = new DistanceUnitValue(0, DistanceUnits.Micro);
            for (DistanceUnitValue z : zs) {
                if (IJ.escapePressed()) {
                    P_status.showStatus(1, 1, "Command canceled");
                    return;
                }
                for (ReconstructionPlugin plugin : plugins) {
                    plugin.process_propagated_field(field, propagating_field,
                                                    t, z_from, z);
                    if (plugin.has_error()) return;
                }
                z_from = z;
            }
        }

        // Ending
        for (ReconstructionPlugin plugin : plugins) {
            plugin.set_ending_priority();
        }
        Collections.sort(plugins);
        for (ReconstructionPlugin plugin : plugins) {
            plugin.process_ending();
            if (plugin.has_error()) return;
        }
    }

    private ReconstructionField create_field(ImageProcessor image)
    {
        float[][] float_array = P_hologram.get_value()
                                          .getProcessor()
                                          .getFloatArray();
        double[][] real = new double[float_array.length][float_array[0].length];
        double[][] imag = new double[real.length][real[0].length];
        for (int x = 0; x < real.length; ++x) {
            for (int y = 0; y < real[0].length; ++y) {
                real[x][y] = float_array[x][y];
            }
        }
        return new ReconstructionFieldImpl(real, imag);
    }
}
