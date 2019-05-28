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

package edu.pdx.imagej.reconstruction;

import java.util.LinkedHashMap;
import java.util.ArrayList;

import org.scijava.plugin.AbstractPTService;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginInfo;
import org.scijava.service.Service;
import net.imagej.ImageJService;

@Plugin(type = Service.class)
public class ReconstructionPluginService
             extends    AbstractPTService<ReconstructionPlugin>
             implements ImageJService {
    public LinkedHashMap<Class<?>, ReconstructionPlugin> get_plugins()
    {
        LinkedHashMap<Class<?>, ReconstructionPlugin> result =
            new LinkedHashMap<>();
        for (PluginInfo<ReconstructionPlugin> info : getPlugins()) {
            if (MainReconstructionPlugin.class.isAssignableFrom(
                                                    info.getPluginClass())) {
                ReconstructionPlugin plugin
                    = pluginService().createInstance(info);
                result.put(plugin.getClass(), plugin);
            }
        }
        return result;
    }
    @Override
    public Class<ReconstructionPlugin> getPluginType()
        {return ReconstructionPlugin.class;}
}
