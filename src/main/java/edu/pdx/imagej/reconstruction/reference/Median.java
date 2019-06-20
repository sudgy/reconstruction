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
    /** Constructor intended for live use of the plugin.
     */
    public Median()
    {
        M_param = new MedianParameter();
    }
    Median(ImagePlus[] images)
    {
        M_param = new MedianParameter(images);
    }
    /** Constructor intended for programmatic use of the plugin.
     *
     * @param median_img The image stack to get the median from.
     * @param ts The time slices used in calculating the median.
     */
    public Median(ImagePlus median_img, Collection<Integer> ts)
    {
        M_median_img = median_img;
        M_ts = ts;
    }
    /** Get the reference hologram. */
    @Override
    public ReconstructionField get_reference_holo(
        ConstReconstructionField field, int t)
    {
        if (M_param != null) {
            M_median_img = M_param.get_value().imp;
            M_ts = M_param.get_value().ts;
        }
        return get_reference_holo(M_median_img, M_ts);
    }
    /** Get the reference hologram.  This is what actually gets the reference
     * hologram, because this is what this class cares about more.
     *
     * @param imp The images whose median will be taken.
     * @param ts The time slices used in calculating the median.
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
    private MedianParameter M_param;
    ImagePlus M_median_img;
    Collection<Integer> M_ts;

    private static class MedianParams {
        public ImagePlus imp;
        public Collection<Integer> ts;
    }
    static class MedianParameter
                         extends HoldingParameter<MedianParams> {
        public MedianParameter()
        {
            super("MedianParameter");
        }
        MedianParameter(ImagePlus[] images)
        {
            super("MedianParameter");
            M_images = images;
        }
        @Override
        public void initialize()
        {
            if (M_images == null) {
                M_img = add_parameter(ImageParameter.class,
                                      "Reference Hologram Stack");
            }
            else {
                M_img = add_parameter(ImageParameter.class,
                                      "Reference Hologram Stack",
                                      M_images);
            }
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
        private ImagePlus[] M_images;
    }
}
