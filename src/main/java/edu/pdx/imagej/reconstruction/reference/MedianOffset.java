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

import java.util.AbstractList;
import java.util.List;

import ij.ImagePlus;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;

import edu.pdx.imagej.dynamic_parameters.HoldingParameter;
import edu.pdx.imagej.dynamic_parameters.ImageParameter;
import edu.pdx.imagej.dynamic_parameters.IntParameter;

import edu.pdx.imagej.reconstruction.ConstReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionFieldImpl;
import edu.pdx.imagej.reconstruction.TParameter;

/** A {@link ReferencePlugin} that uses the median of a bunch of images as the
 * reference hologram, but where the images used change along with the time
 * slice used in reconstruction.
 */
@Plugin(type = ReferencePlugin.class,
        name = "Median with Offset",
        priority = Priority.VERY_HIGH * 0.997)
public class MedianOffset extends AbstractReferencePlugin {
    /** Constructor intended for live use of the plugin.
     */
    public MedianOffset()
    {
        M_param = new MedianOffsetParameter();
    }
    // Constructor used for testing
    MedianOffset(ImagePlus[] images)
    {
        M_param = new MedianOffsetParameter(images);
    }
    /** Constructor intended for programmatic use of the plugin.
     *
     * @param medianImg The image stack to get the median from.
     * @param ts The time slices used in calculating the median.
     * @param offset The time offset to use on <code>ts</code>.
     */
    public MedianOffset(ImagePlus medianImg, List<Integer> ts, int offset)
    {
        M_medianImg = medianImg;
        M_ts = ts;
        M_offset = offset;
    }
    @Override
    public MedianOffset duplicate()
    {
        return new MedianOffset(M_medianImg, M_ts, M_offset);
    }
    /** Get the reference hologram. */
    @Override
    public ReconstructionField getReferenceHolo(
        ConstReconstructionField field, int t)
    {
        if (M_param != null) {
            M_medianImg = M_param.getValue().imp;
            M_ts = M_param.getValue().ts;
            M_offset = M_param.getValue().offset;
        }
        return getReferenceHolo(t, M_medianImg, M_ts, M_offset);
    }
    /** Get the reference hologram.  This is what actually gets the reference
     * hologram, because this is whtat this class cares about more.
     *
     * @param t The time slice of the current field being reconstructed.
     * @param imp The stack of images to take the median of.
     * @param ts The time slices used to get the median.
     * @param offset The offset to use on <code>ts</code>.
     * @return The median of the images, starting at the specified offset from
     *         <code>t</code>.
     */
    public ReconstructionField getReferenceHolo(int t, ImagePlus imp,
                                                  List<Integer> ts,
                                                  int offset)
    {
        // Like offset, should this be cached?

        int minT = ts.get(0), maxT = ts.get(0);
        for (int t2 : ts) {
            if (t2 < minT) minT = t2;
            if (t2 > maxT) maxT = t2;
        }
        int newOffset = OffsetUtil.getMultiOffset(offset, t, 1,
                                                     imp.getImageStackSize(),
                                                     minT, maxT);
        double[][] real = MedianUtil.calculateMedian(imp,
            new AbstractList<Integer>() {
                @Override public Integer get(int index)
                    {return ts.get(index) + newOffset + t - 1;}
                @Override public int size() {return ts.size();}
            });
        double[][] imag = new double[real.length][real[0].length];
        return new ReconstructionFieldImpl(real, imag);
    }
    @Override
    public MedianOffsetParameter param()
    {
        return M_param;
    }

    private MedianOffsetParameter M_param = new MedianOffsetParameter();
    private ImagePlus M_medianImg;
    private List<Integer> M_ts;
    private int M_offset;

    private static class MedianOffsetParams {
        public ImagePlus imp;
        public List<Integer> ts;
        public int offset;
    }
    static class MedianOffsetParameter
                 extends HoldingParameter<MedianOffsetParams> {
        public MedianOffsetParameter()
        {
            super("MedianOffsetParameter");
        }
        MedianOffsetParameter(ImagePlus[] images)
        {
            super("MedianOffsetParameter");
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
                                 TParameter.PossibleTypes.SomeMulti,
                                 "MedianOffsetRef"));
            M_offset = addParameter(new IntParameter( 0,
                                     "Time offset", "frames"));
        }
        @Override
        public MedianOffsetParams getValue()
        {
            MedianOffsetParams result = new MedianOffsetParams();
            result.imp = M_img.getValue();
            result.ts = M_ts.getValue();
            result.offset = M_offset.getValue();
            return result;
        }
        private ImageParameter M_img;
        private TParameter M_ts;
        private IntParameter M_offset;
        private ImagePlus[] M_images;
    }
}
