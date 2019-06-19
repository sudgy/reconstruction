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

import edu.pdx.imagej.reconstruction.ConstReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionFieldImpl;
import edu.pdx.imagej.reconstruction.TParameter;

/** A {@link ReferencePlugin} that uses the median of a bunch of images as the
 * reference hologram.
 */
@Plugin(type = ReferencePlugin.class,
        name = "Median",
        priority = Priority.VERY_HIGH * 0.998)
public class Median extends AbstractReferencePlugin {
    /** Get the reference hologram. */
    @Override
    public ReconstructionField get_reference_holo(
        ConstReconstructionField field, int t)
    {
        MedianParams params = M_param.get_value();
        return get_reference_holo(params.imp, params.ts);
    }
    /** Get the reference hologram.  This is what actually gets the reference
     * hologram, because this is what this class cares about more.
     *
     * @param imp The images whose median will be taken.
     * @param ts The time slices used in calculating the Median.
     * @return The median of the images.
     */
    public ReconstructionField get_reference_holo(ImagePlus imp,
                                                  Collection<Integer> ts)
    {
        if (M_result == null) {
            double[][] real = MedianUtil.calculate_median(imp, ts);
            double[][] imag = new double[real.length][real[0].length];
            M_result = new ReconstructionFieldImpl(real, imag);
        }
        return M_result;
    }
    /** {@inheritDoc} */
    @Override
    public MedianParameter param()
    {
        return M_param;
    }

    private ReconstructionField M_result;
    private MedianParameter M_param = new MedianParameter();

    private class MedianParams {
        public ImagePlus imp;
        public Collection<Integer> ts;
    }
    /** A dynamic parameter that gets the options needed for {@link Median}.  It
     * is not intended for public use.
     */
    public class MedianParameter extends HoldingParameter<MedianParams> {
        public MedianParameter()
        {
            super("MedianParameter");
        }
        @Override
        public void initialize()
        {
            M_img = add_parameter(ImageParameter.class,
                                  "Reference Hologram Stack");
            M_ts = add_parameter(TParameter.class, M_img,
                                 TParameter.PossibleTypes.AllMulti,
                                 "MedianRef");
        }
        @Override
        public MedianParams get_value()
        {
            MedianParams result = new MedianParams();
            result.imp = M_img.get_value();
            result.ts = M_ts.get_value();
            return result;
        }
        private ImageParameter M_img;
        private TParameter M_ts;
    }
}
