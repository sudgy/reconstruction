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

package edu.pdx.imagej.reconstruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.util.List;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.FloatProcessor;

import edu.pdx.imagej.dynamic_parameters.ImageParameter;
import edu.pdx.imagej.dynamic_parameters.TestDialog;

public class TParameterTest {
    @Test public void testSingleInit()
    {
        TParameter param = getTestParam(1);
        TParameter.SingleT test = param.new SingleT();
        test.initialize();
        List<Integer> list = test.getValue();
        assertEquals(1, list.size());
        assertEquals(1, list.get(0).intValue());
    }
    @Test public void testSingleChange()
    {
        TParameter param = getTestParam(1);
        TParameter.SingleT test = param.new SingleT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.addToDialog(dialog);
        dialog.getInteger(0).value = 2;
        test.readFromDialog();
        List<Integer> list = test.getValue();
        assertEquals(1, list.size());
        assertEquals(2, list.get(0).intValue());
    }
    @Test public void testSingleError()
    {
        TParameter param = getTestParam(2);
        param.updateMaxT();
        TParameter.SingleT test = param.new SingleT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.addToDialog(dialog);

        assertTrue(test.getError() == null);

        dialog.getInteger(0).value = 3;
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getInteger(0).value = 2;
        test.readFromDialog();
        assertTrue(test.getError() == null);
    }
    @Test public void testSingleErrorChange()
    {
        ImageParameter images = getImageParam(2, 3);
        TParameter param = getTestParam(images);
        param.updateMaxT();
        TParameter.SingleT test = param.new SingleT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        images.addToDialog(dialog);
        test.addToDialog(dialog);

        dialog.getInteger(0).value = 3;
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getStringIndex(0).value = 1;
        images.readFromDialog();
        param.updateMaxT();
        test.readFromDialog();
        assertTrue(test.getError() == null);

        dialog.getStringIndex(0).value = 0;
        images.readFromDialog();
        param.updateMaxT();
        test.readFromDialog();
        assertTrue(test.getError() != null);
    }

    @Test public void testCurrentInit()
    {
        TParameter param = getTestParam(2);
        TParameter.CurrentT test = param.new CurrentT();
        test.initialize();
        List<Integer> list = test.getValue();
        assertEquals(1, list.size());
        assertEquals(1, list.get(0).intValue());
    }
    @Test public void testCurrentChange()
    {
        ImageParameter image = getImageParam(2);
        TParameter param = getTestParam(image);
        TParameter.CurrentT test = param.new CurrentT();
        test.initialize();
        image.getValue().setPosition(2);
        List<Integer> list = test.getValue();
        assertEquals(1, list.size());
        assertEquals(2, list.get(0).intValue());
    }

    @Test public void testAll()
    {
        ImageParameter image = getImageParam(2, 3);
        TParameter param = getTestParam(image);
        param.updateMaxT();
        TParameter.AllT test = param.new AllT();
        test.initialize();

        List<Integer> list = test.getValue();
        assertEquals(2, list.size());
        assertEquals(1, list.get(0).intValue());
        assertEquals(2, list.get(1).intValue());

        TestDialog dialog = new TestDialog();
        image.addToDialog(dialog);
        dialog.getStringIndex(0).value = 1;
        image.readFromDialog();
        param.updateMaxT();

        list = test.getValue();
        assertEquals(3, list.size());
        assertEquals(1, list.get(0).intValue());
        assertEquals(2, list.get(1).intValue());
        assertEquals(3, list.get(2).intValue());
    }

