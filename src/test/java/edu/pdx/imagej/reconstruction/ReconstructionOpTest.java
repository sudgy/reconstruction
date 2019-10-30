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

import java.util.ArrayList;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.PointRoi;
import ij.process.FloatProcessor;

import org.scijava.Context;
import org.scijava.Priority;
import org.scijava.app.StatusService;
import org.scijava.ui.UIService;
import net.imagej.ops.OpService;

import edu.pdx.imagej.reconstruction.plugin.AbstractReconstructionPlugin;
import edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin;
import edu.pdx.imagej.reconstruction.units.DistanceUnits;
import edu.pdx.imagej.reconstruction.units.DistanceUnitValue;
import edu.pdx.imagej.reconstruction.filter.Filter;
import edu.pdx.imagej.reconstruction.reference.Reference;
import edu.pdx.imagej.reconstruction.reference.Self;
import edu.pdx.imagej.reconstruction.poly_tilt.PolyTilt;
import edu.pdx.imagej.reconstruction.poly_tilt.Auto;
import edu.pdx.imagej.reconstruction.propagation.Propagation;
import edu.pdx.imagej.reconstruction.propagation.AngularSpectrum;
import edu.pdx.imagej.reconstruction.result.Result;
import edu.pdx.imagej.reconstruction.result.ResultOptions;

