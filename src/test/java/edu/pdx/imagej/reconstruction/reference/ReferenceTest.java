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

import java.util.LinkedHashMap;

import ij.gui.PointRoi;

import edu.pdx.imagej.dynamic_parameters.TestDialog;
import edu.pdx.imagej.dynamic_parameters.HoldingParameter;

import edu.pdx.imagej.reconstruction.ConstReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionFieldImpl;
import edu.pdx.imagej.reconstruction.ComplexField;
import edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin;
import edu.pdx.imagej.reconstruction.filter.Filter;

public class ReferenceTest {
    @Test public void test_use_same_roi() {
        Reference test = new Reference(new TestPlugin());
        TestDialog dialog = new TestDialog();
        ReferenceParameter param = new ReferenceParameter(new TestParameter());
        test.M_param = param;
        param.initialize();
        param.add_to_dialog(dialog);
        dialog.get_boolean(0).value = true; // Set phase to true
        dialog.get_boolean(1).value = false; // Set amplitude to false
        dialog.get_boolean(2).value = true; // Set use_same_roi to true
        param.read_from_dialog();

        Filter normal_filter = new Filter();
        normal_filter.set_filter(new PointRoi(0, 0));
        Filter other_filter = new Filter();
        other_filter.set_filter(new PointRoi(new int[]{1, 1, 2, 2},
                                             new int[]{1, 2, 1, 2}, 4));
        LinkedHashMap<Class<?>, ReconstructionPlugin> plugins
            = new LinkedHashMap<>();
        plugins.put(Filter.class, normal_filter);
        test.read_plugins(plugins);
        test.set_not_same_filter(other_filter);

        ReconstructionField normal_field
            = new ReconstructionFieldImpl(real, imag);
        ReconstructionField other_field = normal_field.copy();
        normal_filter.filter_field(normal_field);
        other_filter.filter_field(other_field);
        ReconstructionField processed_normal_field = normal_field.copy();
        ReconstructionField processed_other_field = other_field.copy();
        // Setup is finally complete!

        test.process_filtered_field(processed_normal_field, 0);
        dialog.get_boolean(2).value = false; // Set use_same_roi to false
        param.read_from_dialog();
        test.process_filtered_field(processed_other_field, 0);

        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                double normal_amp = normal_field.field().get_amp()[x][y];
                double other_amp = other_field.field().get_amp()[x][y];
                double processed_normal_amp
                    = processed_normal_field.field().get_amp()[x][y];
                double processed_normal_arg
                    = processed_normal_field.field().get_arg()[x][y];
                double processed_other_amp
                    = processed_other_field.field().get_amp()[x][y];
                double processed_other_arg
                    = processed_other_field.field().get_arg()[x][y];
                String coord = "at (" + x + ", " + y + ")";
                assertEquals(normal_amp, processed_normal_amp, 1e-6, coord);
                assertEquals(other_amp, processed_other_amp, 1e-6, coord);
                assertEquals(processed_normal_arg, 0, 1e-6, coord);
                assertEquals(processed_other_arg, 0, 1e-6, coord);
            }
        }
    }
    // HoldingParameter has the fewest methods to override :/
    public static class TestParameter
                        extends HoldingParameter<ReferencePlugin> {
        public TestParameter() {super("");}
        @Override
        public ReferencePlugin get_value() {return new TestPlugin();}
    }
    public static class TestPlugin extends AbstractReferencePlugin {
        @Override
        public ReconstructionField get_reference_holo(
            ConstReconstructionField field, int t)
        {
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
