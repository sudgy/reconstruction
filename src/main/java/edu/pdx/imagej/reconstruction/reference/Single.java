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

import java.util.Collection;

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

/** A {@link ReferencePlugin} that uses a single image as the reference
 * hologram.  It will warn you if you are about to use the same image as the one
 * you are reconstructing.
 */
@Plugin(type = ReferencePlugin.class,
        name = "Single Image",
        priority = Priority.VERY_HIGH)
public class Single extends AbstractReferencePlugin {
    /** Constructor intended for live use of the plugin.
     */
    public Single()
    {
        M_param = new SingleParameter();
    }
    Single(Collection<ImagePlus> images)
    {
        M_param = new SingleParameter(images);
    }
    /** Constructor intended for programmatic use of the plugin.
     *
     * @param image The image to use as the reference hologram.
     */
    public Single(ImagePlus image)
    {
        M_image = image;
    }
    private Single(ReconstructionField result)
    {
        M_result = result;
    }
    @Override
    public Single duplicate()
    {
        return new Single(getReferenceHolo(null, 0).copy());
    }
    /** Get the reference hologram.  It just returns the input image.
     *
     * @param field Unused.
     * @param t Unused.
     */
    @Override
    public ReconstructionField getReferenceHolo(
        ConstReconstructionField field, int t)
    {
        if (M_param != null) {
            M_image = M_param.getValue();
        }
        if (M_result == null) {
            float[][] floatArray = M_image.getProcessor().getFloatArray();
            double[][] real = new double[floatArray.length]
                                        [floatArray[0].length];
            double[][] imag = new double[real.length][real[0].length];
            for (int x = 0; x < real.length; ++x) {
                for (int y = 0; y < real[0].length; ++y) {
                    real[x][y] = floatArray[x][y];
                }
            }
            M_result = new ReconstructionFieldImpl(real, imag);
        }
        return M_result;
    }
    @Override
    public SingleParameter param()
    {
        return M_param;
    }

    private SingleParameter M_param;
    private ImagePlus M_image;
    private ReconstructionField M_result;

    static class SingleParameter extends ImageParameter
                                 implements HologramPluginParameter {
        public SingleParameter() {super("Reference Hologram Image");}
        public SingleParameter(Collection<ImagePlus> images)
        {
            super("Reference Hologram Image", images);
        }
        @Override
        public void readFromDialog()
        {
            super.readFromDialog();
            checkForErrors();
        }
        @Override
        public void readFromPrefs(Class<?> c, String name)
        {
            super.readFromPrefs(c, name);
            checkForErrors();
        }
        @Override
        public void setHologram(ImageParameter hologram)
        {
            M_holo = hologram;
        }

        private void checkForErrors()
        {
            if (M_holo == null) return;
            if (getValue() == M_holo.getValue()) {
                setWarning("Warning: Reference Hologram is the same as the "
                            + "Hologram being reconstructed.");
            }
            else setWarning(null);
        }

        private ImageParameter M_img;

        private ImageParameter M_holo;
    }
}
