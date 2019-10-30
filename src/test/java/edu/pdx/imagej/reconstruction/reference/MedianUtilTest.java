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

public class MedianUtilTest {
    @Test public void testOdd()
    {
        ImageStack stack = new ImageStack(1, 1);
        stack.addSlice(new FloatProcessor(new float[][] {{0}}));
        stack.addSlice(new FloatProcessor(new float[][] {{1}}));
        stack.addSlice(new FloatProcessor(new float[][] {{2}}));
        ImagePlus imp = new ImagePlus("", stack);

        double result = MedianUtil.calculateMedian(imp,
            new AbstractList<Integer>() {
                @Override
                public Integer get(int index) {return index + 1;}
                @Override
                public int size() {return 3;}
            })[0][0];

        assertEquals(result, 1);
    }
    @Test public void testEven()
    {
        ImageStack stack = new ImageStack(1, 1);
        stack.addSlice(new FloatProcessor(new float[][] {{0}}));
        stack.addSlice(new FloatProcessor(new float[][] {{1}}));
        stack.addSlice(new FloatProcessor(new float[][] {{2}}));
        stack.addSlice(new FloatProcessor(new float[][] {{3}}));
        ImagePlus imp = new ImagePlus("", stack);

        double result = MedianUtil.calculateMedian(imp,
            new AbstractList<Integer>() {
                @Override
                public Integer get(int index) {return index + 1;}
                @Override
                public int size() {return 4;}
            })[0][0];

        assertEquals(result, 1.5);
    }
    @Test public void testTimes()
    {
        ImageStack stack = new ImageStack(1, 1);
        stack.addSlice(new FloatProcessor(new float[][] {{0}}));
        stack.addSlice(new FloatProcessor(new float[][] {{382}}));
        stack.addSlice(new FloatProcessor(new float[][] {{5}}));
        stack.addSlice(new FloatProcessor(new float[][] {{99}}));
        ImagePlus imp = new ImagePlus("", stack);

        double result = MedianUtil.calculateMedian(imp,
            new AbstractList<Integer>() {
                @Override
                public Integer get(int index) {return index * 2 + 1;}
                @Override
                public int size() {return 2;}
            })[0][0];

        assertEquals(result, 2.5);
    }
}
