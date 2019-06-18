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

import java.util.ArrayList;
import java.util.List;

import org.scijava.Context;
import org.scijava.plugin.AbstractRichPlugin;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginInfo;
import org.scijava.plugin.PluginService;

public class ReconstructionPluginServiceTest {
    @Test public void test_get_main()
    {
        Context context = new Context(PluginService.class);
        TestReconstructionPluginService test
            = new TestReconstructionPluginService(TestA.class, TestB.class);
        context.inject(test);
        List<ReconstructionPlugin> plugins
            = test.get_plugins();
        assertEquals(plugins.size(), 1, "ReconstructionPluginService should "
            + "only get MainReconstructionPlugins.");
        assertTrue(plugins.get(0) instanceof TestA,
            "ReconstructionPluginService should get a particular plugin type.");
    }
    @Test public void test_bad_plugin()
    {
        Context context = new Context(PluginService.class);
        TestReconstructionPluginService test
            = new TestReconstructionPluginService(TestA.class, TestC.class);
        context.inject(test);
        try {
            test.get_plugins();
            assertTrue(false, "A bad plugin should cause an exception.");
        }
        catch (RuntimeException e) {}
    }

    public static abstract class TestPlugin extends AbstractRichPlugin {}
    @Plugin(type = TestPlugin.class)
    public static class TestA extends TestPlugin
                              implements MainReconstructionPlugin {}
    @Plugin(type = TestPlugin.class)
    public static class TestB extends TestPlugin
                              implements SubReconstructionPlugin {}
    @Plugin(type = TestPlugin.class)
    public static class TestC extends TestPlugin
                              implements ReconstructionPlugin {}
}

class TestReconstructionPluginService extends ReconstructionPluginService {
    @SafeVarargs
    public TestReconstructionPluginService(
        Class<? extends ReconstructionPlugin>... classes)
    {
        for (Class<? extends ReconstructionPlugin> cls : classes) {
            M_plugins.add(new PluginInfo<ReconstructionPlugin>(
                                            cls, ReconstructionPlugin.class));
        }
    }
    @Override
    public List<PluginInfo<ReconstructionPlugin>> getPlugins()
    {
        return M_plugins;
    }
    private List<PluginInfo<ReconstructionPlugin>> M_plugins
        = new ArrayList<>();
}
