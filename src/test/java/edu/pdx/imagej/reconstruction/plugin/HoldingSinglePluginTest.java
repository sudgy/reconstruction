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

package edu.pdx.imagej.reconstruction.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import org.scijava.Context;
import org.scijava.Priority;
import org.scijava.plugin.AbstractRichPlugin;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginService;
import org.scijava.prefs.PrefService;

import edu.pdx.imagej.dynamic_parameters.DParameter;
import edu.pdx.imagej.dynamic_parameters.IntParameter;
import edu.pdx.imagej.dynamic_parameters.ImageParameter;
import edu.pdx.imagej.dynamic_parameters.TestDialog;

public class HoldingSinglePluginTest {
    @Test public void test_dialog()
    {
        HoldingSinglePlugin<TestPlugin> test
            = new HoldingSinglePlugin<>("C", TestPlugin.class);
        TestDialog dialog = new TestDialog();
        Context context = new Context(PluginService.class, PrefService.class);
        context.inject(test.param());
        test.param().initialize();
        test.param().add_to_dialog(dialog);
        assertEquals(dialog.get_string(0).value, "B", "HoldingSinglePlugin "
            + "should discover plugins and should start with the "
            + "highest-priority plugin.");
        assertTrue(test.param().get_value() instanceof TestB,
            "HoldingSinglePlugin should return a correct parameter.");

        dialog.get_string(0).value = "A";
        test.param().read_from_dialog();
        assertTrue(test.param().get_value() instanceof TestA,
            "HoldingSinglePlugin should change the parameter correctly.");
    }
    @Test public void test_process()
    {
        HoldingSinglePlugin<TestPlugin> test
            = new HoldingSinglePlugin<>("C", TestPlugin.class);
        Context context = new Context(PluginService.class, PrefService.class);
        context.inject(test.param());
        test.param().initialize();
        ((HologramPluginParameter)test.param()).set_hologram(null);
        test.process_beginning();

        assertTrue(test.param().get_value().M_processed);
        assertTrue(test.param().get_value().M_hologrammed);

        TestDialog dialog = new TestDialog();
        test.param().add_to_dialog(dialog);
        dialog.get_string(0).value = "A";
        test.param().read_from_dialog();
        assertTrue(!test.param().get_value().M_processed, "Non-selected plugins"
            + " should not get processed.");
        assertTrue(test.param().get_value().M_hologrammed, "Non-selected "
            + "plugins should still see the hologram.");
    }
    public static abstract class TestPlugin extends    AbstractRichPlugin
                                            implements SubReconstructionPlugin {
        @Override
        public void process_beginning()
        {
            M_processed = true;
        }
        @Override
        public DParameter<?> param() {return M_param;}
        public boolean M_processed = false;
        public boolean M_hologrammed = false;

        // It extends IntParameter just to extend from some DParameter
        private class HoloParam extends IntParameter
                                implements HologramPluginParameter {
            public HoloParam() {super(0, "");}
            @Override
            public void set_hologram(ImageParameter hologram)
            {
                M_hologrammed = true;
            }
        }
        private HoloParam M_param = new HoloParam();
    }
    @Plugin(type = TestPlugin.class, name = "A")
    public static class TestA extends TestPlugin {}
    @Plugin(type = TestPlugin.class, name = "B", priority = Priority.HIGH)
    public static class TestB extends TestPlugin {}
}
