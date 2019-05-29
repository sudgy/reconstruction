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

package edu.pdx.imagej.reconstruction.reference;

import ij.ImagePlus;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;

import edu.pdx.imagej.dynamic_parameters.HoldingParameter;
import edu.pdx.imagej.dynamic_parameters.ImageParameter;
import edu.pdx.imagej.dynamic_parameters.BoolParameter;

import edu.pdx.imagej.reconstruction.ConstReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionFieldImpl;
import edu.pdx.imagej.reconstruction.plugin.HologramPluginParameter;

@Plugin(type = ReferencePlugin.class,
        name = "Single Image",
        priority = Priority.VERY_HIGH)
public class Single extends AbstractReferencePlugin {
    @Override
    public ReconstructionField get_reference_holo(
        ConstReconstructionField field, int t)
    {
        if (result == null) {
            float[][] float_array = M_param.get_value()
                                           .getProcessor()
                                           .getFloatArray();
            double[][] real = new double[float_array.length]
                                        [float_array[0].length];
            double[][] imag = new double[real.length][real[0].length];
            for (int x = 0; x < real.length; ++x) {
                for (int y = 0; y < real[0].length; ++y) {
                    real[x][y] = float_array[x][y];
                }
            }
            result = new ReconstructionFieldImpl(real, imag);
        }
        return result;
    }
    @Override
    public SingleParameter param()
    {
        return M_param;
    }

    private SingleParameter M_param = new SingleParameter();
    private ReconstructionField result;

    public class SingleParameter extends ImageParameter
                                 implements HologramPluginParameter {
        public SingleParameter() {super("Reference Hologram Image");}
        @Override
        public void read_from_dialog()
        {
            super.read_from_dialog();
            check_for_errors();
        }
        @Override
        public void read_from_prefs(Class<?> c, String name)
        {
            super.read_from_prefs(c, name);
            check_for_errors();
        }
        @Override
        public void set_hologram(ImageParameter hologram)
        {
            M_holo = hologram;
        }

        private void check_for_errors()
        {
            if (M_holo == null) return;
            if (get_value() == M_holo.get_value()) {
                set_warning("Warning: Reference Hologram is the same as the "
                            + "Hologram being reconstructed.");
            }
            else set_warning(null);
        }

        private ImageParameter M_img;

        private ImageParameter M_holo;
    }
}
