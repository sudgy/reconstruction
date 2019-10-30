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

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

import org.scijava.plugin.Plugin;
import org.scijava.plugin.Parameter;

import edu.pdx.imagej.dynamic_parameters.HoldingParameter;
import edu.pdx.imagej.dynamic_parameters.ImageParameter;
import edu.pdx.imagej.dynamic_parameters.DParameter;

/** A Parameter that has all main reconstruction parameters.  You should
 * probably never need to use this, unless you are reimplementing the
 * reconstruction command for some reason.
 */
@Plugin(type = DParameter.class)
public class AllPluginsParameter extends HoldingParameter<
        List<MainReconstructionPlugin>
    > {
    @Parameter private ReconstructionPluginService P_pluginService;

    /** Normal constructor.
     *
     * @param hologram The parameter being used to select the hologram.
     */
    public AllPluginsParameter(ImageParameter hologram)
    {
        super("PluginParameters");
        M_hologram = hologram;
    }

    /** Adds all parameters, and calls {@link
     * HologramPluginParameter#setHologram setHologram} on all that need it.
     */
    @Override
    public void initialize()
    {
        M_plugins = P_pluginService.getMainPlugins();
        for (MainReconstructionPlugin plugin : M_plugins) {
            DParameter param = plugin.param();
            if (param != null) addParameter(param);
            if (param instanceof HologramPluginParameter) {
                ((HologramPluginParameter)param).setHologram(M_hologram);
            }
        }
    }

    /** Get all of the plugins.
     *
     * @return All of the plugins.
     */
    @Override
    public List<MainReconstructionPlugin> getValue()
    {
        return M_plugins;
    }

    List<MainReconstructionPlugin> M_plugins;
    ImageParameter M_hologram;
}
