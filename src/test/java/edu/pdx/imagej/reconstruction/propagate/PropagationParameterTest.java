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

import org.scijava.Context;

import edu.pdx.imagej.dynamic_parameters.TestDialog;

public class PropagationParameterTest {
    @Test public void test_single()
    {
        Context context = new Context(PropagationPluginService.class);
        TestPropagationPluginService plugins
            = (TestPropagationPluginService)context.getService(
                PropagationPluginService.class);
        plugins.M_plugins.put("A", new AngularSpectrum());
        PropagationParameter param = new PropagationParameter();
        context.inject(param);
        param.initialize();

        TestDialog dialog = new TestDialog();
        param.add_to_dialog(dialog);
        try {
            dialog.get_string(0);
            assertTrue(false, "PropagationParameter should not give a choice "
                + "when there is only one thing to choose from.");
        }
        catch (IndexOutOfBoundsException e) {}
    }
    @Test public void test_multi()
    {
        Context context = new Context(PropagationPluginService.class);
        TestPropagationPluginService plugins
            = (TestPropagationPluginService)context.getService(
                PropagationPluginService.class);
        plugins.M_plugins.put("A", new AngularSpectrum());
        plugins.M_plugins.put("B", new AngularSpectrum());
        PropagationParameter param = new PropagationParameter();
        context.inject(param);
        param.initialize();

        TestDialog dialog = new TestDialog();
        param.add_to_dialog(dialog);
        assertEquals(dialog.get_string(0).get(), "A", "PropagationParameter "
            + "should display choices in the order that they are in the list.");
    }
}