    @Test public void testListInit()
    {
        TParameter param = getTestParam(1);
        TParameter.ListT test = param.new ListT();
        test.initialize();
        assertTrue(test.getError() != null);
    }
    @Test public void testListSingle()
    {
        TParameter param = getTestParam(1);
        TParameter.ListT test = param.new ListT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.addToDialog(dialog);
        dialog.getString(0).value = "3";
        test.readFromDialog();

        List<Integer> list = test.getValue();
        assertEquals(1, list.size());
        assertEquals(3, list.get(0).intValue());
    }
    @Test public void testListSingleBounds()
    {
        TParameter param = getTestParam(2);
        param.updateMaxT();
        TParameter.ListT test = param.new ListT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.addToDialog(dialog);

        dialog.getString(0).value = "1";
        test.readFromDialog();
        assertTrue(test.getError() == null);

        dialog.getString(0).value = "3";
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getString(0).value = "2";
        test.readFromDialog();
        assertTrue(test.getError() == null);
    }
    @Test public void testListMulti()
    {
        TParameter param = getTestParam(1);
        TParameter.ListT test = param.new ListT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.addToDialog(dialog);
        dialog.getString(0).value = "3, \t 2,5";
        test.readFromDialog();

        List<Integer> list = test.getValue();
        assertEquals(3, list.size());
        assertEquals(3, list.get(0).intValue());
        assertEquals(2, list.get(1).intValue());
        assertEquals(5, list.get(2).intValue());
    }
    @Test public void testListMultiBounds()
    {
        TParameter param = getTestParam(2);
        param.updateMaxT();
        TParameter.ListT test = param.new ListT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.addToDialog(dialog);

        dialog.getString(0).value = "1, 2";
        test.readFromDialog();
        assertTrue(test.getError() == null);

        dialog.getString(0).value = "1, 3";
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getString(0).value = "1, 2";
        test.readFromDialog();
        assertTrue(test.getError() == null);
    }
    @Test public void testListChangeBounds()
    {
        ImageParameter image = getImageParam(2, 3);
        TParameter param = getTestParam(image);
        param.updateMaxT();
        TParameter.ListT test = param.new ListT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        image.addToDialog(dialog);
        test.addToDialog(dialog);

        dialog.getString(0).value = "1, 3";
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getStringIndex(0).value = 1;
        image.readFromDialog();
        param.updateMaxT();
        test.readFromDialog();
        assertTrue(test.getError() == null);
    }
    @Test public void testListParse()
    {
        TParameter param = getTestParam(1);
        param.updateMaxT();
        TParameter.ListT test = param.new ListT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.addToDialog(dialog);

        dialog.getString(0).value = "";
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getString(0).value = "1";
        test.readFromDialog();
        assertTrue(test.getError() == null);

        dialog.getString(0).value = ".";
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getString(0).value = "a";
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getString(0).value = "1a";
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getString(0).value = "1,.";
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getString(0).value = "1,,1";
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getString(0).value = "1.5";
        test.readFromDialog();
        assertTrue(test.getError() != null);
    }

