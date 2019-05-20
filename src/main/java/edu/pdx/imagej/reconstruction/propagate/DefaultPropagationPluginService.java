
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

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.scijava.plugin.AbstractPTService;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginInfo;
import org.scijava.service.Service;
import net.imagej.ImageJService;

@Plugin(type = PropagationPluginService.class)
public class DefaultPropagationPluginService
             extends AbstractPTService<PropagationPlugin>
             implements PropagationPluginService
{
    @Override
    public LinkedHashMap<String, PropagationPlugin> get_plugins()
    {
        LinkedHashMap<String, PropagationPlugin> result
            = new LinkedHashMap<>();
        for (HashMap.Entry<String, PluginInfo<PropagationPlugin>> entry :
                M_plugins.entrySet()) {
            String name = entry.getKey();
            PluginInfo<PropagationPlugin> info = entry.getValue();
            PropagationPlugin plugins = pluginService().createInstance(info);
            result.put(name, plugins);
        }
        return result;
    }
    @Override
    public void initialize()
    {
        for (PluginInfo<PropagationPlugin> info : getPlugins()) {
            String name = info.getName();
            if (name == null || name.isEmpty()) {
                name = info.getClassName();
            }
            M_plugins.put(name, info);
        }
    }
    @Override
    public Class<PropagationPlugin> getPluginType()
        {return PropagationPlugin.class;}

    private LinkedHashMap<String, PluginInfo<PropagationPlugin>> M_plugins
        = new LinkedHashMap<>();
}
