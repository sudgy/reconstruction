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
     * @param medianImg The image stack to get the median from.
     * @param ts The time slices used in calculating the median.
     */
    public Median(ImagePlus medianImg, Collection<Integer> ts)
    {
        M_medianImg = medianImg;
        M_ts = ts;
    }
    private Median(ReconstructionField result)
    {
        M_result = result;
    }
    @Override
    public Median duplicate()
    {
        return new Median(getReferenceHolo(null, 0).copy());
    }
    /** Get the reference hologram. */
    @Override
    public ReconstructionField getReferenceHolo(
        ConstReconstructionField field, int t)
    {
        if (M_param != null) {
            M_medianImg = M_param.getValue().imp;
            M_ts = M_param.getValue().ts;
        }
        return getReferenceHolo(M_medianImg, M_ts);
    }
    /** Get the reference hologram.  This is what actually gets the reference
     * hologram, because this is what this class cares about more.
     *
     * @param imp The images whose median will be taken.
     * @param ts The time slices used in calculating the median.
     * @return The median of the images.
     */
    public ReconstructionField getReferenceHolo(ImagePlus imp,
                                                  Collection<Integer> ts)
    {
        if (M_result == null) {
            double[][] real = MedianUtil.calculateMedian(imp, ts);
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
    private ImagePlus M_medianImg;
    private Collection<Integer> M_ts;

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
                M_img = addParameter(new ImageParameter(
                                      "Reference Hologram Stack"));
            }
            else {
                M_img = addParameter(new ImageParameter(
                                      "Reference Hologram Stack",
                                      M_images));
            }
            M_ts = addParameter(new TParameter(M_img,
                                 TParameter.PossibleTypes.AllMulti,
                                 "MedianRef"));
        }
        @Override
        public MedianParams getValue()
        {
            MedianParams result = new MedianParams();
            result.imp = M_img.getValue();
            result.ts = M_ts.getValue();
            return result;
        }
        private ImageParameter M_img;
        private TParameter M_ts;
        private ImagePlus[] M_images;
    }
}
