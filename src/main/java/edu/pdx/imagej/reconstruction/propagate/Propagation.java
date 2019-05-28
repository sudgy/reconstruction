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

import java.util.HashSet;
import java.util.LinkedHashMap;

import ij.ImagePlus;

import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import edu.pdx.imagej.dynamic_parameters.DParameter;
import edu.pdx.imagej.reconstruction.ConstReconstructionField;
import edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin;
import edu.pdx.imagej.reconstruction.plugin.AbstractReconstructionPlugin;
import edu.pdx.imagej.reconstruction.plugin.MainReconstructionPlugin;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.units.DistanceUnitValue;

@Plugin(type = ReconstructionPlugin.class, priority = Priority.FIRST)
public class Propagation extends AbstractReconstructionPlugin
                         implements MainReconstructionPlugin {
    public Propagation() {}
    public Propagation(PropagationPlugin plugin) {M_plugin = plugin;}
    @Override public DParameter param() {return M_param;}
    @Override public void read_plugins(
        LinkedHashMap<Class<?>, ReconstructionPlugin> plugins)
    {
        get_plugin().read_plugins(plugins);
    }
    @Override public void process_hologram_param(ImagePlus hologram)
    {
        M_hologram = hologram;
    }
    @Override public void process_wavelength_param(DistanceUnitValue wavelength)
    {
        M_wavelength = wavelength;
    }
    @Override public void process_dimensions_param(DistanceUnitValue width,
                                                   DistanceUnitValue height)
    {
        M_width = width;
        M_height = height;
    }
    @Override public void process_beginning()
    {
        get_plugin()
               .process_beginning(M_hologram, M_wavelength, M_width, M_height);
    }
    @Override public void process_propagated_field(
        ConstReconstructionField original_field,
        ReconstructionField current_field,
        int t, DistanceUnitValue z_from, DistanceUnitValue z_to)
    {
        if (M_ts_processed.add(t)) {
            get_plugin().process_starting_field(original_field);
        }
        get_plugin().propagate(original_field, current_field, z_from, z_to);
    }

    private PropagationPlugin get_plugin()
    {
        if (M_plugin == null) M_plugin = M_param.get_value();
        return M_plugin;
    }

    private PropagationParameter M_param = new PropagationParameter();
    private PropagationPlugin M_plugin;
    private ImagePlus M_hologram;
    private DistanceUnitValue M_wavelength;
    private DistanceUnitValue M_width;
    private DistanceUnitValue M_height;
    private HashSet<Integer> M_ts_processed = new HashSet<>();
}
