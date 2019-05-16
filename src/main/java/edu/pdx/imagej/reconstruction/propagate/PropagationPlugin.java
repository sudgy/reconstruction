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

import java.util.LinkedHashMap;

import ij.ImagePlus;

import org.scijava.Prioritized;
import net.imagej.ImageJPlugin;

import edu.pdx.imagej.dynamic_parameters.DParameter;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionPlugin;
import edu.pdx.imagej.reconstruction.units.DistanceUnitValue;

public interface PropagationPlugin extends ImageJPlugin, Prioritized {
    default DParameter param() {return null;}
    default void read_plugins(
        LinkedHashMap<Class<?>, ReconstructionPlugin> plugins) {}

    default void process_beginning(ImagePlus hologram,
                                   DistanceUnitValue wavelength,
                                   DistanceUnitValue width,
                                   DistanceUnitValue height) {}
    default void process_starting_field(ReconstructionField field) {}
    void propagate(ReconstructionField field, DistanceUnitValue z);
}
