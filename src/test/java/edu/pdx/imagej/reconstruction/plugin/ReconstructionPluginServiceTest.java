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
import org.scijava.prefs.PrefService;

public class ReconstructionPluginServiceTest {
    @Test public void testGetMain()
    {
        TestReconstructionPluginService test
            = new TestReconstructionPluginService(TestA.class, TestB.class);
        S_context.inject(test);
        test.enable(TestA.class);
        test.enable(TestB.class);
        List<MainReconstructionPlugin> plugins = test.getMainPlugins();
        assertEquals(plugins.size(), 1, "ReconstructionPluginService should "
            + "only get MainReconstructionPlugins.");
        assertTrue(plugins.get(0) instanceof TestA,
            "ReconstructionPluginService should get a particular plugin type.");
    }
    @Test public void testBadPlugin()
    {
        TestReconstructionPluginService test
            = new TestReconstructionPluginService(TestA.class, TestC.class);
        S_context.inject(test);
        test.enable(TestA.class);
        test.enable(TestB.class);
        try {
            test.getMainPlugins();
            assertTrue(false, "A bad plugin should cause an exception.");
        }
        catch (RuntimeException e) {}
    }
    @Test public void testEnable()
    {
        TestReconstructionPluginService test =
            new TestReconstructionPluginService(TestA.class, TestB.class);
        S_context.inject(test);
        test.enable(TestA.class);
        test.enable(TestB.class);
        assertTrue(test.isEnabled(TestA.class));
        assertTrue(test.isEnabled(TestB.class));
        test.disable(TestB.class);
        assertTrue(!test.isEnabled(TestB.class));
        test.enable(TestB.class);
        assertTrue(test.isEnabled(TestB.class));
        test.disable(TestA.class);
        List<MainReconstructionPlugin> plugins = test.getMainPlugins();
        assertEquals(0, plugins.size());
        test.enable(TestA.class);
        plugins = test.getMainPlugins();
        assertEquals(1, plugins.size());
        assertTrue(plugins.get(0) instanceof TestA);
    }
    @Test public void testGetType()
    {
        TestReconstructionPluginService test =
            new TestReconstructionPluginService(TestB.class, TestD.class);
        S_context.inject(test);
        test.disable(TestB.class);
        List<SubReconstructionPlugin> plugins
            = test.getAllPlugins(SubReconstructionPlugin.class);
        assertEquals(2, plugins.size());
        assertTrue(plugins.get(0) instanceof TestB
                || plugins.get(0) instanceof TestD);
        if (plugins.get(0) instanceof TestB) {
            assertTrue(plugins.get(1) instanceof TestD);
        }
        else {
            assertTrue(plugins.get(1) instanceof TestB);
        }
        test.enable(TestB.class);
    }
    private static Context S_context = new Context(PluginService.class,
                                                   PrefService.class);

    public static abstract class TestPlugin extends AbstractRichPlugin
                                            implements ReconstructionPlugin {
        @Override public TestPlugin duplicate() {return null;}
    }
    @Plugin(type = TestPlugin.class)
    public static class TestA extends TestPlugin
                              implements MainReconstructionPlugin {}
    @Plugin(type = TestPlugin.class)
    public static class TestB extends TestPlugin
                              implements SubReconstructionPlugin {}
    @Plugin(type = TestPlugin.class)
    public static class TestC extends TestPlugin
                              implements ReconstructionPlugin {}
    @Plugin(type = TestPlugin.class)
    public static class TestD extends TestPlugin
                              implements SubReconstructionPlugin {}
}

class TestReconstructionPluginService extends DefaultReconstructionPluginService
{
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
