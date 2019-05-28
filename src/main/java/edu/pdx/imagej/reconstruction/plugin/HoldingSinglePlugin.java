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

import edu.pdx.imagej.dynamic_parameters.PluginParameter;

public class HoldingSinglePlugin<T extends ReconstructionPlugin>
             extends AbstractHoldingPlugin<T> {
    public HoldingSinglePlugin(String label, Class<T> cls)
    {
        M_param = new PluginParameter<T>(label, cls);
    }
    public HoldingSinglePlugin(T plugin)
    {
        M_plugin = plugin;
    }
    @Override
    public PluginParameter<T> param() {return M_param;}
    public T get_plugin()
    {
        if (M_plugin == null) M_plugin = M_param.get_value();
        return M_plugin;
    }
    @Override
    public Iterable<T> get_plugins()
    {
        return Collections.singleton(get_plugin());
    }
    @Override public void sort_plugins() {}

    private PluginParameter<T> M_param;
    private T M_plugin;
}
