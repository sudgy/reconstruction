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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.util.AbstractList;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.FloatProcessor;

import edu.pdx.imagej.reconstruction.ReconstructionField;

public class MedianOffsetTest {
    @Test public void test_median_offset()
    {
        ImageStack stack = new ImageStack(2, 2);
        stack.addSlice(new FloatProcessor(new float[][] {{1, 0}, {0, 0}}));
        stack.addSlice(new FloatProcessor(new float[][] {{3, 0}, {0, 0}}));
        stack.addSlice(new FloatProcessor(new float[][] {{6, 0}, {0, 0}}));
        stack.addSlice(new FloatProcessor(new float[][] {{10, 0}, {0, 0}}));
        stack.addSlice(new FloatProcessor(new float[][] {{15, 0}, {0, 0}}));
        ImagePlus imp = new ImagePlus("", stack);

        MedianOffset test = new MedianOffset();

        AbstractList<Integer> ts = new AbstractList<Integer>() {
            @Override public Integer get(int index) {return index * 2 + 1;}
            @Override public int size() {return 2;}
        };

        ReconstructionField result = test.get_reference_holo(1, imp, ts, -1);
        assertEquals(3.5, result.field().get_real(0, 0));
        result = test.get_reference_holo(2, imp, ts, -1);
        assertEquals(3.5, result.field().get_real(0, 0));
        result = test.get_reference_holo(3, imp, ts, -1);
        assertEquals(6.5, result.field().get_real(0, 0));
        result = test.get_reference_holo(4, imp, ts, -1);
        assertEquals(10.5, result.field().get_real(0, 0));
        result = test.get_reference_holo(5, imp, ts, -1);
        assertEquals(10.5, result.field().get_real(0, 0));
    }
}
