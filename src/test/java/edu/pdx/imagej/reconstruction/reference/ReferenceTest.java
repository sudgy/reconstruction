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

import java.util.ArrayList;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.PointRoi;
import ij.process.FloatProcessor;

import org.scijava.Context;
import org.scijava.plugin.PluginService;
import org.scijava.prefs.PrefService;

import edu.pdx.imagej.dynamic_parameters.TestDialog;
import edu.pdx.imagej.dynamic_parameters.HoldingParameter;
import edu.pdx.imagej.dynamic_parameters.ImageParameter;

import edu.pdx.imagej.reconstruction.ConstReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionFieldImpl;
import edu.pdx.imagej.reconstruction.ComplexField;
import edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin;
import edu.pdx.imagej.reconstruction.filter.Filter;

public class ReferenceTest {
    @Test public void testUseSameRoi() {
        Filter normalFilter = new Filter();
        normalFilter.setFilter(new PointRoi(0, 0));
        Filter otherFilter = new Filter();
        otherFilter.setFilter(new PointRoi(new int[]{1, 1, 2, 2},
                                             new int[]{1, 2, 1, 2}, 4));
        Reference test = new Reference(new TestPlugin(), true, false);
        ArrayList<ReconstructionPlugin> plugins = new ArrayList<>();
        plugins.add(normalFilter);
        test.readPlugins(plugins);

        ReconstructionField normalField
            = new ReconstructionFieldImpl(real, imag);
        ReconstructionField otherField = normalField.copy();
        normalFilter.filterField(normalField);
        otherFilter.filterField(otherField);
        ReconstructionField processedNormalField = normalField.copy();
        ReconstructionField processedOtherField = otherField.copy();
        // Setup is finally complete!

        test.processFilteredField(processedNormalField, 0);
        test = new Reference(new TestPlugin(), true, false, otherFilter);
        test.readPlugins(plugins);
        test.processFilteredField(processedOtherField, 0);

        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                double normalAmp = normalField.field().getAmp()[x][y];
                double otherAmp = otherField.field().getAmp()[x][y];
                double processedNormalAmp
                    = processedNormalField.field().getAmp()[x][y];
                double processedNormalArg
                    = processedNormalField.field().getArg()[x][y];
                double processedOtherAmp
                    = processedOtherField.field().getAmp()[x][y];
                double processedOtherArg
                    = processedOtherField.field().getArg()[x][y];
                String coord = "at (" + x + ", " + y + ")";
                assertEquals(normalAmp, processedNormalAmp, 1e-6, coord);
                assertEquals(otherAmp, processedOtherAmp, 1e-6, coord);
                assertEquals(processedNormalArg, 0, 1e-6, coord);
                assertEquals(processedOtherArg, 0, 1e-6, coord);
            }
        }
    }
    @Test public void testLive()
    {
        Context context = new Context(PluginService.class, PrefService.class);
        Reference test = new Reference();
        context.inject(test.param());
        TestDialog dialog = new TestDialog();
        test.param().initialize();
        test.param().addToDialog(dialog);
        dialog.getString(0).value = "Self";
        test.param().readFromDialog();
        dialog = new TestDialog();
        test.param().addToDialog(dialog);
        dialog.getBoolean(0).value = true; // Set phase to true
        dialog.getBoolean(1).value = false; // Set amplitude to false
        // Use same roi does nothing
        test.param().readFromDialog();

        Filter filter = new Filter();
        filter.setFilter(new PointRoi(new int[]{1, 1, 2, 2},
                                       new int[]{1, 2, 1, 2}, 4));
        ReconstructionField field = new ReconstructionFieldImpl(real, imag);
        filter.filterField(field);
        ReconstructionField origField = field.copy();
        filter = new Filter();
        filter.setFilter(new PointRoi(new int[]{0, 0, 0, 1, 1, 1, 2, 2, 2},
                                       new int[]{0, 1, 2, 0, 1, 2, 0, 1, 2},9));
        test.M_notSameFilter = filter;
        // Setup is finally complete!

        test.processFilteredField(field, 0);

        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                double amp1 = origField.field().getAmp()[x][y];
                double amp2 = field.field().getAmp()[x][y];
                double arg1 = origField.field().getArg()[x][y];
                double arg2 = field.field().getArg()[x][y];
                String coord = "at (" + x + ", " + y + ")";
                assertEquals(amp1, amp2, 1e-6, coord);
                assertTrue(Math.abs(0 - arg1) > 1e-6, coord);
                assertEquals(0, arg2, 1e-6, coord);
            }
        }
    }
    // Test that processFilteredField always produces the same result.  This
    // was a bug.
    @Test public void testConstant()
    {
        float[][] ref1 = {
            {0.8447616701F, 0.4280179246F, 0.6236040991F},
            {0.5490310073F, 0.8402175080F, 0.2656365367F},
            {0.9427865837F, 0.6489800130F, 0.0992594036F}
        };
        float[][] ref2 = {
            {0.9998434794F, 0.5681384218F, 0.7113728796F},
            {0.6662169492F, 0.0381267463F, 0.9366164646F},
            {0.9904258024F, 0.0142653695F, 0.1162896629F}
        };
        ImageStack refStack = new ImageStack(3, 3);
        refStack.addSlice(new FloatProcessor(ref1));
        refStack.addSlice(new FloatProcessor(ref2));
        ImagePlus refImage = new ImagePlus("", refStack);
        Filter filter = new Filter();
        filter.setFilter(new PointRoi(new int[]{1, 1, 2, 2},
                                       new int[]{1, 2, 1, 2}, 4));
        Reference test = new Reference(new TestPlugin2(refImage), true, false);
        ArrayList<ReconstructionPlugin> plugins = new ArrayList<>();
        plugins.add(filter);
        test.readPlugins(plugins);
        ReconstructionField field1 = new ReconstructionFieldImpl(real, imag);
        ReconstructionField field2 = field1.copy();
        ReconstructionField field3 = field1.copy();
        ReconstructionField field4 = field1.copy();

        test.processFilteredField(field1, 1);
        test.processFilteredField(field2, 1);
        test.processFilteredField(field3, 2);
        test.processFilteredField(field4, 2);

        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                String coord = "(" + x + ", " + y + ")";
                double[][] f1 = field1.field().getField();
                double[][] f2 = field2.field().getField();
                double[][] f3 = field3.field().getField();
                double[][] f4 = field4.field().getField();
                assertEquals(f1[x][2*y], f2[x][2*y], coord);
                assertEquals(f1[x][2*y+1], f2[x][2*y+1], coord);
                assertEquals(f3[x][2*y], f4[x][2*y], coord);
                assertEquals(f3[x][2*y+1], f4[x][2*y+1], coord);
                assertTrue(f1[x][2*y] != f3[x][2*y], coord);
                assertTrue(f1[x][2*y+1] != f3[x][2*y+1], coord);
            }
        }
    }
    public static class TestPlugin extends AbstractReferencePlugin {
        @Override
        public ReconstructionField getReferenceHolo(
            ConstReconstructionField field, int t)
        {
            return new ReconstructionFieldImpl(real, imag);
        }
        @Override public TestPlugin duplicate() {return new TestPlugin();}
    }
    public static class TestPlugin2 extends AbstractReferencePlugin {
        public TestPlugin2(ImagePlus refImage)
        {
            M_1 = create(refImage, 1);
            M_2 = create(refImage, 2);
        }
        @Override
        public ReconstructionField getReferenceHolo(
            ConstReconstructionField field, int t)
        {
            if (t == 1) return M_1;
            else return M_2;
        }
        private TestPlugin2(ReconstructionField m1, ReconstructionField m2)
        {
            M_1 = m1;
            M_2 = m2;
        }
        @Override public TestPlugin2 duplicate()
            {return new TestPlugin2(M_1, M_2);}
        private ReconstructionField M_1;
        private ReconstructionField M_2;
        private ReconstructionField create(ImagePlus refImage, int t)
        {
            float[][] arr = refImage.getStack()
                                     .getProcessor(t)
                                     .getFloatArray();
            double[][] real = new double[arr.length][arr[0].length];
            double[][] imag = new double[arr.length][arr[0].length];
            for (int x = 0; x < arr.length; ++x) {
                for (int y = 0; y < arr.length; ++y) {
                    real[x][y] = arr[x][y];
                }
            }
            return new ReconstructionFieldImpl(real, imag);
        }
    }
    private static double[][] real = {
        {0.5542820629, 0.1879272540, 0.8584170661},
        {0.7808111477, 0.6247602260, 0.6811765293},
        {0.3942249921, 0.1238077507, 0.1966343374},
    };
    private static double[][] imag = {
        {0.1062510323, 0.5731869642, 0.2101399789},
        {0.2327204311, 0.9912915487, 0.5350163478},
        {0.1526346228, 0.2690232265, 0.7611883011},
    };
}