    @Test public void testRangeInit()
    {
        TParameter param = getTestParam();
        TParameter.RangeT test = param.new RangeT();
        test.initialize();
        List<Integer> list = test.getValue();
        assertEquals(1, list.size());
        assertEquals(1, list.get(0).intValue());
    }
    @Test public void testRangeUp()
    {
        TParameter param = getTestParam();
        TParameter.RangeT test = param.new RangeT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.addToDialog(dialog);
        dialog.getInteger(0).value = 3;
        dialog.getInteger(1).value = 7;
        dialog.getInteger(2).value = 2;
        test.readFromDialog();

        List<Integer> list = test.getValue();
        assertEquals(3, list.size());
        assertEquals(3, list.get(0).intValue());
        assertEquals(5, list.get(1).intValue());
        assertEquals(7, list.get(2).intValue());
    }
    @Test public void testRangeDown()
    {
        TParameter param = getTestParam();
        TParameter.RangeT test = param.new RangeT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.addToDialog(dialog);
        dialog.getInteger(0).value = 8;
        dialog.getInteger(1).value = 2;
        dialog.getInteger(2).value = -3;
        test.readFromDialog();

        List<Integer> list = test.getValue();
        assertEquals(3, list.size());
        assertEquals(8, list.get(0).intValue());
        assertEquals(5, list.get(1).intValue());
        assertEquals(2, list.get(2).intValue());
    }
    @Test public void testRangeErrorStep()
    {
        TParameter param = getTestParam(1);
        param.updateMaxT();
        TParameter.RangeT test = param.new RangeT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.addToDialog(dialog);

        assertTrue(test.getError() == null);

        dialog.getInteger(2).value = 0;
        test.readFromDialog();
        assertTrue(test.getError() != null);
        dialog.getInteger(2).value = 1;
        test.readFromDialog();
        assertTrue(test.getError() == null);
    }
    @Test public void testRangeErrorSign()
    {
        TParameter param = getTestParam(2);
        param.updateMaxT();
        TParameter.RangeT test = param.new RangeT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.addToDialog(dialog);

        // Test that nothing weird happens when begin == end
        dialog.getInteger(2).value = -1;
        test.readFromDialog();
        assertTrue(test.getError() == null);

        // Test that it's okay to go up
        dialog.getInteger(1).value = 2;
        dialog.getInteger(2).value = 1;
        test.readFromDialog();
        assertTrue(test.getError() == null);

        // Test that going down with a positive step is bad
        dialog.getInteger(0).value = 2;
        dialog.getInteger(1).value = 1;
        test.readFromDialog();
        assertTrue(test.getError() != null);
        dialog.getInteger(0).value = 1;
        dialog.getInteger(1).value = 2;
        test.readFromDialog();
        assertTrue(test.getError() == null);

        // Test that going up with a negative step is bad
        dialog.getInteger(2).value = -1;
        test.readFromDialog();
        assertTrue(test.getError() != null);

        // Test that going down with a negative step is okay
        dialog.getInteger(0).value = 2;
        dialog.getInteger(1).value = 1;
        test.readFromDialog();
        assertTrue(test.getError() == null);
    }
    @Test public void testRangeBounds()
    {
        TParameter param = getTestParam(2);
        param.updateMaxT();
        TParameter.RangeT test = param.new RangeT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.addToDialog(dialog);

        dialog.getInteger(1).value = 3;
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getInteger(0).value = 3;
        dialog.getInteger(1).value = 4;
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getInteger(0).value = 3;
        dialog.getInteger(1).value = 1;
        dialog.getInteger(2).value = -1;
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getInteger(0).value = 1;
        dialog.getInteger(1).value = 2;
        dialog.getInteger(2).value = 1;
        test.readFromDialog();
        assertTrue(test.getError() == null);

        dialog.getInteger(0).value = 3;
        dialog.getInteger(1).value = 1;
        dialog.getInteger(2).value = -1;
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getInteger(0).value = 4;
        dialog.getInteger(1).value = 3;
        dialog.getInteger(2).value = -1;
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getInteger(0).value = 3;
        dialog.getInteger(1).value = 1;
        dialog.getInteger(2).value = -1;
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getInteger(0).value = 2;
        dialog.getInteger(1).value = 1;
        dialog.getInteger(2).value = -1;
        test.readFromDialog();
        assertTrue(test.getError() == null);
    }
    @Test public void testRangeChangingBound()
    {
        ImageParameter image = getImageParam(2, 3);
        TParameter param = getTestParam(image);
        param.updateMaxT();
        TParameter.RangeT test = param.new RangeT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        image.addToDialog(dialog);
        test.addToDialog(dialog);

        dialog.getInteger(1).value = 3;
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getStringIndex(0).value = 1;
        image.readFromDialog();
        param.updateMaxT();
        test.readFromDialog();
        assertTrue(test.getError() == null);

        dialog.getStringIndex(0).value = 0;
        image.readFromDialog();
        param.updateMaxT();
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getInteger(0).value = 3;
        dialog.getInteger(1).value = 1;
        dialog.getInteger(2).value = -1;
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getStringIndex(0).value = 1;
        image.readFromDialog();
        param.updateMaxT();
        test.readFromDialog();
        assertTrue(test.getError() == null);

        dialog.getStringIndex(0).value = 0;
        image.readFromDialog();
        param.updateMaxT();
        test.readFromDialog();
        assertTrue(test.getError() != null);
    }

