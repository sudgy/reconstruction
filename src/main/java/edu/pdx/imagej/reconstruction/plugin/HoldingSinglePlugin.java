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

import java.util.Collections;

import edu.pdx.imagej.dynamic_parameters.DParameter;
import edu.pdx.imagej.dynamic_parameters.PluginParameter;

/** A {@link HoldingPlugin} that holds a single plugin.  This is actually an
 * extremely useful class, with many of the default plugins extending it.
 *
 * @param <T> The type of plugin you are holding
 */
public class HoldingSinglePlugin<T extends ReconstructionPlugin>
             extends AbstractHoldingPlugin<T> {
    /** Constructor intended for live use of this plugin.
     *
     * @param label The label to use on the dialog for the parameter.
     * @param cls The class for the type of plugin you are holding.
     */
    public HoldingSinglePlugin(String label, Class<T> cls)
    {
        M_param = new ReconstructionPluginParameter<T>(label, cls);
    }
    /** Constructor intended for programmatic use of this plugin.
     *
     * @param plugin The particular plugin to use.
     */
    public HoldingSinglePlugin(T plugin)
    {
        M_plugin = plugin;
    }
    @Override
    public DParameter<T> param() {return M_param;}
    /** Get the plugin this is holding.  If the live constructor was used, this
     * will be the plugin retrieved from the parameter.  If the programmatic
     * constructor was used, it will be the plugin passed in there.
     *
     * @return The plugin this plugin is holding.
     */
    public T get_plugin()
    {
        if (M_plugin == null) M_plugin = M_param.get_value();
        return M_plugin;
    }
    /** This just returns a singleton of {@link get_plugin}.
     *
     * @return An Iterable of just the single plugin this is holding.
     */
    @Override
    public Iterable<T> get_plugins()
    {
        return Collections.singleton(get_plugin());
    }
    /** Does nothing, because one thing does not need to be sorted. */
    @Override public void sort_plugins() {}

    private ReconstructionPluginParameter<T> M_param;
    private T M_plugin;
}
