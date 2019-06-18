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

import java.util.ArrayList;
import java.util.List;

import org.scijava.InstantiableException;
import org.scijava.plugin.AbstractPTService;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginInfo;
import org.scijava.service.Service;
import net.imagej.ImageJService;

/** A service for {@link ReconstructionPlugin}s.  You probably don't need to use
 * it, because it is used mostly internally.
 */
@Plugin(type = Service.class)
public class ReconstructionPluginService
             extends    AbstractPTService<ReconstructionPlugin>
             implements ImageJService {
    /** Get all {@link MainReconstructionPlugin}s.  If this method finds any
     * {@link ReconstructionPlugin} that is not a {@link
     * MainReconstructionPlugin} or {@link SubReconstructionPlugin}, it will
     * throw an exception.
     *
     * @return An instance of all <code>MainReconstructionPlugin</code>s.
     */
    public List<ReconstructionPlugin> get_plugins()
    {
        ArrayList<ReconstructionPlugin> result = new ArrayList<>();
        for (PluginInfo<ReconstructionPlugin> info : getPlugins()) {
            Class<?> cls;
            try {
                cls = info.loadClass();
            }
            catch (InstantiableException e) {
                throw new RuntimeException(e);
            }
            if (MainReconstructionPlugin.class.isAssignableFrom(cls)) {
                ReconstructionPlugin plugin
                    = pluginService().createInstance(info);
                result.add(plugin);
            }
            else if (!SubReconstructionPlugin.class.isAssignableFrom(cls)) {
                throw new RuntimeException(cls.getName() + " must inherit from "
                    + "either MainReconstructionPlugin or "
                    + "SubReconstructionPlugin.");
            }
        }
        return result;
    }
    @Override
    public Class<ReconstructionPlugin> getPluginType()
        {return ReconstructionPlugin.class;}
}