    @Test public void testContinuousInit()
    {
        TParameter param = getTestParam(1);
        TParameter.ContinuousT test = param.new ContinuousT();
        test.initialize();

        List<Integer> list = test.getValue();
        assertEquals(1, list.size());
        assertEquals(1, list.get(0).intValue());
    }
    @Test public void testContinuousUp()
    {
        TParameter param = getTestParam(1);
        TParameter.ContinuousT test = param.new ContinuousT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.addToDialog(dialog);
        dialog.getInteger(0).value = 2;
        dialog.getInteger(1).value = 5;
        test.readFromDialog();

        List<Integer> list = test.getValue();
        assertEquals(4, list.size());
        assertEquals(2, list.get(0).intValue());
        assertEquals(3, list.get(1).intValue());
        assertEquals(4, list.get(2).intValue());
        assertEquals(5, list.get(3).intValue());
    }
    @Test public void testContinuousDown()
    {
        TParameter param = getTestParam(1);
        TParameter.ContinuousT test = param.new ContinuousT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.addToDialog(dialog);
        dialog.getInteger(0).value = 6;
        dialog.getInteger(1).value = 4;
        test.readFromDialog();

        List<Integer> list = test.getValue();
        assertEquals(3, list.size());
        assertEquals(6, list.get(0).intValue());
        assertEquals(5, list.get(1).intValue());
        assertEquals(4, list.get(2).intValue());
    }
    @Test public void testContinuousBounds()
    {
        TParameter param = getTestParam(2);
        param.updateMaxT();
        TParameter.ContinuousT test = param.new ContinuousT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.addToDialog(dialog);

        assertTrue(test.getError() == null);

        dialog.getInteger(0).value = 3;
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getInteger(1).value = 3;
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getInteger(1).value = 2;
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getInteger(0).value = 2;
        test.readFromDialog();
        assertTrue(test.getError() == null);

        dialog.getInteger(1).value = 3;
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getInteger(0).value = 3;
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getInteger(0).value = 2;
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getInteger(1).value = 2;
        test.readFromDialog();
        assertTrue(test.getError() == null);
    }
    @Test public void testContinuousBoundsChange()
    {
        ImageParameter image = getImageParam(2, 3);
        TParameter param = getTestParam(image);
        param.updateMaxT();
        TParameter.ContinuousT test = param.new ContinuousT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        image.addToDialog(dialog);
        test.addToDialog(dialog);

        dialog.getInteger(0).value = 3;
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getStringIndex(0).value = 1;
        image.readFromDialog();
        param.updateMaxT();
        test.readFromDialog();
        assertTrue(test.getError() == null);

        dialog.getStringIndex(0).value = 0;
        image.readFromDialog();
        param.updateMaxT();
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getInteger(0).value = 1;
        dialog.getInteger(1).value = 3;
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getStringIndex(0).value = 1;
        image.readFromDialog();
        param.updateMaxT();
        test.readFromDialog();
        assertTrue(test.getError() == null);

        dialog.getStringIndex(0).value = 0;
        image.readFromDialog();
        param.updateMaxT();
        test.readFromDialog();
        assertTrue(test.getError() != null);
    }

    private ImageParameter getImageParam(Integer... numSlices)
    {
        ImagePlus[] imgs = new ImagePlus[numSlices.length];
        for (int i = 0; i < numSlices.length; ++i) {
            int numSlice = numSlices[i];
            ImageStack stack = new ImageStack(1, 1);
            for (int j = 0; j < numSlice; ++j) {
                stack.addSlice(new FloatProcessor(1, 1));
            }
            imgs[i] = new ImagePlus("", stack);
        }
        return new ImageParameter("", imgs);
    }
    private TParameter getTestParam(ImageParameter param)
    {
        return new TParameter(param, TParameter.PossibleTypes.All, "");
    }
    private TParameter getTestParam(Integer... numSlices)
    {
        return getTestParam(getImageParam(numSlices));
    }
}
