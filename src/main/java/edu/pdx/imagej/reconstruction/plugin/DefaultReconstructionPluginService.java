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
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginInfo;
import org.scijava.prefs.PrefService;
import org.scijava.service.Service;
import net.imagej.ImageJService;

import edu.pdx.imagej.dynamic_parameters.PluginParameter;

/** The default implementation of {@link ReconstructionPluginService}.
 */
@Plugin(type = Service.class)
public class DefaultReconstructionPluginService
             extends    AbstractPTService<ReconstructionPlugin>
             implements ReconstructionPluginService {
    /** {@inheritDoc} */
    public List<MainReconstructionPlugin> getMainPlugins()
    {
        ArrayList<MainReconstructionPlugin> result = new ArrayList<>();
        for (PluginInfo<ReconstructionPlugin> info : getPlugins()) {
            Class<? extends ReconstructionPlugin> cls;
            try {
                cls = info.loadClass();
            }
            catch (InstantiableException e) {
                throw new RuntimeException(e);
            }
            if (MainReconstructionPlugin.class.isAssignableFrom(cls)) {
                if (isEnabled(cls)) {
                    MainReconstructionPlugin plugin
                        = (MainReconstructionPlugin)
                        pluginService().createInstance(info);
                    result.add(plugin);
                }
            }
            else if (!SubReconstructionPlugin.class.isAssignableFrom(cls)) {
                throw new RuntimeException(cls.getName() + " must inherit from "
                    + "either MainReconstructionPlugin or "
                    + "SubReconstructionPlugin.");
            }
        }
        return result;
    }
    @SuppressWarnings("unchecked")  // This is for adding to result, see below
    /** {@inheritDoc} */
    public <T extends ReconstructionPlugin>
           List<T> getEnabledPlugins(Class<T> type)
    {
        ArrayList<T> result = new ArrayList<>();
        for (PluginInfo<ReconstructionPlugin> info : getPlugins()) {
            Class<? extends ReconstructionPlugin> cls;
            try {
                cls = info.loadClass();
            }
            catch (InstantiableException e) {
                throw new RuntimeException(e);
            }
            if (type.isAssignableFrom(cls)) {
                if (isEnabled(cls)) {
                    // This is actually safe because we already checked that we
                    // can assign
                    result.add((T)pluginService().createInstance(info));
                }
            }
        }
        return result;
    }
    @SuppressWarnings("unchecked")  // This is for adding to result, see below
    /** {@inheritDoc} */
    public<T extends ReconstructionPlugin> List<T> getAllPlugins(Class<T>type)
    {
        ArrayList<T> result = new ArrayList<>();
        for (PluginInfo<ReconstructionPlugin> info : getPlugins()) {
            Class<? extends ReconstructionPlugin> cls;
            try {
                cls = info.loadClass();
            }
            catch (InstantiableException e) {
                throw new RuntimeException(e);
            }
            if (type.isAssignableFrom(cls)) {
                // This is actually safe because we already checked that we can
                // assign
                result.add((T)pluginService().createInstance(info));
            }
        }
        return result;
    }
    /** {@inheritDoc} */
    @Override
    public boolean isEnabled(Class<? extends ReconstructionPlugin> plugin)
    {
        return P_prefs.getBoolean(PluginParameter.class,
                                  plugin.getName(),
                                  true);
    }
    /** {@inheritDoc} */
    @Override
    public void enable(Class<? extends ReconstructionPlugin> plugin)
    {
        P_prefs.put(PluginParameter.class, plugin.getName(), true);
    }
    /** {@inheritDoc} */
    @Override
    public void disable(Class<? extends ReconstructionPlugin> plugin)
    {
        P_prefs.put(PluginParameter.class, plugin.getName(), false);
    }
    @Override
    public Class<ReconstructionPlugin> getPluginType()
        {return ReconstructionPlugin.class;}

    @Parameter private PrefService P_prefs;
}
