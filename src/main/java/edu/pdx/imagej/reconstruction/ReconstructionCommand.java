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

import ij.process.ImageProcessor;

import org.scijava.Initializable;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import edu.pdx.imagej.dynamic_parameters.ImageParameter;

@Plugin(type = Command.class, menuPath = "Plugins > DHM > Reconstruction")
public class ReconstructionCommand implements Command, Initializable {
    @Parameter private ImageParameter  P_hologram;
    @Parameter private TParameter      P_t;
    @Parameter private ZParameter      P_z;
    @Parameter private PluginParameter P_plugins;

    @Override
    public void initialize()
    {
        P_hologram = new ImageParameter("Hologram(s)");
        P_t = new TParameter(P_hologram, TParameter.PossibleTypes.All, "Main");
        P_z = new ZParameter();
        P_plugins = new PluginParameter(P_hologram);
    }
    @Override
    public void run()
    {
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
            plugin.process_beginning(plugins_map);
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
        }

        AbstractList<Integer> ts = P_t.get_value();
        AbstractList<Double> zs = P_z.get_value();
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
            }

            // Filtered Field
            for (ReconstructionPlugin plugin : plugins) {
                plugin.set_filtered_field_priority();
            }
            Collections.sort(plugins);
            for (ReconstructionPlugin plugin : plugins) {
                plugin.process_filtered_field(field, t);
            }

            for (double z : zs) {
                // Propagated Field
                for (ReconstructionPlugin plugin : plugins) {
                    plugin.set_propagated_field_priority();
                }
                Collections.sort(plugins);
                ReconstructionField propagating_field = field.copy();
                for (ReconstructionPlugin plugin : plugins) {
                    plugin.process_propagated_field(propagating_field, t, z);
                }
            }
        }

        // Ending
        for (ReconstructionPlugin plugin : plugins) {
            plugin.set_ending_priority();
        }
        Collections.sort(plugins);
        for (ReconstructionPlugin plugin : plugins) {
            plugin.process_ending();
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
