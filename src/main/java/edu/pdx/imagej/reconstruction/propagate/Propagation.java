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
import edu.pdx.imagej.reconstruction.ReconstructionPlugin;
import edu.pdx.imagej.reconstruction.AbstractReconstructionPlugin;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.units.DistanceUnitValue;

@Plugin(type = ReconstructionPlugin.class, priority = Priority.FIRST)
public class Propagation extends AbstractReconstructionPlugin {
    @Override public DParameter param() {return M_param;}
    @Override public void read_plugins(
        LinkedHashMap<Class<?>, ReconstructionPlugin> plugins)
    {
        M_param.get_value().read_plugins(plugins);
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
        M_param.get_value()
               .process_beginning(M_hologram, M_wavelength, M_width, M_height);
    }
    @Override public void process_propagated_field(
        ReconstructionField original_field, ReconstructionField current_field,
        int t, DistanceUnitValue z_from, DistanceUnitValue z_to)
    {
        if (M_ts_processed.add(t)) {
            M_param.get_value().process_starting_field(original_field);
        }
        M_param.get_value()
               .propagate(original_field, current_field, z_from, z_to);
    }

    private PropagationParameter M_param = new PropagationParameter();
    private ImagePlus M_hologram;
    private DistanceUnitValue M_wavelength;
    private DistanceUnitValue M_width;
    private DistanceUnitValue M_height;
    private HashSet<Integer> M_ts_processed = new HashSet<>();
}
