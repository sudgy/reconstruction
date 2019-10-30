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

import org.scijava.Context;
import org.scijava.plugin.PluginService;

import edu.pdx.imagej.dynamic_parameters.HoldingParameter;
import edu.pdx.imagej.dynamic_parameters.ChoiceParameter;
import edu.pdx.imagej.dynamic_parameters.TestDialog;

import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.ConstReconstructionField;
import edu.pdx.imagej.reconstruction.plugin.ReconstructionPluginParameter;
import edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin;

public class ReferenceParameterTest {
    @Test public void testVisibilities()
    {
        Context context = new Context(PluginService.class);
        TestDialog dialog = new TestDialog();
        ReferenceParameter test = new ReferenceParameter(new TestParameter());
        context.inject(test);
        test.initialize();

        test.addToDialog(dialog);
        dialog.getString(0).value = "None";
        test.readFromDialog();
        test.refreshVisibility();

        assertTrue(!test.M_phase       .visible(), "Reference hologram options "
            + "should not be visible when not using a reference hologram.");
        assertTrue(!test.M_amplitude   .visible(), "Reference hologram options "
            + "should not be visible when not using a reference hologram.");
        assertTrue(!test.M_useSameRoi.visible(), "Reference hologram options "
            + "should not be visible when not using a reference hologram.");

        dialog.getString(0).value = "A";
        test.readFromDialog();
        test.refreshVisibility();

        assertTrue(test.M_phase       .visible(), "Reference hologram options "
            + "should be visible when using a reference hologram.");
        assertTrue(test.M_amplitude   .visible(), "Reference hologram options "
            + "should be visible when using a reference hologram.");
        assertTrue(test.M_useSameRoi.visible(), "Reference hologram options "
            + "should be visible when using a reference hologram.");
    }

    public static class TestParameter
                        extends HoldingParameter<ReferencePlugin> {
        public TestParameter() {super("");}
        @Override
        public void initialize()
        {
            String[] choices = {"None", "A"};
            M_choice = addParameter(new ChoiceParameter("", choices));
        }
        @Override
        public ReferencePlugin getValue()
        {
            if (M_choice.getValue().equals("None")) return new None();
            else return new AbstractReferencePlugin() {
                @Override
                public ReconstructionField getReferenceHolo(
                    ConstReconstructionField field, int t) {return null;}
                @Override
                public ReconstructionPlugin duplicate() {return null;}
            };
        }
        private ChoiceParameter M_choice;
    }
}
