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

package edu.pdx.imagej.reconstruction.propagation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import ij.ImagePlus;

import edu.pdx.imagej.reconstruction.ConstReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.units.DistanceUnits;
import edu.pdx.imagej.reconstruction.units.DistanceUnitValue;

public class PropagationTest {
    @Test public void testProcessed()
    {
        TestPlugin plugin = new TestPlugin();
        Propagation prop = new Propagation(plugin);

        ImagePlus hologram = new ImagePlus();
        DistanceUnitValue wavelength
            = new DistanceUnitValue(100, DistanceUnits.Micro);
        DistanceUnitValue width
            = new DistanceUnitValue(200, DistanceUnits.Micro);
        DistanceUnitValue height
            = new DistanceUnitValue(300, DistanceUnits.Micro);

        prop.processHologramParam(hologram);
        prop.processWavelengthParam(wavelength);
        prop.processDimensionsParam(width, height);
        prop.processBeginning();

        assertEquals(plugin.M_hologram, hologram);
        assertEquals(plugin.M_wavelength.asMicro(), 100);
        assertEquals(plugin.M_width.asMicro(), 200);
        assertEquals(plugin.M_height.asMicro(), 300);
        assertTrue(plugin.M_processedBeginning);

        prop.processPropagatedField(null, 0, width);
        assertTrue(plugin.M_processed);
        assertTrue(plugin.M_propagated);
        plugin.M_processed = false;
        plugin.M_propagated = false;

        prop.processPropagatedField(null, 0, width);
        assertTrue(!plugin.M_processed, "First frame processing should not "
            + "happen on later frames.");
        assertTrue(plugin.M_propagated);
        plugin.M_propagated = false;

        prop.processPropagatedField(null, 1, width);
        assertTrue(plugin.M_processed, "First frame processing should happen "
            + "on a later first frame.");
        assertTrue(plugin.M_propagated);
    }

    private static class TestPlugin extends AbstractPropagationPlugin {
        @Override
        public void processHologramParam(ImagePlus hologram)
        {
            M_hologram = hologram;
        }
        @Override
        public void processWavelengthParam(DistanceUnitValue wavelength)
        {
            M_wavelength = wavelength;
        }
        @Override
        public void processDimensionsParam(DistanceUnitValue width,
                                             DistanceUnitValue height)
        {
            M_width = width;
            M_height = height;
        }
        @Override
        public void processBeginning()
        {
            M_processedBeginning = true;
        }
        @Override
        public void processStartingField(ConstReconstructionField field)
        {
            M_processed = true;
        }
        @Override
        public void propagate(
            ConstReconstructionField originalField,
            DistanceUnitValue z,
            ReconstructionField field,
            DistanceUnitValue lastZ)
        {
            M_propagated = true;
        }
        @Override
        public TestPlugin duplicate() {return new TestPlugin();}

        public ImagePlus M_hologram;
        public DistanceUnitValue M_wavelength;
        public DistanceUnitValue M_width;
        public DistanceUnitValue M_height;

        public boolean M_processedBeginning = false;
        public boolean M_processed = false;
        public boolean M_propagated = false;
    }
}
