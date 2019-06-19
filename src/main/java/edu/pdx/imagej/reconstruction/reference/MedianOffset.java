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
    /** Get the reference hologram. */
    @Override
    public ReconstructionField get_reference_holo(
        ConstReconstructionField field, int t)
    {
        MedianOffsetParams params = M_param.get_value();
        return get_reference_holo(t, params.imp, params.ts, params.offset);
    }
    /** Get the reference hologram.  This is what actually gets the reference
     * hologram, because this is whtat this class cares about more.
     *
     * @param t The time slice of the current field being reconstructed.
     * @param imp The stack of images to take the median of.
     * @param ts The time slices used to get the median.
     * @param offset The offset to use on <code>ts</code>.
     */
    public ReconstructionField get_reference_holo(int t, ImagePlus imp,
                                                  List<Integer> ts,
                                                  int offset)
    {
        // Like offset, should this be cached?

        int min_t = ts.get(0), max_t = ts.get(0);
        for (int t2 : ts) {
            if (t2 < min_t) min_t = t2;
            if (t2 > max_t) max_t = t2;
        }
        int new_offset = OffsetUtil.get_multi_offset(offset, t, 1,
                                                     imp.getImageStackSize(),
                                                     min_t, max_t);
        double[][] real = MedianUtil.calculate_median(imp,
            new AbstractList<Integer>() {
                @Override public Integer get(int index)
                    {return ts.get(index) + new_offset + t - 1;}
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

    private static class MedianOffsetParams {
        public ImagePlus imp;
        public List<Integer> ts;
        public int offset;
    }
    private static class MedianOffsetParameter
                 extends HoldingParameter<MedianOffsetParams> {
        public MedianOffsetParameter()
        {
            super("MedianOffsetParameter");
        }
        @Override
        public void initialize()
        {
            M_img = add_parameter(ImageParameter.class,
                                  "Reference Hologram Stack");
            M_ts = add_parameter(TParameter.class, M_img,
                                 TParameter.PossibleTypes.SomeMulti,
                                 "MedianOffsetRef");
            M_offset = add_parameter(IntParameter.class, 0,
                                     "Time offset", "frames");
        }
        @Override
        public MedianOffsetParams get_value()
        {
            MedianOffsetParams result = new MedianOffsetParams();
            result.imp = M_img.get_value();
            result.ts = M_ts.get_value();
            result.offset = M_offset.get_value();
            return result;
        }
        private ImageParameter M_img;
        private TParameter M_ts;
        private IntParameter M_offset;
    }
}