// This is basically the one system test we have here.
public class ReconstructionOpTest {
    @Test public void testOp()
    {
        // The question is, what can we test that we can know perfectly?  We
        // make everything really simple.  We use most of the plugins, but in
        // simple ways.  Here are the plugins we will use:
        //  - Filter: We can actually use pretty much any filter here.
        //  - Reference: We use a reference hologram to cancel out the phase,
        //               but it's actually offset so that there is a linear
        //               tilt.
        //  - PolyTilt: Apply a linear correction so that phase is completely
        //              removed.
        //  - We add a special plugin here to make amplitude one.
        //  - Propagation: With everything equal to 1 + 0i, propagation just
        //                 changes phase, so we check that.
        //  - Result: We get the amplitude and phase with custom ResultOptions
        //            functions.

        // Initialize normal things
        ImageStack stack = new ImageStack(8, 8);
        stack.addSlice(new FloatProcessor(new float[][] {
            {0.3329F,0.8189F,0.4099F,0.2558F,0.1270F,0.7001F,0.9656F,0.5523F},
            {0.6552F,0.9121F,0.2803F,0.3362F,0.7305F,0.6633F,0.7680F,0.1141F},
            {0.1148F,0.3833F,0.5768F,0.6974F,0.7748F,0.2299F,0.2146F,0.5582F},
            {0.3408F,0.3469F,0.3234F,0.2921F,0.4411F,0.7914F,0.4770F,0.1511F},
            {0.2783F,0.9917F,0.3320F,0.6592F,0.3511F,0.7558F,0.9489F,0.5393F},
            {0.8627F,0.1474F,0.5944F,0.5100F,0.8751F,0.9530F,0.9537F,0.6698F},
            {0.2177F,0.3435F,0.5747F,0.7085F,0.2513F,0.5606F,0.1815F,0.7103F},
            {0.8881F,0.2867F,0.5842F,0.8933F,0.4831F,0.3560F,0.9298F,0.0030F}
        }));
        stack.addSlice(new FloatProcessor(new float[][] {
            {0.1770F,0.4005F,0.2051F,0.3702F,0.3514F,0.1223F,0.0934F,0.9360F},
            {0.3860F,0.7909F,0.3634F,0.3395F,0.9489F,0.7538F,0.4692F,0.0944F},
            {0.1893F,0.0364F,0.9772F,0.4683F,0.8221F,0.1398F,0.9658F,0.5278F},
            {0.7714F,0.5167F,0.3950F,0.1335F,0.3802F,0.7772F,0.3746F,0.8431F},
            {0.7955F,0.0544F,0.6983F,0.2012F,0.8137F,0.6930F,0.6942F,0.7103F},
            {0.6626F,0.0785F,0.1503F,0.6658F,0.5708F,0.6446F,0.3143F,0.2893F},
            {0.4042F,0.5552F,0.0188F,0.5900F,0.3314F,0.1948F,0.9084F,0.3802F},
            {0.6591F,0.8361F,0.7728F,0.7123F,0.0678F,0.2890F,0.4949F,0.7139F}
        }));
        ImagePlus hologram = new ImagePlus("", stack);
        DistanceUnitValue wavelength =
            new DistanceUnitValue(500, DistanceUnits.Meter);
        DistanceUnitValue width = wavelength;
        DistanceUnitValue height = wavelength;
        ArrayList<Integer> ts = new ArrayList<>();
        ts.add(1);
        ts.add(2);
        ArrayList<DistanceUnitValue> zs = new ArrayList<>();
        for (int i = -5; i <= 5; ++i) {
            zs.add(new DistanceUnitValue(i * 10, DistanceUnits.Meter));
        }
        // Initialize plugins
        ArrayList<ReconstructionPlugin> plugins = new ArrayList<>();
        // Filter
        Filter filter = new Filter();
        filter.setFilter(new PointRoi(
            new int[]{0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3},
            new int[]{0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3}, 16));
        plugins.add(filter);
        // Reference Hologram
        Filter newFilter = new Filter();
        newFilter.setFilter(new PointRoi(
            new int[]{2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 0},
            new int[]{2, 3, 4, 5, 2, 3, 4, 5, 2, 3, 4, 5, 2, 3, 4, 5, 0}, 17));
        plugins.add(new Reference(new Self(), true, false, newFilter));
        // Polynomial Tilt Correction
        plugins.add(new PolyTilt(new Auto(), 1));
        plugins.add(new AbstractReconstructionPlugin() {
            @Override
            public void setBeginningPriority()
            {
                setPriority(-999999999999.0);
            }
            @Override
            public void processFilteredField(ReconstructionField field, int t)
            {
                double[][] array = field.field().getField();
                for (int x = 0; x < 8; ++x) {
                    for (int y = 0; y < 8; ++y) {
                        double real = array[x][y*2];
                        double imag = array[x][y*2+1];
                        double abs = Math.sqrt(real*real + imag*imag);
                        array[x][y*2] /= abs;
                        array[x][y*2+1] /= abs;
                    }
                }
            }
            @Override
            public ReconstructionPlugin duplicate() {return null;}
        });
        // Propagation
        plugins.add(new Propagation(new AngularSpectrum()));
        // Result
        ImagePlus[] result = new ImagePlus[2];
        ResultOptions options = new ResultOptions();
        options.amplitude = true;
        options.phase = true;
        options.type = ResultOptions.Type.Type32Bit;
        options.amplitudeFunc = (ImagePlus imp) -> result[0] = imp;
        options.phaseFunc = (ImagePlus imp) -> result[1] = imp;
        plugins.add(new Result(options));

        // Run it!
        Context context = new Context(OpService.class, StatusService.class,
                                      UIService.class);
        OpService ops = context.getService(OpService.class);
        ops.run(ReconstructionOp.class, hologram, wavelength, width, height,
                                        ts, zs, plugins);
        ImagePlus amp = result[0];
        ImagePlus arg = result[1];
        for (int x = 0; x < 8; ++x) {
            for (int y = 0; y < 8; ++y) {
                for (int t = 1; t <= 2; ++t) {
                    /*
                    double constAmp
                        = amp.getStack()
                             .getProcessor(amp.getStackIndex(1, 1, t))
                             .getFloatArray()[x][y];
                             */
                    for (int z = 1; z <= 11; ++z) {
                        double thisAmp
                            = amp.getStack()
                                 .getProcessor(amp.getStackIndex(1, z, t))
                                 .getFloatArray()[x][y];
                        double thisArg
                            = arg.getStack()
                                 .getProcessor(arg.getStackIndex(1, z, t))
                                 .getFloatArray()[x][y];
                        String coord = "(" + x + ", " + y + ", " + (z - 6) * 10
                                       + ", " + t + ")";
                        assertEquals(1.0, thisAmp, 1e-6, coord);
                        assertEquals((z - 6) * Math.PI / 25.0, thisArg, 1e-6, coord);
                    }
                }
            }
        }
    }
}
